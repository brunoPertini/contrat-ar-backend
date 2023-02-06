package com.contractar.microserviciousuario.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.contractar.microserviciousuario.models.Proveedor;

public interface ProveedorRepository  extends PagingAndSortingRepository<Proveedor, Long>{
    public Proveedor save(Proveedor proveedor);
}
