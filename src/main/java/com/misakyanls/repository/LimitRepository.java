package com.misakyanls.repository;

import java.util.List;

import com.misakyanls.model.Service;
import com.misakyanls.model.limit.Limit;

public interface LimitRepository {
	List<Limit> getLimits(Service service);
}
