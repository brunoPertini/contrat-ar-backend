package com.contractar.microserviciocommons.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.date.enums.DateFormatType;
import com.contractar.microserviciocommons.date.enums.DateOperationType;
import com.contractar.microserviciocommons.helpers.DatesHelper;
import com.contractar.microserviciocommons.constants.controllers.DateControllerUrls;

@RestController
public class DateController {
	private DatesHelper datesHelper;
	
	public DateController(DatesHelper datesHelper) {
		this.datesHelper = datesHelper;
	}
	
	@GetMapping(DateControllerUrls.DATES_BASE_URL)
	public ResponseEntity<String> getFormattedDate(@RequestParam(name = "operation", required = true) DateOperationType operation
			, @RequestParam(name="format", required = false) DateFormatType format) {
		return new ResponseEntity<>(format.equals(DateFormatType.DAY_AND_MONTH)
				? datesHelper.getMonthAndYearPattern(): datesHelper.getFullDatePattern(), HttpStatus.OK);
	}
}
