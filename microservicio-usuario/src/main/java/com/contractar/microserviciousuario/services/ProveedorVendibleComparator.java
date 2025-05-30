package com.contractar.microserviciousuario.services;

import java.util.Comparator;

import com.contractar.microservicioadapter.dtos.AbstractProveedorVendibleDTOAccesor;
import com.contractar.microserviciousuario.dtos.DistanceProveedorDTO;
import com.contractar.microserviciousuario.sorting.VendibleDistanceComparator;
import com.contractar.microserviciousuario.sorting.VendiblePlanComparator;
import com.contractar.microserviciousuario.sorting.VendiblePriceComparator;

/**
 * Main comparator for DistanceProveedorDTO. Ordering by price has higher priority over the other attributes.
 * If ordering by price leaves an equality result, the comparison is made by distance.
 *
 */
public class ProveedorVendibleComparator implements Comparator<AbstractProveedorVendibleDTOAccesor> {
	
	private boolean shouldSortByPrice;
	private boolean shouldSortByDistance;
	
	private VendiblePriceComparator byPriceComparator;
	private VendibleDistanceComparator byDistanceComparator;
	private VendiblePlanComparator byPlanComparator;

	public ProveedorVendibleComparator(boolean shouldSortByPrice, boolean shouldSortByDistance) {
		this.shouldSortByPrice = shouldSortByPrice;
		this.shouldSortByDistance = shouldSortByDistance;
		this.byPriceComparator = new VendiblePriceComparator();
		this.byDistanceComparator = new VendibleDistanceComparator();
		this.byPlanComparator = new VendiblePlanComparator();
	}

	@Override
	public int compare(AbstractProveedorVendibleDTOAccesor o1, AbstractProveedorVendibleDTOAccesor o2) {
		
		int byPlanComparingResult = byPlanComparator.compare(o1, o2);
		
		if (byPlanComparingResult != 0) {
			return byPlanComparingResult;
		}
		
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
