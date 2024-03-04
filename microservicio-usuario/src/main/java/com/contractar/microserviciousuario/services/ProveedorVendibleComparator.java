package com.contractar.microserviciousuario.services;

import java.util.Comparator;

import com.contractar.microserviciocommons.dto.proveedorvendible.AbstractProveedorVendibleDTO;
import com.contractar.microserviciousuario.dtos.DistanceProveedorDTO;
import com.contractar.microserviciousuario.sorting.VendibleDistanceComparator;
import com.contractar.microserviciousuario.sorting.VendiblePriceComparator;

/**
 * Main comparator for DistanceProveedorDTO. Ordering by price has higher priority over the other attributes.
 * If ordering by price leaves an equality result, the comparison is made by distance.
 *
 */
public class ProveedorVendibleComparator implements Comparator<AbstractProveedorVendibleDTO> {
	
	private boolean shouldSortByPrice;
	private boolean shouldSortByDistance;
	
	private VendiblePriceComparator byPriceComparator;
	private VendibleDistanceComparator byDistanceComparator;

	public ProveedorVendibleComparator(boolean shouldSortByPrice, boolean shouldSortByDistance) {
		this.shouldSortByPrice = shouldSortByPrice;
		this.shouldSortByDistance = shouldSortByDistance;
		this.byPriceComparator = new VendiblePriceComparator();
		this.byDistanceComparator = new VendibleDistanceComparator();
	}

	@Override
	public int compare(AbstractProveedorVendibleDTO o1, AbstractProveedorVendibleDTO o2) {
		
		DistanceProveedorDTO first = (DistanceProveedorDTO) o1;
		
		DistanceProveedorDTO second = (DistanceProveedorDTO) o2;
		
		int dtosComparingResult = first.equals(second) ? 0 : -1;

		if (shouldSortByPrice) {
			int byPriceComparingResult = byPriceComparator.compare(first, second);

			return byPriceComparingResult != 0 ? byPriceComparingResult
					: shouldSortByDistance ? byDistanceComparator.compare(first, second) : dtosComparingResult;
		}

		return shouldSortByDistance ? byDistanceComparator.compare(first, second) : dtosComparingResult;
	}

}
