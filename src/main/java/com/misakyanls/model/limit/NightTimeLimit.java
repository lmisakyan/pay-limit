package com.misakyanls.model.limit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.misakyanls.model.Payment;
import com.misakyanls.model.Payment.Status;

public final class NightTimeLimit extends BaseLimit {
	
	private final LocalTime from;
	private final LocalTime to;
	private LocalDateTime lastValidDate;

	public NightTimeLimit(BigDecimal limitAmount, LocalTime from, LocalTime to) {
		super(limitAmount);
		this.from = from;
		this.to = to;
	}

	public void validate(Payment payment) {
		LocalDateTime now = LocalDateTime.now(clock);
		LocalTime time = now.toLocalTime();
		
		boolean beforeMidnight = !time.isBefore(from)
				&& !time.isAfter(LocalTime.MAX);
		boolean afterMidnight = !time.isBefore(LocalTime.MIDNIGHT)
				&& time.isBefore(to);
		if (beforeMidnight || afterMidnight) {
			LocalDate date = now.toLocalDate();
			if (lastValidDate == null
					|| (beforeMidnight && lastValidDate.toLocalTime().isBefore(
							from))
					|| (afterMidnight
							&& lastValidDate.toLocalTime().isBefore(from) && !date
								.isEqual(lastValidDate.toLocalDate())))
				currentLimitAmount = BigDecimal.ZERO;
			BigDecimal cur = currentLimitAmount.add(payment.getAmount());
			if (cur.compareTo(limitAmount) > 0)
				payment.setStatus(Status.SUBMIT_REQUIRED);
			else {
				currentLimitAmount = cur;
				lastValidDate = now;
				payment.setStatus(Status.VALID);
			}
		} else
			payment.setStatus(Status.VALID);

	}
}
