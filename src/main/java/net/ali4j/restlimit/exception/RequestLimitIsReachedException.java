package net.ali4j.restlimit.exception;

public class RequestLimitIsReachedException extends RuntimeException {
    public RequestLimitIsReachedException(String message) {
        super(message);
    }
}
