package com.contractar.microserviciousuario.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciousuario.models.Cliente;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Usuario;
import com.contractar.microserviciousuario.services.UsuarioService;
import com.contractar.microserviciovendible.models.Vendible;

import jakarta.validation.Valid;

@RestController
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;
    
    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> crearUsuario(@RequestBody @Valid Usuario usuario) {
        Usuario createdUsuario =  usuarioService.create(usuario); 
        return new ResponseEntity<Usuario>(createdUsuario, HttpStatus.CREATED);
    }

    @PostMapping(UsersControllerUrls.CREATE_PROVEEDOR)
    public ResponseEntity<Proveedor> crearProveedor(@RequestBody @Valid Proveedor usuario) {
        Proveedor createdUsuario =  usuarioService.createProveedor(usuario); 
        return new ResponseEntity<Proveedor>(createdUsuario, HttpStatus.CREATED);
    }

    @PostMapping(UsersControllerUrls.CREATE_CLIENTE)
    public ResponseEntity<Cliente> crearCliente(@RequestBody @Valid Cliente usuario) {
        Cliente createdUsuario =  usuarioService.createCliente(usuario); 
        return new ResponseEntity<Cliente>(createdUsuario, HttpStatus.CREATED);
    }
    
    @GetMapping(UsersControllerUrls.GET_PROVEEDOR)
    public ResponseEntity<Proveedor> findProveedor(@RequestParam Long id) {
    	Proveedor proveedor = usuarioService.findProveedorById(id);
    	return new ResponseEntity<Proveedor>(proveedor, HttpStatus.OK);
    }
    
    @GetMapping(UsersControllerUrls.GET_USUARIOS)
    public ResponseEntity<Usuario> findByParam(@RequestParam(required = false) String email,
    		@RequestParam(required = false) Long id) throws UserNotFoundException {
        Usuario usuario = email != null ? usuarioService.findByEmail(email) : usuarioService.findById(id);
        return new ResponseEntity<Usuario>(usuario, HttpStatus.OK); 
    }
    
    @PutMapping(UsersControllerUrls.UPDATE_PROVEEDOR)
    public ResponseEntity addVendible(Vendible vendible) {
    	
    }
}
