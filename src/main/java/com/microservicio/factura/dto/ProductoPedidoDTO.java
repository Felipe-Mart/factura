package com.microservicio.factura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoPedidoDTO {
    private Long idProducto;
    private String nombProducto;
    private Double precio;
    private Integer cantidad;

}
