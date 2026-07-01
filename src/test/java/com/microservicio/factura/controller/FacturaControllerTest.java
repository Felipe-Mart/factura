package com.microservicio.factura.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.factura.model.Factura;
import com.microservicio.factura.service.FacturaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FacturaController.class)
class FacturaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacturaService facturaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Factura factura;

    @BeforeEach
    void setUp() {

        factura = new Factura();

        factura.setIdFactura(1L);
        factura.setIdEmpleado(2L);
        factura.setIdPedido(10L);

        factura.setNombreCliente("Juan Pérez");
        factura.setRutCliente("11.111.111-1");
        factura.setEmailCliente("juan@gmail.com");
    }

    @Test
    void generarFacturaDebeRetornar201() throws Exception {

        when(facturaService.generarFactura(factura))
                .thenReturn(factura);

        mockMvc.perform(
                post("/api/v1/facturas")
                        .with(user("admin"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(factura)))

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.idFactura").value(1))

                .andExpect(jsonPath("$._links.self").exists())

                .andExpect(jsonPath("$._links.facturas").exists());
    }

    @Test
    void listarFacturasDebeRetornar200() throws Exception {

        when(facturaService.listarFacturas())
                .thenReturn(List.of(factura));

        mockMvc.perform(
                get("/api/v1/facturas")
                        .with(user("admin")))

                .andExpect(status().isOk())

                .andExpect(
                        jsonPath(
                                "$._embedded.facturaList[0].idFactura")
                                .value(1))

                .andExpect(
                        jsonPath("$._links.self")
                                .exists());
    }

    @Test
    void obtenerFacturaPorIdDebeRetornar200() throws Exception {

        when(facturaService.obtenerFacturaPorId(1L))
                .thenReturn(factura);

        mockMvc.perform(
                get("/api/v1/facturas/buscar/1")
                        .with(user("admin")))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.idFactura").value(1))

                .andExpect(jsonPath("$._links.self").exists())

                .andExpect(jsonPath("$._links.facturas").exists())

                .andExpect(
                        jsonPath("$._links.enviar-email")
                                .exists());
    }

    @Test
    void eliminarFacturaDebeRetornar200() throws Exception {

        mockMvc.perform(
                delete("/api/v1/facturas/eliminar/1")
                        .with(user("admin"))
                        .with(csrf()))

                .andExpect(status().isOk())

                .andExpect(content().string(
                        "Factura eliminada correctamente"));
    }

    @Test
    void enviarFacturaEmailDebeRetornar200() throws Exception {

        when(facturaService.enviarFacturaPorEmail(1L))
                .thenReturn(
                        "Factura enviada exitosamente al correo juan@gmail.com");

        mockMvc.perform(
                post("/api/v1/facturas/1/enviar-email")
                        .with(user("admin"))
                        .with(csrf()))

                .andExpect(status().isOk())

                .andExpect(content().string(
                        "Factura enviada exitosamente al correo juan@gmail.com"));
    }
}