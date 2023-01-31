package com.contractar.usuarios.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.contractar.usuarios.models.Usuario;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long>{
    public Usuario save(Usuario usuario);
}
