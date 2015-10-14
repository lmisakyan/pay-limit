package com.misakyanls.limit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.Test;

import com.misakyanls.model.Payment;
import com.misakyanls.model.Payment.Status;
import com.misakyanls.model.limit.NightTimeLimit;

public class NightTimeLimitBehavior {
	Payment payment;

	@Before
	public void createPayment() {
		payment = AllLimitHelper.createPayment();
	}

	@Test
	public void shouldValidateIfAmountNotExceeded() {
		// given
		NightTimeLimit limit = new NightTimeLimit(BigDecimal.TEN, LocalTime.of(
				23, 0), LocalTime.of(9, 0));
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
	public void shouldNotValidateIfAmountExceededBeforeMidnight() {
		// given
		NightTimeLimit limit = new NightTimeLimit(BigDecimal.ONE, LocalTime.of(
				23, 0), LocalTime.of(9, 0));
		// use Moscow time for testing
		// 23-00 - in limit time
		Clock clock = Clock.fixed(Instant.parse("2015-09-14T20:00:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);

		// when
		limit.validate(payment);
		// 23-01 - also in limit time
		clock = Clock.fixed(Instant.parse("2015-09-14T20:01:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);
		limit.validate(payment);

		// then
		assertThat(payment.getStatus(), is(Status.SUBMIT_REQUIRED));
	}

	@Test
	public void shouldNotValidateIfAmountExceededAfterMidnight() {
		// given
		NightTimeLimit limit = new NightTimeLimit(BigDecimal.ONE, LocalTime.of(
				23, 0), LocalTime.of(9, 0));
		// use Moscow time for testing
		// 08:58:59 - in limit time
		Clock clock = Clock.fixed(Instant.parse("2015-09-15T05:58:59.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);

		// when
		limit.validate(payment);
		// 08:59:59 - also in limit time
		clock = Clock.fixed(Instant.parse("2015-09-15T05:59:59.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);
		limit.validate(payment);

		// then
		assertThat(payment.getStatus(), is(Status.SUBMIT_REQUIRED));
	}

	@Test
	public void shouldNotValidateIfAmountExceededNextDay() {
		// given
		NightTimeLimit limit = new NightTimeLimit(BigDecimal.ONE, LocalTime.of(
				23, 0), LocalTime.of(9, 0));
		// use Moscow time for testing
		// 23-00 - in limit time
		Clock clock = Clock.fixed(Instant.parse("2015-09-14T20:00:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);

		// when
		limit.validate(payment);
		// 08:59:59 - also in limit time
		clock = Clock.fixed(Instant.parse("2015-09-15T05:59:59.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);
		limit.validate(payment);

		// then
		assertThat(payment.getStatus(), is(Status.SUBMIT_REQUIRED));
	}

	@Test
	public void shouldValidateIfAmountExceededNextNight() {
		// given
		NightTimeLimit limit = new NightTimeLimit(BigDecimal.ONE, LocalTime.of(
				23, 0), LocalTime.of(9, 0));
		// use Moscow time for testing
		// 03-00 - in limit time
		Clock clock = Clock.fixed(Instant.parse("2015-09-14T00:00:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);

		// when
		limit.validate(payment);
		// 03-00 next day - NOT in limit time. Should zero amount
		clock = Clock.fixed(Instant.parse("2015-09-15T00:00:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);
		limit.validate(payment);

		// then
		assertThat(payment.getStatus(), is(Status.VALID));
	}

	@Test
	public void shouldValidateIfAmountExceededNextFrame() {
		// given
		NightTimeLimit limit = new NightTimeLimit(BigDecimal.ONE, LocalTime.of(
				23, 0), LocalTime.of(9, 0));
		// use Moscow time for testing
		// 03-00 - in limit time
		Clock clock = Clock.fixed(Instant.parse("2015-09-14T00:00:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);

		// when
		limit.validate(payment);
		// 23-00 same day - NOT in limit time. Should zero amount
		clock = Clock.fixed(Instant.parse("2015-09-14T20:00:00.00Z"),
				ZoneId.of("UTC+3"));
		limit.setClock(clock);
		limit.validate(payment);

		// then
		assertThat(payment.getStatus(), is(Status.VALID));
	}
}
