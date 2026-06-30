package com.microservicio.factura.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFactura;

    @Column(nullable= false)
    @NotNull(message = "El id del empleado es obligatorio")
    @Positive(message = "El id del empleado debe ser mayor que 0")
    private Long idEmpleado;

    @Column(nullable= false)
    @NotNull(message = "El id del pedido es obligatorio")   
    @Positive(message = "El id de la compra debe ser mayor que 0")
    private Long idPedido;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Column(nullable= false)
    @Size(max = 100,
            message = "El nombre del cliente no puede superar los 100 caracteres")
    private String nombreCliente;

    @Column(nullable= false)
    @Size(max = 20,
            message = "El rut no puede superar los 20 caracteres")
    private String rutCliente;

    @Column(nullable= false)
    @Email(message = "El correo electrónico no tiene un formato válido")
    @Size(max = 100,
            message = "El correo no puede superar los 100 caracteres")
    private String emailCliente;

    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "factura_id")
    private List<ProductoFactura> productos;

    @Column(nullable= false)
    @PositiveOrZero(
            message = "El total final no puede ser negativo")
    private Double totalFinal;

    @Column(nullable= false)
    @PositiveOrZero(
            message = "El subtotal no puede ser negativo")
    private Double subtotal;

    @Column(nullable= false)
    @PositiveOrZero(
            message = "El IVA no puede ser negativo")
    private Double iva;

    @Column(nullable= false, updatable = false)
    private LocalDate fechaEmision;
}