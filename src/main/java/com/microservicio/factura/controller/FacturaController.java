package com.microservicio.factura.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.factura.model.Factura;
import com.microservicio.factura.service.FacturaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/facturas")
@Tag(name = "Facturas", description = "Operaciones relacionadas con la generación, consulta, eliminación y envío de facturas.")
public class FacturaController {

        @Autowired
        private FacturaService facturaService;

        @PostMapping
        @Operation(summary = "Generar factura", description = "Crea una nueva factura en el sistema y devuelve el recurso creado junto con sus enlaces HATEOAS.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Factura generada correctamente"),
                        @ApiResponse(responseCode = "400", description = "Datos de la factura inválidos o incorrectos")
        })
        public ResponseEntity<EntityModel<Factura>> generarFactura(
                        @Valid @RequestBody Factura factura) {

                Factura nueva = facturaService.generarFactura(factura);

                EntityModel<Factura> recurso = EntityModel.of(nueva);

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
        @Operation(summary = "Listar facturas", description = "Obtiene una lista de todas las facturas registradas en el sistema.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de facturas obtenida correctamente")
        })
        public CollectionModel<EntityModel<Factura>> listarFacturas() {

                List<EntityModel<Factura>> facturas = facturaService.listarFacturas()
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
        @Operation(summary = "Buscar factura por ID", description = "Obtiene los detalles de una factura específica mediante su identificador único.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Factura encontrada"),
                        @ApiResponse(responseCode = "404", description = "Factura no encontrada en el sistema")
        })
        public ResponseEntity<EntityModel<Factura>> obtenerFacturaPorId(
                        @PathVariable Long id) {

                Factura factura = facturaService.obtenerFacturaPorId(id);

                EntityModel<Factura> recurso = EntityModel.of(factura);

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
        @Operation(summary = "Eliminar factura", description = "Elimina de forma permanente una factura del sistema mediante su identificador.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Factura eliminada correctamente"),
                        @ApiResponse(responseCode = "404", description = "Factura no encontrada para eliminar")
        })
        public ResponseEntity<String> eliminarFactura(
                        @PathVariable Long id) {

                facturaService.eliminarFactura(id);

                return ResponseEntity.ok(
                                "Factura eliminada correctamente");
        }

        @PostMapping("/{id}/enviar-email")
        @Operation(summary = "Enviar factura por email", description = "Dispara el proceso de envío de la factura por correo electrónico al cliente correspondiente.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Correo enviado correctamente"),
                        @ApiResponse(responseCode = "404", description = "Factura no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error interno al intentar enviar el correo")
        })
        public ResponseEntity<String> enviarFacturaEmail(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                facturaService.enviarFacturaPorEmail(id));
        }
}