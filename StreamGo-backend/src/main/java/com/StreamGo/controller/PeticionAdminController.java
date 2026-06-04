package com.StreamGo.controller;

import com.StreamGo.dto.request.ContenidoVotableRequest;
import com.StreamGo.dto.response.ContenidoVotableResponse;
import com.StreamGo.dto.response.VotoResponse;
import com.StreamGo.service.PeticionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/editar/{id}")
    public ResponseEntity<ContenidoVotableResponse> editar(
            @PathVariable Long id,
            @RequestBody ContenidoVotableRequest request
    ) {
        return ResponseEntity.ok(peticionService.editarVotable(id, request));
    }

    @PutMapping("/desactivar/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        peticionService.desactivarVotable(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<VotoResponse>> ranking() {
        return ResponseEntity.ok(peticionService.verRankingVotos());
    }
}

