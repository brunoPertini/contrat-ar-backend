package com.contractar.microserviciocommons.proveedores;

import java.util.List;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciovendible.models.Producto;
import com.contractar.microserviciovendible.models.Servicio;
import com.contractar.microserviciovendible.models.Vendible;

public class ProveedorHelper {

    public static List<? extends Vendible> parseVendibles(Proveedor proveedor, ProveedorType proveedorType) {
        boolean isProductoProveedor = proveedorType.equals(ProveedorType.PRODUCTOS);

        return ((List<LinkedHashMap>) proveedor.getVendibles())
                .stream()
                .map(v -> {
                    String nombre = (String) v.get("nombre");
                    String descripcion = (String) v.get("descripcion");
                    int precio = (int) v.get("precio");

                    if (isProductoProveedor) {
                        int stock = (int) v.get("stock");
                        return new Producto(precio, descripcion, nombre, stock);
                    } else {
                        return new Servicio(precio, descripcion, nombre);
                    }
                })
                .collect(Collectors.toList());
    }
}
