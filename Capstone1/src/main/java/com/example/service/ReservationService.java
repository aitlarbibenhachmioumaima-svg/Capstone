package com.example.service;

import com.example.model.Reservation;
import java.util.List;

public interface ReservationService {
    void addReservation(Reservation r);
    void deleteReservation(Long id);
    List<Reservation> getAll();
    Reservation findById(Long id);
}