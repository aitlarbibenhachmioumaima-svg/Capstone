package com.example.service;

import com.example.model.Reservation;
import com.example.repository.ReservationRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

public class ReservationServiceImpl implements ReservationService {

    private final EntityManager em;
    private final ReservationRepository repository;

    public ReservationServiceImpl(EntityManager em, ReservationRepository repository) {
        this.em = em;
        this.repository = repository;
    }

    @Override
    public void addReservation(Reservation r) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        repository.save(r);
        tx.commit();
    }

    @Override
    public void deleteReservation(Long id) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Reservation r = repository.findById(id);
        if (r != null) {
            em.remove(em.contains(r) ? r : em.merge(r));
        }
        tx.commit();
    }

    @Override
    public List<Reservation> getAll() {
        return repository.findAll();
    }

    @Override
    public Reservation findById(Long id) {
        return repository.findById(id);
    }
}