package com.contractar.microserviciousuario.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contractar.microserviciousuario.models.Cliente;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Usuario;
import com.contractar.microserviciousuario.repository.ClienteRepository;
import com.contractar.microserviciousuario.repository.ProveedorRepository;
import com.contractar.microserviciousuario.repository.UsuarioRepository;
import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.serviciocommons.proveedores.ProveedorHelper;
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
        List<? extends Vendible> parsedVendibles = ProveedorHelper.parseVendibles(proveedor, proveedorType);

        proveedor.setVendibles(parsedVendibles);
        return proveedorRepository.save(proveedor);
    }

    public Cliente createCliente(Cliente cliente) {
        return clienteRepository.save(cliente); 
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}
