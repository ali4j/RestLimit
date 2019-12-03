package net.ali4j.restlimit.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestLimitStructureHolder {

    private static Map<String, RequestLimitStructure> map;


    static {
        map = new HashMap<>();
    }


    public static Optional<RequestLimitStructure> getRequestLimitStructure(String key){
        return Optional.ofNullable(map.get(key));
    }

    public static RequestLimitStructure setNewRequestLimitStructure(String key, Integer duration){
        RequestLimitStructure requestLimitStructure = RequestLimitStructure.of(System.currentTimeMillis(), System.currentTimeMillis() + (duration * 1000), 1);
        map.put(key, requestLimitStructure);
        return map.get(key);
    }
}
