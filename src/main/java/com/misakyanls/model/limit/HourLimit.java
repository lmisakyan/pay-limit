package com.misakyanls.model.limit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.misakyanls.model.Payment;
import com.misakyanls.model.Payment.Status;

public final class HourLimit extends BaseLimit {
	private LocalDateTime lastValidDate;

	public HourLimit(BigDecimal limitAmount) {
		super(limitAmount);
	}

	public void validate(Payment payment) {
		LocalDateTime now = LocalDateTime.now(clock);
		if (lastValidDate == null
				|| !now.toLocalDate().isEqual(lastValidDate.toLocalDate())
				|| now.getHour() != lastValidDate.getHour())
			currentLimitAmount = BigDecimal.ZERO;

		BigDecimal cur = currentLimitAmount.add(payment.getAmount());
		if (cur.compareTo(limitAmount) > 0)
			payment.setStatus(Status.SUBMIT_REQUIRED);
		else {
			currentLimitAmount = cur;
			lastValidDate = now;
			payment.setStatus(Status.VALID);
		}
	}
}
