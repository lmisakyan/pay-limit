package com.misakyanls.model.limit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.misakyanls.model.Payment;
import com.misakyanls.model.Payment.Status;

public final class DayTimeLimit extends BaseLimit {

	private final LocalTime from;
	private final LocalTime to;
	private LocalDate lastValidDate;

	public DayTimeLimit(BigDecimal limitAmount, LocalTime from, LocalTime to) {
		super(limitAmount);
		this.from = from;
		this.to = to;
	}

	public DayTimeLimit(BigDecimal limitAmount) {
		this(limitAmount, LocalTime.of(0, 0), LocalTime.of(23, 59, 59));
	}

	@Override
	public void validate(Payment payment) {
		LocalDateTime now = LocalDateTime.now(clock);
		LocalTime time = now.toLocalTime();

		if (!time.isBefore(from) && time.isBefore(to)) {
			LocalDate date = now.toLocalDate();
			if (lastValidDate == null || (!date.isEqual(lastValidDate)))
				currentLimitAmount = BigDecimal.ZERO;
			BigDecimal cur = currentLimitAmount.add(payment.getAmount());
			if (cur.compareTo(limitAmount) > 0)
				payment.setStatus(Status.SUBMIT_REQUIRED);
			else {
				currentLimitAmount = cur;
				lastValidDate = date;
				payment.setStatus(Status.VALID);
			}
		} else
			payment.setStatus(Status.VALID);
	}

}
