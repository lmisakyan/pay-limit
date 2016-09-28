package com.misakyanls.limit;

import java.math.BigDecimal;

import com.misakyanls.model.Account;
import com.misakyanls.model.Payment;
import com.misakyanls.model.Service;
import com.misakyanls.model.Payment.Status;

public class AllLimitHelper {
	private static final Service service = new Service("1", "Service 1");
	private static final Account account = new Account("acc_number");

	public static Payment createPayment() {
		return new Payment(service, account, BigDecimal.ONE, Status.WAITING);
	}
}
