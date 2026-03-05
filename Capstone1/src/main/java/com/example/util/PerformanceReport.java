package com.example.util;

import com.example.model.Salle;
import com.example.model.Reservation;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PerformanceReport {

    private final EntityManagerFactory emf;
    private final Map<String, TestResult> results = new HashMap<>();

    public PerformanceReport(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void runPerformanceTests() {
        System.out.println("Exécution des tests de performance...");

        // Réinitialiser les statistiques Hibernate
        resetStatistics();

        // Test 1: Recherche de salles disponibles
        testPerformance("Recherche de salles disponibles", () -> {
            EntityManager em = emf.createEntityManager();
            try {
                LocalDateTime start = LocalDateTime.now().plusDays(1);
                LocalDateTime end = start.plusHours(2);

                return em.createQuery(
                                "SELECT DISTINCT s FROM Salle s WHERE s.id NOT IN " +
                                        "(SELECT r.salle.id FROM Reservation r " +
                                        "WHERE r.dateDebut <= :end AND r.dateFin >= :start)", Salle.class)
                        .setParameter("start", start)
                        .setParameter("end", end)
                        .getResultList();
            } finally {
                em.close();
            }
        });

        // Test 2: Recherche multi-critères
        testPerformance("Recherche multi-critères", () -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery(
                                "SELECT DISTINCT s FROM Salle s LEFT JOIN s.equipements e " +
                                        "WHERE s.capacite >= :capacite AND s.batiment = :batiment AND e.id = :equipementId", Salle.class)
                        .setParameter("capacite", 30)
                        .setParameter("batiment", "Bâtiment B")
                        .setParameter("equipementId", 1L)
                        .getResultList();
            } finally {
                em.close();
            }
        });

        // Test 3: Pagination
        testPerformance("Pagination", () -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery("SELECT s FROM Salle s ORDER BY s.id", Salle.class)
                        .setFirstResult(0)
                        .setMaxResults(10)
                        .getResultList();
            } finally {
                em.close();
            }
        });

        // Test 4: Accès répété avec cache
        testPerformance("Accès répété avec cache", () -> {
            Salle result = null;
            for (int i = 0; i < 100; i++) {
                EntityManager em = emf.createEntityManager();
                try {
                    result = em.find(Salle.class, 1L);
                } finally {
                    em.close();
                }
            }
            return result;
        });

        // Test 5: Requête avec JOIN FETCH
        testPerformance("Requête avec JOIN FETCH", () -> {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery(
                                "SELECT DISTINCT s FROM Salle s LEFT JOIN FETCH s.equipements WHERE s.capacite > 20", Salle.class)
                        .getResultList();
            } finally {
                em.close();
            }
        });

        // Générer le rapport final
        generateReport();
    }

    private void testPerformance(String testName, Supplier<?> testFunction) {
        System.out.println("Exécution du test: " + testName);

        // Réinitialiser les statistiques avant le test
        resetStatistics();

        long startTime = System.currentTimeMillis();
        Object result = testFunction.get();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Récupérer les statistiques Hibernate
        EntityManager em = emf.createEntityManager();
        try {
            Session session = em.unwrap(Session.class);
            Statistics stats = session.getSessionFactory().getStatistics();

            TestResult testResult = new TestResult();
            testResult.executionTime = executionTime;
            testResult.queryCount = stats.getQueryExecutionCount();
            testResult.entityLoadCount = stats.getEntityLoadCount();
            testResult.cacheHitCount = stats.getSecondLevelCacheHitCount();
            testResult.cacheMissCount = stats.getSecondLevelCacheMissCount();
            testResult.resultSize = (result instanceof Collection) ?
                    ((Collection<?>) result).size() : (result != null ? 1 : 0);

            results.put(testName, testResult);
        } finally {
            em.close();
        }

        System.out.println("Test terminé: " + testName + " en " + executionTime + "ms");
    }

    private void resetStatistics() {
        EntityManager em = emf.createEntityManager();
        try {
            Session session = em.unwrap(Session.class);
            session.getSessionFactory().getStatistics().clear();
        } finally {
            em.close();
        }
    }

    private void generateReport() {
        System.out.println("Génération du rapport de performance...");

        String fileName = "performance_report_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("=== RAPPORT DE PERFORMANCE ===");
            writer.println("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("=================================\n");

            for (Map.Entry<String, TestResult> entry : results.entrySet()) {
                TestResult r = entry.getValue();
                writer.println("Test: " + entry.getKey());
                writer.println("Temps d'exécution: " + r.executionTime + "ms");
                writer.println("Nombre de requêtes: " + r.queryCount);
                writer.println("Entités chargées: " + r.entityLoadCount);
                writer.println("Hits du cache: " + r.cacheHitCount);
                writer.println("Miss du cache: " + r.cacheMissCount);
                writer.println("Taille du résultat: " + r.resultSize);

                long totalCacheAccess = r.cacheHitCount + r.cacheMissCount;
                double cacheHitRatio = totalCacheAccess > 0 ?
                        (double) r.cacheHitCount / totalCacheAccess : 0;
                writer.println("Ratio de hit du cache: " + String.format("%.2f", cacheHitRatio * 100) + "%");

                writer.println("----------------------------------\n");
            }

            System.out.println("Rapport généré: " + fileName);

        } catch (IOException e) {
            System.err.println("Erreur lors de la génération du rapport: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class TestResult {
        long executionTime;
        long queryCount;
        long entityLoadCount;
        long cacheHitCount;
        long cacheMissCount;
        int resultSize;
    }
}