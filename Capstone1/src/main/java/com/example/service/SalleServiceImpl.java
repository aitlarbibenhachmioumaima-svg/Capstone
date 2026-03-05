package com.example.service;

import com.example.model.Salle;
import com.example.repository.SalleRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SalleServiceImpl implements SalleService {

    private final EntityManager em;
    private final SalleRepository repository;

    public SalleServiceImpl(EntityManager em, SalleRepository repository) {
        this.em = em;
        this.repository = repository;
    }

    @Override
    public void addSalle(Salle s) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        repository.save(s);
        tx.commit();
    }

    @Override
    public List<Salle> getAll() {
        return repository.findAll();
    }

    @Override
    public List<Salle> findAvailableRooms(LocalDateTime start, LocalDateTime end) {
        // Version simple (retourne toutes les salles)
        return repository.findAll();
    }

    @Override
    public List<Salle> searchRooms(Map<String, Object> criteres) {
        String batiment = (String) criteres.get("batiment");
        Integer etage = (Integer) criteres.get("etage");
        Integer capaciteMin = (Integer) criteres.get("capaciteMin");
        Integer capaciteMax = (Integer) criteres.get("capaciteMax");

        return repository.searchRooms(batiment, etage, capaciteMin, capaciteMax);
    }

    @Override
    public int getTotalPages(int pageSize) {
        int total = repository.findAll().size();
        return (total + pageSize - 1) / pageSize;
    }

    @Override
    public List<Salle> getPaginatedRooms(int page, int pageSize) {
        List<Salle> all = repository.findAll();
        int from = (page - 1) * pageSize;

        if (from >= all.size()) {
            return List.of();
        }

        int to = Math.min(from + pageSize, all.size());
        return all.subList(from, to);
    }

    @Override
    public long countRooms() {
        return repository.findAll().size();
    }
}