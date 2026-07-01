package com.microservicio.factura.controller;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.microservicio.factura.model.Factura;
import com.microservicio.factura.repository.FacturaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class FacturaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FacturaRepository facturaRepository;

    private Factura factura;

    @BeforeEach
    void setUp() {

        facturaRepository.deleteAll();

        factura = new Factura();

        factura.setIdEmpleado(2L);
        factura.setIdPedido(10L);

        factura.setNombreCliente("Juan Pérez");
        factura.setRutCliente("11.111.111-1");
        factura.setEmailCliente("juan@gmail.com");

        factura.setSubtotal(1000.0);
        factura.setIva(190.0);
        factura.setTotalFinal(1190.0);

        factura.setFechaEmision(LocalDate.now());

        factura = facturaRepository.save(factura);
    }

    @Test
    void obtenerFacturaPorIdDebeRetornar200() throws Exception {

        mockMvc.perform(
                get("/api/v1/facturas/buscar/"
                        + factura.getIdFactura())
                        .with(user("admin")))

                .andExpect(status().isOk())

                .andExpect(
                        jsonPath("$.idFactura")
                                .value(factura.getIdFactura()))

                .andExpect(
                        jsonPath("$._links.self")
                                .exists())

                .andExpect(
                        jsonPath("$._links.facturas")
                                .exists())

                .andExpect(
                        jsonPath("$._links.enviar-email")
                                .exists());
    }

    @Test
    void listarFacturasDebeRetornar200() throws Exception {

        mockMvc.perform(
                get("/api/v1/facturas")
                        .with(user("admin")))

                .andExpect(status().isOk())

                .andExpect(
                        jsonPath(
                                "$._embedded.facturaList[0].idFactura")
                                .value(factura.getIdFactura()))

                .andExpect(
                        jsonPath("$._links.self")
                                .exists());
    }

    @Test
    void eliminarFacturaDebeEliminarCorrectamente()
            throws Exception {

        mockMvc.perform(
                delete("/api/v1/facturas/eliminar/"
                        + factura.getIdFactura())
                        .with(user("admin"))
                        .with(csrf()))

                .andExpect(status().isOk())

                .andExpect(content().string(
                        "Factura eliminada correctamente"));

        Optional<Factura> resultado =
                facturaRepository.findById(
                        factura.getIdFactura());

        assert(resultado.isEmpty());
    }
}