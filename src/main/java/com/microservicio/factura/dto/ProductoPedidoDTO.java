package com.microservicio.factura.dto;

import lombok.Data;

@Data
public class ProductoPedidoDTO {
    private Long idProducto;
    private String nombProducto;
    private Double precio;
    private Integer cantidad;

}
