package com.example.repository;

import com.example.model.Utilisateur;
import javax.persistence.EntityManager;
import java.util.List;

public class UtilisateurRepository {

    private EntityManager em;

    public UtilisateurRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Utilisateur u) {
        em.persist(u);
    }

    public Utilisateur findById(Long id) {
        return em.find(Utilisateur.class, id);
    }

    public List<Utilisateur> findAll() {
        return em.createQuery("from Utilisateur", Utilisateur.class)
                .getResultList();
    }

    public void delete(Utilisateur u) {
        em.remove(u);
    }
}