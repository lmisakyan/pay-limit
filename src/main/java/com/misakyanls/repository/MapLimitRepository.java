package com.misakyanls.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.misakyanls.model.Service;
import com.misakyanls.model.limit.Limit;

public final class MapLimitRepository implements LimitRepository {
	private Map<String, List<Limit>> limitsMap;

	public void setLimitsMap(Map<String, List<Limit>> limitsMap) {
		this.limitsMap = limitsMap;
	}

	@Override
	public List<Limit> getLimits(Service service) {
		List<Limit> result = limitsMap.get(service.getCode());
		if (result == null) {
			result = Collections.emptyList();
		}
		return result;
	}
}
