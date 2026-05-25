package com.microservicio.factura.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.microservicio.factura.dto.ProductoDTO;
import com.microservicio.factura.model.Factura;
import com.microservicio.factura.repository.FacturaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FacturaService {
	@Autowired
	private FacturaRepository facturaRepository;
    private RestTemplate restTemplate;


    public Factura generarFactura(Factura factura) {

        String url = "http://localhost:8082/api/v1/productos/"+ factura.getIdProducto();

        ProductoDTO productoDTO =restTemplate.getForObject(url,ProductoDTO.class);

        if (productoDTO != null) {

            factura.setSubtotal(productoDTO.getSubtotal());
            factura.setIva(productoDTO.getIva());
            factura.setTotal(productoDTO.getTotal());
        }

        factura.setFechaEmision(LocalDate.now());

        return facturaRepository.save(factura);
    }

    public List<Factura> listarFacturas() {
        return facturaRepository.findAll();
    }

    public Factura modificarFactura(Long id,Factura nuevaFactura) {

        Factura factura =facturaRepository.findById(id).orElse(null);

        if (factura != null) {

            factura.setIdCliente(nuevaFactura.getIdCliente());
            factura.setIdEmpleado(nuevaFactura.getIdEmpleado());
            factura.setIdProducto(nuevaFactura.getIdProducto());
            factura.setSubtotal(nuevaFactura.getSubtotal());
            factura.setIva(nuevaFactura.getIva());
            factura.setTotal(nuevaFactura.getTotal());

            return facturaRepository.save(factura);
        }

        return null;
    }

    public Factura buscarFactura(Long id) {
        return facturaRepository.findById(id).orElse(null);
    }

    public String enviarFacturaEmail(Long idFactura) {

        Factura factura =facturaRepository.findById(idFactura).orElse(null);

        if (factura != null) {
            return "Factura enviada correctamente";
        }

        return "Factura no encontrada";
    }  
}
