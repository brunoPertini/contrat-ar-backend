package com.contractar.microserviciousuario.sorting;

import java.util.Comparator;

import com.contractar.microservicioadapter.dtos.AbstractProveedorVendibleDTOAccesor;

public class VendiblePriceComparator implements Comparator<AbstractProveedorVendibleDTOAccesor>{

	public VendiblePriceComparator() {}

	@Override
	public int compare(AbstractProveedorVendibleDTOAccesor first, AbstractProveedorVendibleDTOAccesor second) {
		boolean isLower = first.getPrecio() < second.getPrecio();

		boolean isEqual = first.getPrecio() == second.getPrecio();

		return isLower ? -1 : isEqual ? 0 : 1;
	}

}
