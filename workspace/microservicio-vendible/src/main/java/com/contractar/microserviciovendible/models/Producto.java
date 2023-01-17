package com.contractar.microserviciovendible.models;

@SuppressWarnings("serial")
public class Producto extends Vendible {
	private int stock;
	
	public Producto() {
		
	}

	public Producto(int stock) {
		super();
		this.stock = stock;
	}
	
	
}
