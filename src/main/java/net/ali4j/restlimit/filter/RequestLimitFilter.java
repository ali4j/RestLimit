package net.ali4j.restlimit.filter;

import net.ali4j.restlimit.controller.RequestLimitConstants;
import net.ali4j.restlimit.structure.RequestLimitStructure;
import net.ali4j.restlimit.structure.RequestLimitStructureHolder;
import net.ali4j.restlimit.exception.RequestLimitIsReachedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@Component
public class RequestLimitFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(RequestLimitFilter.class);

    @Value("${request.limit.type}")
    private String type;
    @Value("${request.limit.headervalue}")
    private String typeValue;
    @Value("${request.limit.max}")
    private Integer max;
    @Value("${request.limit.duration.seconds}")
    private Integer duration;


    @Override
    public void init(FilterConfig filterConfig) {}
    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
//        String requestLimitType = RequestLimitConfig.getRequestLimitType();

        logger.info("RLC max number of requests:{}", max);
        logger.info("RLC duration:{}", duration);
        logger.info("RLC type:{}", type);
        logger.info("RLC type value (null if type==ip):{}", typeValue);

        if(this.checkRequestLimit(type, httpServletRequest))
            filterChain.doFilter(servletRequest, servletResponse);
        else {
            logger.info("request limit is reached");
            throw new RequestLimitIsReachedException("request limit is reached");
        }

    }


    private boolean checkRequestLimit(String requestLimitTypeConfigValue, HttpServletRequest httpServletRequest){

        if (requestLimitTypeConfigValue!=null &&
                requestLimitTypeConfigValue.equals(RequestLimitConstants.TYPE_VALUE_HEADER) ) {/*header based RL config*/

            Optional<String> requestLimitHeaderValueOptional =
                    Optional.ofNullable(httpServletRequest.getHeader(typeValue));

            /* RLCH is set in current request, this indicates that RLC is applied on this client*/
            if (requestLimitHeaderValueOptional.isPresent()) {
                Optional<RequestLimitStructure> requestLimitStructureOptional =
                        RequestLimitStructureHolder.getRequestLimitStructure(requestLimitHeaderValueOptional.get());

                if (requestLimitStructureOptional.isPresent()) {/*RLCHV already has RLC*/
                    resetCurrentClientRLConfig(requestLimitStructureOptional.get(), duration);
                    if (isLimitPassed(requestLimitStructureOptional.get(), max)) {
                        logger.info("request already has RLC, and it has passed the limit. details: {}", requestLimitHeaderValueOptional.get());
                        return false;
                    }
                    else {
                        logger.info("request already has RLC, and it is still eligible to sent requests. details: {}", requestLimitHeaderValueOptional.get());
                        requestLimitStructureOptional.get().setCurrentNumberOfRequests(
                                (requestLimitStructureOptional.get().getCurrentNumberOfRequests())+1
                        );
                        return true;
                    }

                } else { /*this is the first request of current client*/
                    RequestLimitStructureHolder.setNewRequestLimitStructure(requestLimitHeaderValueOptional.get(), duration);
                    logger.info("request doesn't have RLC, fresh one is added. RLCHV:{}", requestLimitHeaderValueOptional.get());
                    return true;
                }

            } else { /*RLCH does't exist, which means RLC is not set on this client*/
                logger.info("request doesn't have RLC header, this means RLC is not set on this client");
                return true;
            }


        } else if (
                requestLimitTypeConfigValue!=null &&
                requestLimitTypeConfigValue.equals(RequestLimitConstants.TYPE_VALUE_IP) ) { /*ip based RL config*/

            String ip = httpServletRequest.getRemoteAddr();
            Optional<RequestLimitStructure> requestLimitStructureOptional = RequestLimitStructureHolder.getRequestLimitStructure(ip);

            if (requestLimitStructureOptional.isPresent()) { /*ip already has RL config*/
                resetCurrentClientRLConfig(requestLimitStructureOptional.get(), duration);
                if (isLimitPassed(requestLimitStructureOptional.get(), max)) {
                    logger.info("request already has RL config, and it has passed the limit. details:{}", requestLimitStructureOptional.get().toString());
                    return false;
                }
                else {
                    logger.info("request already has RL config, and it is still eligible to sent request. details:{}",
                            requestLimitStructureOptional.get().toString());

                    requestLimitStructureOptional.get().setCurrentNumberOfRequests(
                            (requestLimitStructureOptional.get().getCurrentNumberOfRequests())+1
                    );

                    return true;
                }

            } else {/*first request of this ip, add new request limit config*/
                RequestLimitStructureHolder.setNewRequestLimitStructure(ip, duration);
                logger.info("request doesn't have RLC, fresh one is added. ip:{}", ip);
                return true;
            }


        } else if (requestLimitTypeConfigValue!=null)
            throw new RuntimeException("invalid request limit type value, must be header or ip");
        else return true;

    }

    static private Boolean isLimitPassed(RequestLimitStructure requestLimitStructure, Integer max){
        /*check if max number of requests has reached*/
        return requestLimitStructure.getCurrentNumberOfRequests() > max;

    }


    static private void resetCurrentClientRLConfig(RequestLimitStructure requestLimitStructure, Integer duration){

        if(requestLimitStructure.getEndOfPeriod()<System.currentTimeMillis()) {

            requestLimitStructure.setStartOfPeriod(System.currentTimeMillis());

            requestLimitStructure.setEndOfPeriod(
                    System.currentTimeMillis() +
                            (duration * 1000)
            );

            requestLimitStructure.setCurrentNumberOfRequests(1);



        }


    }




}
