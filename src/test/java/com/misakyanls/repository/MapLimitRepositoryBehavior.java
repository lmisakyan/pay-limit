package com.misakyanls.repository;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.misakyanls.model.Service;
import com.misakyanls.model.limit.Limit;

public class MapLimitRepositoryBehavior {
	@Test
	public void shouldGetLimitsForSomeService() {
		// given
		MapLimitRepository limitRepository = new MapLimitRepository();
		Map<String, List<Limit>> limitsMap = new HashMap<String, List<Limit>>();
		Limit limit1 = mock(Limit.class);
		Limit limit2 = mock(Limit.class);
		limitsMap.put("1", Arrays.asList(new Limit[] { limit1, limit2 }));
		limitRepository.setLimitsMap(limitsMap);

		// when
		List<Limit> limits = limitRepository.getLimits(new Service("1",
				"Service 1"));

		// then
		assertThat(limits, is(not(nullValue())));
		assertThat(limits.get(0), is(not(nullValue())));
		assertThat(limits.size(), is(2));
	}

	@Test
	public void shouldReturnEmptyListIfNoLimitsPresent() {
		// given
		MapLimitRepository limitRepository = new MapLimitRepository();
		Map<String, List<Limit>> limitsMap = new HashMap<String, List<Limit>>();
		limitRepository.setLimitsMap(limitsMap);

		// when
		List<Limit> limits = limitRepository.getLimits(new Service("1",
				"Service 1"));

		// then
		assertThat(limits, is(not(nullValue())));
	}
}
