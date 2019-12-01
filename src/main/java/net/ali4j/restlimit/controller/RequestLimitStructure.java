package net.ali4j.restlimit.controller;

import net.ali4j.restlimit.config.RequestLimitConfig;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class RequestLimitStructure {

    private Long startOfPeriod;
    private Long endOfPeriod;
    private Integer currentNumberOfRequests;

    public static RequestLimitStructure getInstanceWithDefaultValues(){
        RequestLimitStructure requestLimitStructure = new RequestLimitStructure();
        requestLimitStructure.startOfPeriod = System.currentTimeMillis();
        requestLimitStructure.endOfPeriod =  requestLimitStructure.startOfPeriod + (RequestLimitConfig.getRequestLimitDuration()*1000);
        requestLimitStructure.currentNumberOfRequests = 1;
        return requestLimitStructure;
    }

    public static RequestLimitStructure of(Long currentStartDate, Long currentEndDate, Integer currentNumberOfRequests){
        RequestLimitStructure requestLimitStructure = new RequestLimitStructure();
        requestLimitStructure.startOfPeriod = currentStartDate;
        requestLimitStructure.endOfPeriod = currentEndDate;
        requestLimitStructure.currentNumberOfRequests = currentNumberOfRequests;
        return requestLimitStructure;
    }

    public Long getStartOfPeriod() {
        return startOfPeriod;
    }

    public void setStartOfPeriod(Long startOfPeriod) {
        this.startOfPeriod = startOfPeriod;
    }

    public Long getEndOfPeriod() {
        return endOfPeriod;
    }

    public void setEndOfPeriod(Long endOfPeriod) {
        this.endOfPeriod = endOfPeriod;
    }

    public Integer getCurrentNumberOfRequests() {
        return currentNumberOfRequests;
    }

    public void setCurrentNumberOfRequests(Integer currentNumberOfRequests) {
        this.currentNumberOfRequests = currentNumberOfRequests;
    }

    @Override
    public String toString() {
        return "RequestLimitStructure{" +
                "startOfPeriod=" + ZonedDateTime.ofInstant(Instant.ofEpochMilli(startOfPeriod), ZoneId.systemDefault()) +
                ", endOfPeriod=" + ZonedDateTime.ofInstant(Instant.ofEpochMilli(endOfPeriod), ZoneId.systemDefault()) +
                ", currentNumberOfRequests=" + currentNumberOfRequests +
                '}';
    }
}
