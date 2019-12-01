package net.ali4j.restlimit.config;

import net.ali4j.restlimit.annotation.RequestLimit;
import net.ali4j.restlimit.controller.RequestLimitConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RequestLimitConfig {

    static private Properties props;

    static {
        try {
            InputStream resourceAsStream = RequestLimit.class.getClassLoader().getResourceAsStream("requestlimit.properties");
            props = new Properties();
            props.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getRequestLimitPath() {
        return (String)props.get(RequestLimitConstants.PATH);
    }

    public static String getRequestLimitType() {
        return (String)props.get(RequestLimitConstants.TYPE);
    }

    public static String getRequestLimitHeaderValue() {
        return (String)props.get(RequestLimitConstants.HEADERVALUE);
    }

    public static Integer getRequestLimitMax() {
        return Integer.valueOf((String)props.get(RequestLimitConstants.MAX));
    }

    public static Integer getRequestLimitDuration() {
        return Integer.valueOf((String)props.get(RequestLimitConstants.DURATION));
    }
}
