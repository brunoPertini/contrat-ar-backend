package com.contractar.microserviciocommons.proveedores;

import java.util.Set;
import java.util.stream.Collectors;

import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciovendible.models.Producto;
import com.contractar.microserviciovendible.models.Servicio;
import com.contractar.microserviciovendible.models.Vendible;

public class ProveedorHelper {

    public static Set<Vendible> parseVendibles(Proveedor proveedor, ProveedorType proveedorType) {
        boolean isProductoProveedor = proveedorType.equals(ProveedorType.PRODUCTOS);

        return proveedor.getVendibles()
                .stream()
                .map(proveedorVendible -> {
                	Vendible v = proveedorVendible.getVendible();
                    String nombre = v.getNombre();
                    String descripcion = proveedorVendible.getDescripcion();
                    int precio = proveedorVendible.getPrecio();

                    if (isProductoProveedor) {
                        int stock = ((Producto) v).getStock();
                        return new Producto(precio, descripcion, nombre, stock);
                    } else {
                        return new Servicio(nombre);
                    }
                }).collect(Collectors.toSet());
    }
}
