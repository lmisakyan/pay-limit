package com.misakyanls.limit;

import com.misakyanls.model.Account;
import com.misakyanls.model.Payment;
import com.misakyanls.model.Payment.Status;
import com.misakyanls.model.Service;
import com.misakyanls.model.limit.DayAccountLimit;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DayAccountLimitBehavior {
    Payment payment = AllLimitHelper.createPayment();

    @Test
    public void shouldValidateIfAmountNotExceeded() {
        // given
        DayAccountLimit limit = new DayAccountLimit(BigDecimal.TEN);
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
    public void shouldNotValidateIfAmountExceeded() {
        // given
        DayAccountLimit limit = new DayAccountLimit(BigDecimal.ONE);
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
    public void shouldValidateIfAmountExceededNextDay() {
        // given
        DayAccountLimit limit = new DayAccountLimit(BigDecimal.ONE);
        // use Moscow time for testing
        // 03-00 - in limit time
        Clock clock = Clock.fixed(Instant.parse("2015-09-14T00:00:00.00Z"),
                ZoneId.of("UTC+3"));
        limit.setClock(clock);

        // when
        limit.validate(payment);
        // 03-00 next day, NOT in limit time
        clock = Clock.fixed(Instant.parse("2015-09-15T00:00:00.00Z"),
                ZoneId.of("UTC+3"));
        limit.setClock(clock);
        limit.validate(payment);

        // then
        assertThat(payment.getStatus(), is(Status.VALID));
    }

    @Test
    public void shouldValidateIfAmountExceededOtherAccount() {
        // given
        DayAccountLimit limit = new DayAccountLimit(BigDecimal.ONE);
        // use Moscow time for testing
        // 03-00 - in limit time
        Clock clock = Clock.fixed(Instant.parse("2015-09-14T00:00:00.00Z"),
                ZoneId.of("UTC+3"));
        limit.setClock(clock);

        // when
        limit.validate(payment);

        // 03-01 next day, NOT in limit time
        clock = Clock.fixed(Instant.parse("2015-09-14T00:01:00.00Z"),
                ZoneId.of("UTC+3"));
        limit.setClock(clock);
        Service service = new Service("1", "Service 1");
        Account account = new Account("other_acc_number");
        Payment payment1 = new Payment(service, account, BigDecimal.ONE,
                Status.WAITING);
        limit.validate(payment1);

        // then
        assertThat(payment1.getStatus(), is(Status.VALID));
    }
}
