package com.microservicio.factura.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.microservicio.factura.dto.ClienteDTO;
import com.microservicio.factura.dto.EmpleadoDTO;
import com.microservicio.factura.dto.PedidoDTO;
import com.microservicio.factura.dto.ProductoPedidoDTO;
import com.microservicio.factura.model.Factura;
import com.microservicio.factura.model.ProductoFactura;
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

    // ==========================
    // VALIDAR EMPLEADO
    // ==========================
    String urlEmpleado =
            "http://localhost:8081/api/v1/empleados/buscar/"
            + factura.getIdEmpleado();

    EmpleadoDTO empleado =
            restTemplate.getForObject(
                    urlEmpleado,
                    EmpleadoDTO.class);

    if (empleado == null) {
        throw new RuntimeException(
                "Empleado no encontrado");
    }

    // ==========================
    // OBTENER PEDIDO
    // ==========================
    String urlPedido =
            "http://localhost:8087/api/v1/pedidos/buscar/"
            + factura.getIdPedido();

    PedidoDTO pedido =
            restTemplate.getForObject(
                    urlPedido,
                    PedidoDTO.class);

    if (pedido == null) {
        throw new RuntimeException(
                "Pedido no encontrado");
    }

    // ==========================
    // OBTENER CLIENTE
    // ==========================
    String urlCliente =
            "http://localhost:8081/api/v1/clientes/buscar/"
            + pedido.getIdCliente();

    ClienteDTO cliente =
            restTemplate.getForObject(
                    urlCliente,
                    ClienteDTO.class);

    if (cliente == null) {
        throw new RuntimeException(
                "Cliente no encontrado");
    }

    // ==========================
    // CREAR DETALLE DE FACTURA
    // ==========================
    List<ProductoFactura> productosFactura =
            new ArrayList<>();

    double totalFinal = 0.0;

    for (ProductoPedidoDTO producto : pedido.getProductos()) {

        ProductoFactura detalle =
                new ProductoFactura();

        detalle.setIdProducto(
                producto.getIdProducto());

        detalle.setNombProducto(
                producto.getNombProducto());

        detalle.setPrecio(
                producto.getPrecio());

        detalle.setCantidad(
                producto.getCantidad());

        productosFactura.add(detalle);

        totalFinal +=
                producto.getPrecio()
                * producto.getCantidad();
        }

        // ==========================
        // CALCULAR TOTALES
        // ==========================
        double subtotal = totalFinal / 1.19;
        double iva = totalFinal - subtotal;

        // ==========================
        // DATOS DEL CLIENTE
        // ==========================
        factura.setNombreCliente(
                cliente.getNombre());

        factura.setRutCliente(
                cliente.getRut());

        factura.setEmailCliente(
                cliente.getEmail());

        // ==========================
        // PRODUCTOS
        // ==========================
        factura.setProductos(
                productosFactura);

        // ==========================
        // TOTALES
        // ==========================
        factura.setSubtotal(subtotal);
        factura.setIva(iva);
        factura.setTotalFinal(totalFinal);

        // ==========================
        // FECHA
        // ==========================
        factura.setFechaEmision(
                LocalDate.now());
        

        // ==========================
        // GUARDAR
        // ==========================
        return facturaRepository.save(
                factura);
    }

    public List<Factura> listarFacturas() {

        return facturaRepository.findAll();
    }

    public Factura obtenerFacturaPorId(
            Long idFactura) {

        return facturaRepository.findById(idFactura)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Factura no encontrada"));
    }

    public void eliminarFactura(
            Long idFactura) {

        Factura factura =
                obtenerFacturaPorId(idFactura);

        facturaRepository.delete(factura);
    }

    public String enviarFacturaPorEmail(
            Long idFactura) {

        Factura factura =
                obtenerFacturaPorId(idFactura);

        StringBuilder correo =
                new StringBuilder();

        correo.append("FACTURA N° ")
                .append(factura.getIdFactura())
                .append("\n\n");

        correo.append("Cliente: ")
                .append(factura.getNombreCliente())
                .append("\n");

        correo.append("RUT: ")
                .append(factura.getRutCliente())
                .append("\n");
        correo.append("Email: ")
                .append(factura.getEmailCliente())
                .append("\n\n");

        correo.append("Productos:\n");

        for (ProductoFactura producto :
                factura.getProductos()) {

            double totalProducto =
                    producto.getPrecio()
                    * producto.getCantidad();

            correo.append("- ")
                    .append(producto.getNombProducto())
                    .append(" x")
                    .append(producto.getCantidad())
                    .append(" : $")
                    .append(totalProducto)
                    .append("\n");
        }

        correo.append("\n");

        correo.append("Subtotal: $")
                .append(factura.getSubtotal())
                .append("\n");

        correo.append("IVA: $")
                .append(factura.getIva())
                .append("\n");

        correo.append("Total Final: $")
                .append(factura.getTotalFinal());

        System.out.println("=================================");
        System.out.println("CORREO ENVIADO EXITOSAMENTE");
        System.out.println("Destinatario: "
                + factura.getEmailCliente());
        System.out.println(correo);
        System.out.println("=================================");

        return "Factura enviada exitosamente al correo "
                + factura.getEmailCliente();
    }
}
