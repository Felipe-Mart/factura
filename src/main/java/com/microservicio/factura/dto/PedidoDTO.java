package com.microservicio.factura.dto;

import java.util.List;

import lombok.Data;

@Data
public class PedidoDTO {

    private Long idPedido;
    private Long idCliente;
    private List<ProductoPedidoDTO> productos;

}
