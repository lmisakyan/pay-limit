package com.misakyanls.model.limit;

import com.misakyanls.model.Payment;

public interface Limit {
	void validate(Payment payment);
}
