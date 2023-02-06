package com.contractar.microserviciousuario.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Usuario;
import com.contractar.microserviciousuario.services.UsuarioService;

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

    @PostMapping("/usuarios/proveedor")
    public ResponseEntity<Proveedor> crearProveedor(@RequestBody @Valid Proveedor usuario) {
        Proveedor createdUsuario =  usuarioService.createProveedor(usuario); 
        return new ResponseEntity<Proveedor>(createdUsuario, HttpStatus.CREATED);
    }
}
