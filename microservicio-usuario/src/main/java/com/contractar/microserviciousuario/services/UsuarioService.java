package com.contractar.microserviciousuario.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.contractar.microserviciousuario.models.Cliente;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Role;
import com.contractar.microserviciousuario.models.Usuario;
import com.contractar.microserviciousuario.repository.ClienteRepository;
import com.contractar.microserviciousuario.repository.ProveedorRepository;
import com.contractar.microserviciousuario.repository.UsuarioRepository;
import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.microserviciocommons.constants.RolesNames;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.proveedores.ProveedorHelper;
import com.contractar.microserviciocommons.proveedores.ProveedorType;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ClienteRepository clienteRepository;
    
    private String setFinalRole(ProveedorType proveedorType) {
    	Optional<ProveedorType> proveedorTypeOptional = Optional.ofNullable(proveedorType);
    	
    	if (!proveedorTypeOptional.isPresent()) {
    		return RolesNames.CLIENTE;
    	}
    	
    	return proveedorType.equals(ProveedorType.PRODUCTOS) ? RolesNames.PROVEEDOR_PRODUCTOS
    			: RolesNames.PROVEEDOR_SERVICIOS;
    }

    public Usuario create(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }


    public Proveedor createProveedor(Proveedor proveedor) {
    	ProveedorType proveedorType = proveedor.getProveedorType();
        List<? extends Vendible> parsedVendibles = ProveedorHelper.parseVendibles(proveedor, proveedorType);

        proveedor.setVendibles(parsedVendibles);
        proveedor.setRole(new Role(this.setFinalRole(proveedorType)));
        return proveedorRepository.save(proveedor);
    }

    public Cliente createCliente(Cliente cliente) {
    	cliente.setRole(new Role(this.setFinalRole(null)));
        return clienteRepository.save(cliente); 
    }

    public Usuario findByEmail(String email) throws UserNotFoundException {
        Usuario usuario  = usuarioRepository.findByEmail(email);
        
        if (usuario != null) {
        	return usuario;
        }
        throw new UserNotFoundException();
    }
}
