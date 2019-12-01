package net.ali4j.restlimit.controller;

import net.ali4j.restlimit.config.RequestLimitConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;

import javax.servlet.Filter;

//@Component
public class RequestLimitHandler implements ApplicationContextAware, InitializingBean {

    private FilterRegistrationBean filterRegistrationBean;

    @Autowired
    public RequestLimitHandler(@Lazy FilterRegistrationBean filterRegistrationBean) {
        this.filterRegistrationBean = filterRegistrationBean;
    }

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Filter filter = new RequestLimitFilter();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.addUrlPatterns(RequestLimitConfig.getRequestLimitPath());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }
}
