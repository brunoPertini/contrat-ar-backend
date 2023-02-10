package com.contractar.microserviciousuario.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.contractar.microserviciousuario.models.Cliente;

public interface ClienteRepository extends PagingAndSortingRepository<Cliente, Long>{
    public Cliente save(Cliente usuario);

}
