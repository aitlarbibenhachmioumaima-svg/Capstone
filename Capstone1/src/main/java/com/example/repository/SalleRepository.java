package com.example.repository;

import com.example.model.Salle;
import java.util.List;

public interface SalleRepository {
    void save(Salle salle);
    Salle findById(Long id);
    List<Salle> findAll();

    // Recherche multi-critères (optionnelle)
    List<Salle> searchRooms(String batiment, Integer etage, Integer capaciteMin, Integer capaciteMax);
}