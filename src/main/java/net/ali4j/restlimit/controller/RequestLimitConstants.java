package net.ali4j.restlimit.controller;

public interface RequestLimitConstants {
    String
        PATH        = "request.limit.path",
        TYPE        = "request.limit.type",
        HEADERVALUE = "request.limit.headervalue",
        MAX         = "request.limit.max",
        DURATION    = "request.limit.duration.seconds",
        TYPE_VALUE_HEADER = "header",
        TYPE_VALUE_IP = "ip";
}
