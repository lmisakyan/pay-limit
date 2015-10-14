package com.misakyanls.service;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.misakyanls.model.Account;
import com.misakyanls.model.Payment;
import com.misakyanls.model.Payment.Status;
import com.misakyanls.model.Service;
import com.misakyanls.model.limit.Limit;
import com.misakyanls.repository.LimitRepository;

public class PayLimitPolicyBehavior {
	private LimitRepository limitRepository = mock(LimitRepository.class);

	private PayLimitPolicy payLimitPolicy = new PayLimitPolicy();
	private Service service = new Service("1", "Service 1");
	private Account account = new Account("acc_number");
	private Payment payment = new Payment(service, account, BigDecimal.ONE, Status.WAITING);

	@Test
	public void shouldValidatePayment() {
		// given
		Limit limit = mock(Limit.class);
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				Payment p = (Payment) args[0];
				p.setStatus(Status.VALID);
				return null;
			}
		}).when(limit).validate(payment);

		given(limitRepository.getLimits(service)).willReturn(Arrays.asList(new Limit[] { limit }));
		payLimitPolicy.setLimitRepository(limitRepository);

		// when
		payLimitPolicy.validate(payment);

		// then
		assertThat(payment.getStatus(), is(Status.VALID));
	}

	@Test
	public void shouldNotValidatePaymentWithTwoLimits() {
		// given
		Limit limit1 = mock(Limit.class);
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				Payment p = (Payment) args[0];
				p.setStatus(Status.SUBMIT_REQUIRED);
				return null;
			}
		}).when(limit1).validate(payment);

		Limit limit2 = mock(Limit.class);
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				Payment p = (Payment) args[0];
				p.setStatus(Status.VALID);
				return null;
			}
		}).when(limit2).validate(payment);

		given(limitRepository.getLimits(service)).willReturn(Arrays.asList(new Limit[] { limit1, limit2 }));
		payLimitPolicy.setLimitRepository(limitRepository);

		// when
		payLimitPolicy.validate(payment);

		// then
		assertThat(payment.getStatus(), is(Status.SUBMIT_REQUIRED));
	}

	@Test
	public void shouldValidatePaymentWithNoLimits() {
		given(limitRepository.getLimits(service)).willReturn(Collections.emptyList());
		payLimitPolicy.setLimitRepository(limitRepository);

		// when
		payLimitPolicy.validate(payment);

		// then
		assertThat(payment.getStatus(), is(Status.VALID));
	}

}
