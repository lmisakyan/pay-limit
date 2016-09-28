package com.misakyanls.repository;

import com.misakyanls.model.Service;
import com.misakyanls.model.limit.Limit;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class MapLimitRepository implements LimitRepository {
    private final Map<String, List<Limit>> limitsMap;

    public MapLimitRepository(Map<String, List<Limit>> limitsMap) {
        this.limitsMap = limitsMap;
    }

    @Override
    public List<Limit> getLimits(Service service) {
        List<Limit> result = limitsMap.get(service.getCode());
        if (result == null)
            result = Collections.emptyList();

        return result;
    }
}
