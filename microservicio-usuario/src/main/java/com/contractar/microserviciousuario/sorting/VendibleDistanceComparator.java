package com.contractar.microserviciousuario.sorting;

import java.util.Comparator;

import com.contractar.microserviciousuario.dtos.DistanceProveedorDTO;

public class VendibleDistanceComparator implements Comparator<DistanceProveedorDTO> {

	public VendibleDistanceComparator() {
	}

	@Override
	public int compare(DistanceProveedorDTO first, DistanceProveedorDTO second) {
		boolean isLower = first.getDistance() < second.getDistance();

		boolean isEqual = first.getDistance() == second.getDistance();

		return isLower ? -1 : isEqual ? 0 : 1;
	}

}
