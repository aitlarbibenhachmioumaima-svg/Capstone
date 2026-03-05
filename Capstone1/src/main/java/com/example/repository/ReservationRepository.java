package com.example.repository;

import com.example.model.Reservation;
import java.util.List;

public interface ReservationRepository {
    void save(Reservation reservation);
    List<Reservation> findAll();
    Reservation findById(Long id);
}