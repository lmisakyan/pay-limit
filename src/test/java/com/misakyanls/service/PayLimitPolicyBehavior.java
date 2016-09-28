package com.misakyanls.service;

import com.misakyanls.model.Account;
import com.misakyanls.model.Payment;
import com.misakyanls.model.Payment.Status;
import com.misakyanls.model.Service;
import com.misakyanls.model.limit.Limit;
import com.misakyanls.repository.LimitRepository;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class PayLimitPolicyBehavior {
    private LimitRepository limitRepository = mock(LimitRepository.class);
    private PayLimitPolicy payLimitPolicy = new PayLimitPolicy(limitRepository);

    private Service service = new Service("1", "Service 1");
    private Account account = new Account("acc_number");
    private Payment payment = new Payment(service, account, BigDecimal.ONE, Status.WAITING);

    private Answer createAnswer(Status status) {
        return invocation -> {
            Object[] args = invocation.getArguments();
            Payment p = (Payment) args[0];
            p.setStatus(status);
            return null;
        };
    }

    @Test
    public void shouldValidatePayment() {
        // given
        Limit limit = mock(Limit.class);
        doAnswer(createAnswer(Status.VALID)).when(limit).validate(payment);

        given(limitRepository.getLimits(service)).willReturn(Collections.singletonList(limit));

        // when
        payLimitPolicy.validate(payment);

        // then
        assertThat(payment.getStatus(), is(Status.VALID));
    }

    @Test
    public void shouldNotValidatePaymentWithTwoLimits() {
        // given
        Limit limit1 = mock(Limit.class);
        doAnswer(createAnswer(Status.SUBMIT_REQUIRED)).when(limit1).validate(payment);

        Limit limit2 = mock(Limit.class);
        doAnswer(createAnswer(Status.VALID)).when(limit2).validate(payment);

        given(limitRepository.getLimits(service)).willReturn(Arrays.asList(limit1, limit2));

        // when
        payLimitPolicy.validate(payment);

        // then
        assertThat(payment.getStatus(), is(Status.SUBMIT_REQUIRED));
    }

    @Test
    public void shouldValidatePaymentWithNoLimits() {
        given(limitRepository.getLimits(service)).willReturn(Collections.emptyList());

        // when
        payLimitPolicy.validate(payment);

        // then
        assertThat(payment.getStatus(), is(Status.VALID));
    }

}
