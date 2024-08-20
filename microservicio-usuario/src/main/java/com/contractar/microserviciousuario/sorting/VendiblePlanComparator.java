package com.contractar.microserviciousuario.sorting;

import java.util.Comparator;

import com.contractar.microservicioadapter.dtos.AbstractProveedorVendibleDTOAccesor;

public class VendiblePlanComparator implements Comparator<AbstractProveedorVendibleDTOAccesor> {

	@Override
	public int compare(AbstractProveedorVendibleDTOAccesor o1, AbstractProveedorVendibleDTOAccesor o2) {
		if (o1.getPlanId() == 1 && o2.getPlanId() == 2) {
			return 1;
		}
		
		if (o1.getPlanId().equals(2) && o2.getPlanId().equals(1)) {
			return -1;
		}
		
		return 0;
	}

}
