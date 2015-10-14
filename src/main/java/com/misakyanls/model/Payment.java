package com.misakyanls.model;

import java.math.BigDecimal;

public class Payment {
	public static enum Status {
		WAITING, VALID, SUBMIT_REQUIRED
	}

	private final Service service;
	private final Account account;
	private final BigDecimal amount;
	private Status status;

	public Payment(Service service, Account account, BigDecimal amount,
			Status status) {
		this.service = service;
		this.account = account;
		this.amount = amount;
		this.status = status;
	}

	public Service getService() {
		return service;
	}

	public Account getAccount() {
		return account;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
