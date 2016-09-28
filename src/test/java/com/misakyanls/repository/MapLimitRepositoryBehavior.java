package com.misakyanls.repository;

import com.misakyanls.model.Service;
import com.misakyanls.model.limit.Limit;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class MapLimitRepositoryBehavior {
    @Test
    public void shouldGetLimitsForSomeService() {
        // given
        Map<String, List<Limit>> limitsMap = new HashMap<>();
        Limit limit1 = mock(Limit.class);
        Limit limit2 = mock(Limit.class);
        limitsMap.put("1", Arrays.asList(limit1, limit2));
        MapLimitRepository limitRepository = new MapLimitRepository(limitsMap);

        // when
        List<Limit> limits = limitRepository.getLimits(new Service("1",
                "Service 1"));

        // then
        assertThat(limits.size(), is(2));
        assertThat(limits.get(0), is(not(nullValue())));
    }

    @Test
    public void shouldReturnEmptyListIfNoLimitsPresent() {
        // given
        Map<String, List<Limit>> limitsMap = new HashMap<>();
        MapLimitRepository limitRepository = new MapLimitRepository(limitsMap);

        // when
        List<Limit> limits = limitRepository.getLimits(new Service("1",
                "Service 1"));

        // then
        assertThat(limits, is(not(nullValue())));
    }
}
