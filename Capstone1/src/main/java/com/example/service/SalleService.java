package com.example.service;

import com.example.model.Salle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SalleService {
    void addSalle(Salle s);
    List<Salle> getAll();

    List<Salle> findAvailableRooms(LocalDateTime start, LocalDateTime end);
    List<Salle> searchRooms(Map<String, Object> criteres);
    int getTotalPages(int pageSize);
    List<Salle> getPaginatedRooms(int page, int pageSize);
    long countRooms();
}