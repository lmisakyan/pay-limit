package com.misakyanls.service;

import com.misakyanls.model.Payment;
import com.misakyanls.model.Payment.Status;
import com.misakyanls.model.limit.Limit;
import com.misakyanls.repository.LimitRepository;

public final class PayLimitPolicy {
	private LimitRepository limitRepository;

	public void validate(Payment payment) {
		if (payment == null) {
			throw new IllegalArgumentException(
					"The 'payment' argument must not be null");
		} else if (payment.getAccount() == null) {
			throw new IllegalArgumentException(
					"The 'account' attribute must not be null or empty");
		} else if (payment.getService() == null) {
			throw new IllegalArgumentException(
					"The 'service' attribute must not be null");
		} else if (payment.getAmount() == null) {
			throw new IllegalArgumentException(
					"The 'amount' attribute must not be null");
		}

		for (Limit limit : limitRepository.getLimits(payment.getService())) {
			limit.validate(payment);
			if (payment.getStatus() == Status.SUBMIT_REQUIRED)
				break;
		}

		if (payment.getStatus() == Status.WAITING)
			payment.setStatus(Status.VALID);
	}

	public void setLimitRepository(LimitRepository limitRepository) {
		this.limitRepository = limitRepository;
	}

}
