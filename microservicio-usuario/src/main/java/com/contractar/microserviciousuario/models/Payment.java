package com.contractar.microserviciousuario.models;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;


/**
 * The payment is registered with its state (PENDING, SUCCESS OR FAILED), the period of the Subscription
 * it belongs to, and the date it was made.
 */
@Entity
public class Payment implements Serializable {
	private static final long serialVersionUID = -5539133673400232413L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private PaymentState state;
	
	private LocalDate paymentPeriod;

	private LocalDate date;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("id")
	@JoinColumn(name = "suscripcion_id")
	private Suscripcion suscripcion;
	
	public Payment() {}
	
	public Payment(PaymentState state, LocalDate date, Suscripcion suscripcion) {
		this.state = state;
		this.date = date;
		this.suscripcion = suscripcion;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PaymentState getState() {
		return state;
	}

	public void setState(PaymentState state) {
		this.state = state;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Suscripcion getSuscripcion() {
		return suscripcion;
	}

	public void setSuscripcion(Suscripcion suscripcion) {
		this.suscripcion = suscripcion;
	}
	
	public LocalDate getPaymentPeriod() {
		return paymentPeriod;
	}

	public void setPaymentPeriod(LocalDate paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	private static enum PaymentState {
		SUCCESS,
		FAILED,
		PENDING,
	}
}
