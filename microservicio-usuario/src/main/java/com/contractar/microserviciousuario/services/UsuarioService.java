package com.contractar.microserviciousuario.services;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contractar.microserviciousuario.models.Cliente;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Usuario;
import com.contractar.microserviciousuario.repository.ClienteRepository;
import com.contractar.microserviciousuario.repository.ProveedorRepository;
import com.contractar.microserviciousuario.repository.UsuarioRepository;
import com.contractar.microserviciovendible.models.Producto;
import com.contractar.microserviciovendible.models.Servicio;
import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.serviciocommons.proveedores.ProveedorType;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public Usuario create(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }


    public Proveedor createProveedor(Proveedor proveedor, ProveedorType proveedorType) {
        boolean isProductoProveedor = proveedorType.equals(ProveedorType.PRODUCTOS);

        List<? extends Vendible> parsedVendibles = ((List<LinkedHashMap>)proveedor.getVendibles())
        .stream()
        .map(v -> {
            String nombre = (String)v.get("nombre");
            String descripcion = (String)v.get("descripcion");
            int precio = (int)v.get("precio");

            if (isProductoProveedor) {
                int stock = (int)v.get("stock");
                return new Producto(precio, descripcion, nombre, stock);  
            } else {
                return new Servicio(precio, descripcion, nombre);
            } 
        })
        .collect(Collectors.toList());

        proveedor.setVendibles(parsedVendibles);
        return proveedorRepository.save(proveedor);
    }

    public Cliente createCliente(Cliente cliente) {
        return clienteRepository.save(cliente); 
    }
}
