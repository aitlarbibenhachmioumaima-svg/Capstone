package com.example.repository;

import com.example.model.Equipement;
import javax.persistence.EntityManager;
import java.util.List;

public class EquipementRepository {

    private EntityManager em;

    public EquipementRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Equipement e) {
        em.persist(e);
    }

    public List<Equipement> findAll() {
        return em.createQuery("from Equipement", Equipement.class)
                .getResultList();
    }
}