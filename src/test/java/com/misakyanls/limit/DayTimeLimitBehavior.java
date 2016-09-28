package com.misakyanls.limit;

import com.misakyanls.model.Payment;
import com.misakyanls.model.Payment.Status;
import com.misakyanls.model.limit.DayTimeLimit;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DayTimeLimitBehavior {
    Payment payment = AllLimitHelper.createPayment();

    @Test
    public void shouldValidatePayment() {
        // given
        DayTimeLimit limit = new DayTimeLimit(BigDecimal.ONE);
        limit.setClock(Clock.systemUTC());

        // when
        limit.validate(payment);

        // then
        assertThat(payment.getStatus(), is(Status.VALID));
    }

    @Test
    public void shouldNotValidateIfAmountExceeded() {
        // given
        DayTimeLimit limit = new DayTimeLimit(BigDecimal.ONE);
        limit.setClock(Clock.systemUTC());

        // when
        limit.validate(payment);
        limit.validate(payment);

        // then
        assertThat(payment.getStatus(), is(Status.SUBMIT_REQUIRED));
    }

    @Test
    public void shouldValidateIfAmountExceededNotInTime() {
        // given
        DayTimeLimit limit = new DayTimeLimit(BigDecimal.ONE,
                LocalTime.of(9, 0), LocalTime.of(23, 0));
        // use Moscow time for testing
        // 09-00 - in limit time
        Clock clock = Clock.fixed(Instant.parse("2015-09-14T06:00:00.00Z"),
                ZoneId.of("UTC+3"));
        limit.setClock(clock);

        // when
        limit.validate(payment);
        // 23-00 - NOT in limit time
        clock = Clock.fixed(Instant.parse("2015-09-14T20:00:00.00Z"),
                ZoneId.of("UTC+3"));
        limit.setClock(clock);
        limit.validate(payment);

        // then
        assertThat(payment.getStatus(), is(Status.VALID));
    }

    @Test
    public void shouldNotValidateIfAmountExceededInTime() {
        // given
        DayTimeLimit limit = new DayTimeLimit(BigDecimal.ONE,
                LocalTime.of(9, 0), LocalTime.of(23, 0));
        // use Moscow time for testing
        // 09-00 - in limit time
        Clock clock = Clock.fixed(Instant.parse("2015-09-14T06:00:00.00Z"),
                ZoneId.of("UTC+3"));
        limit.setClock(clock);

        // when
        limit.validate(payment);
        // 22:59:59 - also in limit time
        clock = Clock.fixed(Instant.parse("2015-09-14T19:59:59.00Z"),
                ZoneId.of("UTC+3"));
        limit.setClock(clock);
        limit.validate(payment);

        // then
        assertThat(payment.getStatus(), is(Status.SUBMIT_REQUIRED));
    }

    @Test
    public void shouldValidateIfAmountExceededNextDay() {
        // given
        DayTimeLimit limit = new DayTimeLimit(BigDecimal.ONE,
                LocalTime.of(9, 0), LocalTime.of(23, 0));
        // use Moscow time for testing
        // 09-00 - in limit time
        Clock clock = Clock.fixed(Instant.parse("2015-09-14T06:00:00.00Z"),
                ZoneId.of("UTC+3"));
        limit.setClock(clock);

        // when
        limit.validate(payment);
        // 09-00 next day - NOT in limit time. Should zero amount
        clock = Clock.fixed(Instant.parse("2015-09-15T06:00:00.00Z"),
                ZoneId.of("UTC+3"));
        limit.setClock(clock);
        limit.validate(payment);

        // then
        assertThat(payment.getStatus(), is(Status.VALID));
    }

}
