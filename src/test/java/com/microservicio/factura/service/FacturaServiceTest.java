package com.microservicio.factura.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.client.RestTemplate;

import com.microservicio.factura.dto.ClienteDTO;
import com.microservicio.factura.dto.EmpleadoDTO;
import com.microservicio.factura.dto.PedidoDTO;
import com.microservicio.factura.dto.ProductoPedidoDTO;
import com.microservicio.factura.model.Factura;
import com.microservicio.factura.model.ProductoFactura;
import com.microservicio.factura.repository.FacturaRepository;

@ExtendWith(MockitoExtension.class)
class FacturaServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FacturaService facturaService;

    private Factura factura;
    private PedidoDTO pedido;
    private ClienteDTO cliente;
    private EmpleadoDTO empleado;

    @BeforeEach
    void setUp() {

        factura = new Factura();
        factura.setIdFactura(1L);
        factura.setIdEmpleado(2L);
        factura.setIdPedido(10L);

        ProductoPedidoDTO producto =
                new ProductoPedidoDTO();

        producto.setIdProducto(1L);
        producto.setNombProducto("Notebook");
        producto.setPrecio(500000.0);
        producto.setCantidad(2);

        pedido = new PedidoDTO();
        pedido.setIdPedido(10L);
        pedido.setIdCliente(5L);
        pedido.setProductos(List.of(producto));

        cliente = new ClienteDTO();
        cliente.setIdCliente(5L);
        cliente.setNombre("Juan Pérez");
        cliente.setRut("11.111.111-1");
        cliente.setEmail("juan@gmail.com");

        empleado = new EmpleadoDTO();
        empleado.setIdEmpleado(2L);
    }

    @Test
    void generarFacturaDebeGuardarCorrectamente() {

        when(restTemplate.getForObject(
                "http://localhost:8081/api/v1/empleados/buscar/2",
                EmpleadoDTO.class))
                .thenReturn(empleado);

        when(restTemplate.getForObject(
                "http://localhost:8087/api/v1/pedidos/buscar/10",
                PedidoDTO.class))
                .thenReturn(pedido);

        when(restTemplate.getForObject(
                "http://localhost:8081/api/v1/clientes/buscar/5",
                ClienteDTO.class))
                .thenReturn(cliente);

        when(facturaRepository.save(any(Factura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Factura resultado =
                facturaService.generarFactura(factura);

        assertNotNull(resultado);

        assertEquals(
                "Juan Pérez",
                resultado.getNombreCliente());

        assertEquals(
                "11.111.111-1",
                resultado.getRutCliente());

        assertEquals(
                "juan@gmail.com",
                resultado.getEmailCliente());

        assertEquals(
                1000000.0,
                resultado.getTotalFinal());

        assertEquals(
                840336.13,
                resultado.getSubtotal(),
                0.01);

        assertEquals(
                159663.87,
                resultado.getIva(),
                0.01);

        assertEquals(
                1,
                resultado.getProductos().size());

        verify(facturaRepository)
                .save(any(Factura.class));
    }

    @Test
    void generarFacturaDebeLanzarExcepcionSiEmpleadoNoExiste() {

        when(restTemplate.getForObject(
                anyString(),
                eq(EmpleadoDTO.class)))
                .thenReturn(null);

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> facturaService.generarFactura(factura));

        assertEquals(
                "Empleado no encontrado",
                ex.getMessage());
    }

    @Test
    void generarFacturaDebeLanzarExcepcionSiPedidoNoExiste() {

        when(restTemplate.getForObject(
                anyString(),
                eq(EmpleadoDTO.class)))
                .thenReturn(empleado);

        when(restTemplate.getForObject(
                anyString(),
                eq(PedidoDTO.class)))
                .thenReturn(null);

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> facturaService.generarFactura(factura));

        assertEquals(
                "Pedido no encontrado",
                ex.getMessage());
    }

    @Test
    void generarFacturaDebeLanzarExcepcionSiClienteNoExiste() {

        when(restTemplate.getForObject(
                anyString(),
                eq(EmpleadoDTO.class)))
                .thenReturn(empleado);

        when(restTemplate.getForObject(
                anyString(),
                eq(PedidoDTO.class)))
                .thenReturn(pedido);

        when(restTemplate.getForObject(
                anyString(),
                eq(ClienteDTO.class)))
                .thenReturn(null);

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> facturaService.generarFactura(factura));

        assertEquals(
                "Cliente no encontrado",
                ex.getMessage());
    }

    @Test
    void listarFacturasDebeRetornarLista() {

        when(facturaRepository.findAll())
                .thenReturn(List.of(factura));

        List<Factura> resultado =
                facturaService.listarFacturas();

        assertEquals(1, resultado.size());

        verify(facturaRepository)
                .findAll();
    }

    @Test
    void obtenerFacturaPorIdDebeRetornarFactura() {

        when(facturaRepository.findById(1L))
                .thenReturn(Optional.of(factura));

        Factura resultado =
                facturaService.obtenerFacturaPorId(1L);

        assertEquals(
                1L,
                resultado.getIdFactura());

        verify(facturaRepository)
                .findById(1L);
    }

    @Test
    void obtenerFacturaPorIdDebeLanzarExcepcion() {

        when(facturaRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> facturaService.obtenerFacturaPorId(1L));

        assertEquals(
                "Factura no encontrada",
                ex.getMessage());
    }

    @Test
    void eliminarFacturaDebeEliminarCorrectamente() {

        when(facturaRepository.findById(1L))
                .thenReturn(Optional.of(factura));

        doNothing()
                .when(facturaRepository)
                .delete(factura);

        facturaService.eliminarFactura(1L);

        verify(facturaRepository)
                .delete(factura);
    }

    @Test
    void enviarFacturaPorEmailDebeRetornarMensajeCorrecto() {

        ProductoFactura producto =
                new ProductoFactura();

        producto.setIdProducto(1L);
        producto.setNombProducto("Notebook");
        producto.setPrecio(500000.0);
        producto.setCantidad(2);

        factura.setNombreCliente("Juan Pérez");
        factura.setRutCliente("11.111.111-1");
        factura.setEmailCliente("juan@gmail.com");

        factura.setProductos(List.of(producto));

        factura.setSubtotal(840336.13);
        factura.setIva(159663.87);
        factura.setTotalFinal(1000000.0);

        when(facturaRepository.findById(1L))
                .thenReturn(Optional.of(factura));

        String resultado =
                facturaService.enviarFacturaPorEmail(1L);

        assertEquals(
                "Factura enviada exitosamente al correo juan@gmail.com",
                resultado);
    }
}