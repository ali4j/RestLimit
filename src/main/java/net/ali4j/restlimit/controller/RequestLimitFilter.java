package net.ali4j.restlimit.controller;

import net.ali4j.restlimit.config.RequestLimitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

public class RequestLimitFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(RequestLimitFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {}
    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestLimitType = RequestLimitConfig.getRequestLimitType();

        logger.info("RLC max number of requests:{}", RequestLimitConfig.getRequestLimitMax());
        logger.info("RLC duration:{}", RequestLimitConfig.getRequestLimitDuration());
        logger.info("RLC type:{}", requestLimitType);
        logger.info("RLC type value (null if type==ip):{}", RequestLimitConfig.getRequestLimitHeaderValue());

        if(this.checkRequestLimit(requestLimitType, httpServletRequest))
            filterChain.doFilter(servletRequest, servletResponse);
        else {
            logger.info("request limit is reached");
//            throw new RequestLimitIsReachedException("request limit is reached");
        }

    }


    private boolean checkRequestLimit(String requestLimitTypeConfigValue, HttpServletRequest httpServletRequest){

        if (requestLimitTypeConfigValue!=null &&
                requestLimitTypeConfigValue.equals(RequestLimitConstants.TYPE_VALUE_HEADER) ) {/*header based RL config*/

            Optional<String> requestLimitHeaderValueOptional =
                    Optional.ofNullable(httpServletRequest.getHeader(RequestLimitConfig.getRequestLimitHeaderValue()));

            /* RLCH is set in current request, this indicates that RLC is applied on this client*/
            if (requestLimitHeaderValueOptional.isPresent()) {
                Optional<RequestLimitStructure> requestLimitStructureOptional =
                        RequestLimitStructureHolder.getRequestLimitStructure(requestLimitHeaderValueOptional.get());

                if (requestLimitStructureOptional.isPresent()) {/*RLCHV already has RLC*/
                    resetCurrentClientRLConfig(requestLimitStructureOptional.get());
                    if (isLimitPassed(requestLimitStructureOptional.get())) {
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
                    RequestLimitStructureHolder.setNewRequestLimitStructure(requestLimitHeaderValueOptional.get());
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
                resetCurrentClientRLConfig(requestLimitStructureOptional.get());
                if (isLimitPassed(requestLimitStructureOptional.get())) {
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
                RequestLimitStructureHolder.setNewRequestLimitStructure(ip);
                logger.info("request doesn't have RLC, fresh one is added. ip:{}", ip);
                return true;
            }


        } else if (requestLimitTypeConfigValue!=null)
            throw new RuntimeException("invalid request limit type value, must be header or ip");
        else return true;

    }

    static private Boolean isLimitPassed(RequestLimitStructure requestLimitStructure){
        /*check if max number of requests has reached*/
        if (requestLimitStructure.getCurrentNumberOfRequests()>RequestLimitConfig.getRequestLimitMax())
            return true;

//        if ( (requestLimitStructure.getEndOfPeriod()<=System.currentTimeMillis()))
//            return false;
        return false;
    }


    static private void resetCurrentClientRLConfig(RequestLimitStructure requestLimitStructure){

        if(requestLimitStructure.getEndOfPeriod()<System.currentTimeMillis()) {

            requestLimitStructure.setStartOfPeriod(System.currentTimeMillis());

            requestLimitStructure.setEndOfPeriod(
                    System.currentTimeMillis() +
                            (RequestLimitConfig.getRequestLimitDuration() * 1000)
            );

            requestLimitStructure.setCurrentNumberOfRequests(1);



        }


    }




}
