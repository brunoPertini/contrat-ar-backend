package com.contractar.microserviciovendible.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.contractar.microserviciocommons.exceptions.VendibleNotFoundException;
import com.contractar.microserviciovendible.repository.VendibleRepository;

@Service
public class VendibleService {
	@Autowired
	private VendibleRepository vendibleRepository;
	
	public void deleteById(Long id) throws VendibleNotFoundException {
		try {
			vendibleRepository.deleteAllProvedoresAndVendiblesRelations(id);
			vendibleRepository.deleteById(id);
		} catch (EmptyResultDataAccessException ex) {
			throw new VendibleNotFoundException();
		}
	}
}
