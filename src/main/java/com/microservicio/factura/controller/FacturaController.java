package com.microservicio.factura.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.factura.model.Factura;
import com.microservicio.factura.service.FacturaService;

@RestController
@RequestMapping("/api/v1/facturas")
public class FacturaController {
    @Autowired
    private FacturaService facturaService;

    @PostMapping
    public Factura postFactura(
            @RequestBody Factura factura) {

        return facturaService.generarFactura(factura);
    }

    @GetMapping
    public List<Factura> getFacturas() {
        return facturaService.listarFacturas();
    }

    @PutMapping("/{id}")
    public Factura putFactura(@PathVariable Long id,@RequestBody Factura factura) {
        return facturaService.modificarFactura(id, factura);
    }

    @GetMapping("/{id}")
    public Factura getFacturaId(@PathVariable Long id) {
        return facturaService.buscarFactura(id);
    }

    @PostMapping("/enviar-email/{id}")
    public String enviarFacturaEmail(@PathVariable Long id) {
        return facturaService.enviarFacturaEmail(id);
    }
}
