package com.example.datn.Controller;


import com.example.datn.DTO.IMEI.ImeiDTO;

import com.example.datn.Model.IMEI;
import com.example.datn.Service.ImeiService;
import com.example.datn.Service.ServiceCRUDSanPham;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/imeis")
@RequiredArgsConstructor
public class ImeiController {

    private final ImeiService imeiService;

    @GetMapping
    public ResponseEntity<List<ImeiDTO>> getAllImeis() {
        return ResponseEntity.ok(imeiService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImeiDTO> getImeiById(@PathVariable Integer id) {
        return ResponseEntity.ok(imeiService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ImeiDTO> createImei(@RequestBody ImeiDTO dto) {
        IMEI createdImei = imeiService.create(dto);
        ImeiDTO createdDto = imeiService.getById(createdImei.getId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdImei.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImeiDTO> updateImei(@PathVariable Integer id, @RequestBody ImeiDTO dto) {
        IMEI updatedImei = imeiService.update(id, dto);
        ImeiDTO updatedDto = imeiService.getById(updatedImei.getId());
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImei(@PathVariable Integer id) {
        imeiService.delete(id);
        return ResponseEntity.noContent().build();
    }




}