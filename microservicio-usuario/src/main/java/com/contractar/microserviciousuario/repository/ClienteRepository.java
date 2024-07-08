package com.contractar.microserviciousuario.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.contractar.microserviciousuario.models.Cliente;

public interface ClienteRepository extends PagingAndSortingRepository<Cliente, Long>{
	public Optional<Cliente> findById(Long id);

    public Cliente save(Cliente usuario);
    
    public void deleteById(Long id);

}
