package com.contractar.microserviciocommons.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.date.enums.DateFormatType;
import com.contractar.microserviciocommons.dto.DateOperationDTO;
import com.contractar.microserviciocommons.helpers.DatesHelper;
import com.contractar.microserviciocommons.constants.controllers.DateControllerUrls;

import jakarta.validation.Valid;

@RestController
public class DateController {
	private DatesHelper datesHelper;
	
	public DateController(DatesHelper datesHelper) {
		this.datesHelper = datesHelper;
	}
	
	GetMapping(DateControllerUrls.DATES_BASE_URL)
	ResponseEntity<?> getFormattedDate(@RequestBody @Valid DateOperationDTO body) {
		return new ResponseEntity(body.getOperation().equals(DateFormatType.DAY_AND_MONTH) 
				? datesHelper.getMonthAndYearFormattedDate(body.getDate()) 
				: datesHelper.getFullFormattedDate(body.getDate()), HttpStatus.OK);
	}
}
