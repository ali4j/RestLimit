package net.ali4j.restlimit.config;

import net.ali4j.restlimit.filter.RequestLimitFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestLimitConfig {


    @Value("${request.limit.path}")
    private String path;

    private RequestLimitFilter requestLimitFilter;


    @Autowired
    public RequestLimitConfig(RequestLimitFilter requestLimitFilter) {
        this.requestLimitFilter = requestLimitFilter;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(requestLimitFilter);
        registration.addUrlPatterns(path);
        return registration;
    }
}


