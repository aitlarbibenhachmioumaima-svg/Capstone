package com.example.service;

import com.example.model.Utilisateur;
import com.example.repository.UtilisateurRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

public class UtilisateurService {

    private UtilisateurRepository repository;
    private EntityManager em;

    public UtilisateurService(EntityManager em) {
        this.em = em;
        this.repository = new UtilisateurRepository(em);
    }

    public void addUtilisateur(Utilisateur u) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        repository.save(u);
        tx.commit();
    }

    public List<Utilisateur> getAll() {
        return repository.findAll();
    }
}