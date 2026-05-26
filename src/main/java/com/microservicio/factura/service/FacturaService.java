package com.microservicio.factura.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.microservicio.factura.dto.ClienteDTO;
import com.microservicio.factura.dto.EmpleadoDTO;
import com.microservicio.factura.dto.ProductoDTO;
import com.microservicio.factura.model.Factura;
import com.microservicio.factura.repository.FacturaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FacturaService {
	@Autowired
	private FacturaRepository facturaRepository;
    @Autowired
    private RestTemplate restTemplate;
   

    public Factura generarFactura(Factura factura) {

        String urlCliente = "http://localhost:8081/api/clientes/" + factura.getIdCliente();

        ClienteDTO cliente = restTemplate.getForObject(urlCliente, ClienteDTO.class);

        String urlProducto = "http://localhost:8082/api/productos/" + factura.getIdProducto();

        ProductoDTO producto = restTemplate.getForObject(urlProducto,ProductoDTO.class);

        String urlEmpleado = "http://localhost:8083/api/empleados/"+ factura.getIdEmpleado();

        EmpleadoDTO empleado =restTemplate.getForObject(urlEmpleado,EmpleadoDTO.class);

        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }

        if (producto == null) {
            throw new RuntimeException("Producto no encontrado");
        }

        if (empleado == null) {
            throw new RuntimeException("Empleado no encontrado");
        }

        factura.setSubtotal(producto.getSubtotal());
        factura.setIva(producto.getIva());
        factura.setTotal(producto.getTotal());
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
