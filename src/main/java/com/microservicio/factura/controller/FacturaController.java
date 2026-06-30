package com.microservicio.factura.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.microservicio.factura.model.Factura;
import com.microservicio.factura.service.FacturaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    // GENERAR FACTURA
    @PostMapping
    public ResponseEntity<?> generarFactura(
            @Valid @RequestBody Factura factura) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(facturaService.generarFactura(factura));
    }

    // LISTAR FACTURAS
    @GetMapping
    public ResponseEntity<?> listarFacturas() {

        return ResponseEntity.ok(
                facturaService.listarFacturas());
    }

    // OBTENER FACTURA POR ID
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> obtenerFacturaPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                facturaService.obtenerFacturaPorId(id));
    }

    // ELIMINAR FACTURA
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarFactura(
            @PathVariable Long id) {

        facturaService.eliminarFactura(id);

        return ResponseEntity.ok(
                "Factura eliminada correctamente");
    }

    // ENVIAR FACTURA POR EMAIL
    @PostMapping("/{id}/enviar-email")
    public ResponseEntity<?> enviarFacturaEmail(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                facturaService.enviarFacturaPorEmail(id));
    }
}

