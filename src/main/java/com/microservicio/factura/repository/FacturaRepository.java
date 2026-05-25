package com.microservicio.factura.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservicio.factura.model.Factura;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
}
