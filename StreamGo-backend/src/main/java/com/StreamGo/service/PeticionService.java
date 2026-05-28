package com.StreamGo.service;

import com.StreamGo.entity.Peticion;
import com.StreamGo.repository.PeticionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PeticionService {

    private final PeticionRepository repository;

    // LISTAR TODAS
    public List<Peticion> listarTodas() {
        return repository.findAll();
    }

    // OBTENER POR ID
    public Peticion obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow();
    }

    // GUARDAR
    public Peticion guardar(Peticion peticion) {
        return repository.save(peticion);
    }

    // ELIMINAR
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}