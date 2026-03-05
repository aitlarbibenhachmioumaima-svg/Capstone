package com.example.repository;

import com.example.model.Salle;

import javax.persistence.EntityManager;
import java.util.List;

public class SalleRepositoryImpl implements SalleRepository {

    private final EntityManager em;

    public SalleRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Salle salle) {
        em.persist(salle);
    }

    @Override
    public Salle findById(Long id) {
        return em.find(Salle.class, id);
    }

    @Override
    public List<Salle> findAll() {
        return em.createQuery("SELECT s FROM Salle s", Salle.class).getResultList();
    }

    @Override
    public List<Salle> searchRooms(String batiment, Integer etage, Integer capaciteMin, Integer capaciteMax) {
        String ql = "SELECT s FROM Salle s WHERE 1=1 ";
        if (batiment != null) ql += "AND s.batiment = :batiment ";
        if (etage != null) ql += "AND s.etage = :etage ";
        if (capaciteMin != null) ql += "AND s.capacite >= :capaciteMin ";
        if (capaciteMax != null) ql += "AND s.capacite <= :capaciteMax ";

        var query = em.createQuery(ql, Salle.class);
        if (batiment != null) query.setParameter("batiment", batiment);
        if (etage != null) query.setParameter("etage", etage);
        if (capaciteMin != null) query.setParameter("capaciteMin", capaciteMin);
        if (capaciteMax != null) query.setParameter("capaciteMax", capaciteMax);

        return query.getResultList();
    }
}