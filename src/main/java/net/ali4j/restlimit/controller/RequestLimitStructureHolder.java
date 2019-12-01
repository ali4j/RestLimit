package net.ali4j.restlimit.controller;

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

    public static RequestLimitStructure setNewRequestLimitStructure(String key){
        map.put(key, RequestLimitStructure.getInstanceWithDefaultValues());
        return map.get(key);
    }
}
