package com.StreamGo.controller;

import com.StreamGo.dto.request.ContenidoVotableRequest;
import com.StreamGo.dto.response.ContenidoVotableResponse;
import com.StreamGo.dto.response.VotoResponse;
import com.StreamGo.service.PeticionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/peticiones")
@RequiredArgsConstructor
public class PeticionAdminController {

    private final PeticionService peticionService;

    @PostMapping("/agregar")
    public ResponseEntity<ContenidoVotableResponse> agregar(
            @RequestBody ContenidoVotableRequest request
    ) {
        return ResponseEntity.ok(peticionService.agregarVotable(request));
    }

    @PutMapping("/{id}/editar")
    public ResponseEntity<ContenidoVotableResponse> editar(
            @PathVariable("id") Long id,
            @Valid @RequestBody ContenidoVotableRequest request
    ) {
        return ResponseEntity.ok(peticionService.editarVotable(id, request));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable("id") Long id) {
        peticionService.desactivarVotable(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<VotoResponse>> ranking() {
        return ResponseEntity.ok(peticionService.verRankingVotos());
    }
    @GetMapping("/lista")
    public ResponseEntity<List<ContenidoVotableResponse>> listar() {
        return ResponseEntity.ok(peticionService.listarVotables());
    }
}
