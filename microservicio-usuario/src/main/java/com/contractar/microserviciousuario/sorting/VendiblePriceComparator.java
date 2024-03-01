package com.contractar.microserviciousuario.sorting;

import com.contractar.microserviciocommons.dto.proveedorvendible.AbstractProveedorVendibleDTO;

public class VendiblePriceComparator extends BaseComparator {

	public VendiblePriceComparator() {}

	@Override
	public int compare(AbstractProveedorVendibleDTO first, AbstractProveedorVendibleDTO second) {
		boolean isLower = first.getPrecio() < second.getPrecio();

		boolean isEqual = first.getPrecio() == second.getPrecio();

		return isLower ? -1 : isEqual ? 0 : 1;
	}

}
