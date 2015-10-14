package com.misakyanls.limit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.Test;

import com.misakyanls.model.Payment;
import com.misakyanls.model.Payment.Status;
import com.misakyanls.model.limit.HourLimit;

public class HourLimitBehavior {
	Payment payment;

	@Before
	public void createPayment() {
		payment = AllLimitHelper.createPayment();
	}

	@Test
	public void shouldValidateIfAmountNotExceeded() {
		// given
		HourLimit limit = new HourLimit(BigDecimal.TEN);
		// use Moscow time for testing
		// 03-00 - in limit time
		Clock clock = Clock.fixed(Instant.parse("2015-09-14T00:00:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);

		// when
		limit.validate(payment);
		// 03-01 in limit time
		clock = Clock.fixed(Instant.parse("2015-09-14T00:01:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);
		limit.validate(payment);

		// then
		assertThat(payment.getStatus(), is(Status.VALID));
	}

	@Test
	public void shouldValidateIfAmountExceededNextHour() {
		// given
		HourLimit limit = new HourLimit(BigDecimal.ONE);
		// use Moscow time for testing
		// 03-00 - in limit time
		Clock clock = Clock.fixed(Instant.parse("2015-09-14T00:00:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);

		// when
		limit.validate(payment);
		// 04-00 NOT in limit time, next hour
		clock = Clock.fixed(Instant.parse("2015-09-15T01:00:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);
		limit.validate(payment);

		// then
		assertThat(payment.getStatus(), is(Status.VALID));
	}

	@Test
	public void shouldValidateIfAmountExceededNextDay() {
		// given
		HourLimit limit = new HourLimit(BigDecimal.ONE);
		// use Moscow time for testing
		// 03-00 - in limit time
		Clock clock = Clock.fixed(Instant.parse("2015-09-14T00:00:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);

		// when
		limit.validate(payment);
		// 03-00 NOT in limit time, next day
		clock = Clock.fixed(Instant.parse("2015-09-15T00:00:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);
		limit.validate(payment);

		// then
		assertThat(payment.getStatus(), is(Status.VALID));
	}

	@Test
	public void shouldNotValidateIfAmountExceeded() {
		// given
		HourLimit limit = new HourLimit(BigDecimal.ONE);
		// use Moscow time for testing
		// 03-00 - in limit time
		Clock clock = Clock.fixed(Instant.parse("2015-09-14T00:00:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);

		// when
		limit.validate(payment);
		// 03-01 in limit time
		clock = Clock.fixed(Instant.parse("2015-09-14T00:01:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);
		limit.validate(payment);

		// then
		assertThat(payment.getStatus(), is(Status.SUBMIT_REQUIRED));
	}

	@Test
	public void shouldNotValidateIfAmountExceededByOnePayment() {
		// given
		HourLimit limit = new HourLimit(BigDecimal.ZERO);
		// use Moscow time for testing
		// 03-00 - in limit time
		Clock clock = Clock.fixed(Instant.parse("2015-09-14T00:00:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);

		// when
		limit.validate(payment);
		
		// then
		assertThat(payment.getStatus(), is(Status.SUBMIT_REQUIRED));
	}
}
