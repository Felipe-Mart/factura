package com.microservicio.factura.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

    @PostMapping
    public ResponseEntity<EntityModel<Factura>>
    generarFactura(
            @Valid @RequestBody Factura factura) {

        Factura nueva =
                facturaService.generarFactura(factura);

        EntityModel<Factura> recurso =
                EntityModel.of(nueva);

        recurso.add(linkTo(
                methodOn(FacturaController.class)
                        .obtenerFacturaPorId(
                                nueva.getIdFactura()))
                .withSelfRel());

        recurso.add(linkTo(
                methodOn(FacturaController.class)
                        .listarFacturas())
                .withRel("facturas"));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(recurso);
    }

    @GetMapping
    public CollectionModel<EntityModel<Factura>>
    listarFacturas() {

        List<EntityModel<Factura>> facturas =
                facturaService.listarFacturas()
                        .stream()
                        .map(f -> EntityModel.of(
                                f,
                                linkTo(methodOn(
                                        FacturaController.class)
                                        .obtenerFacturaPorId(
                                                f.getIdFactura()))
                                        .withSelfRel()))
                        .collect(Collectors.toList());

        return CollectionModel.of(
                facturas,
                linkTo(methodOn(
                        FacturaController.class)
                        .listarFacturas())
                        .withSelfRel());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<EntityModel<Factura>>
    obtenerFacturaPorId(
            @PathVariable Long id) {

        Factura factura =
                facturaService.obtenerFacturaPorId(id);

        EntityModel<Factura> recurso =
                EntityModel.of(factura);

        recurso.add(linkTo(
                methodOn(FacturaController.class)
                        .obtenerFacturaPorId(id))
                .withSelfRel());

        recurso.add(linkTo(
                methodOn(FacturaController.class)
                        .listarFacturas())
                .withRel("facturas"));

        recurso.add(linkTo(
                methodOn(FacturaController.class)
                        .enviarFacturaEmail(id))
                .withRel("enviar-email"));

        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String>
    eliminarFactura(
            @PathVariable Long id) {

        facturaService.eliminarFactura(id);

        return ResponseEntity.ok(
                "Factura eliminada correctamente");
    }

    @PostMapping("/{id}/enviar-email")
    public ResponseEntity<String>
    enviarFacturaEmail(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                facturaService.enviarFacturaPorEmail(id));
    }
}