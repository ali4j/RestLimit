package net.ali4j.restlimit;

import net.ali4j.restlimit.config.RequestLimitConfig;
import net.ali4j.restlimit.controller.RequestLimitFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

//@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@SpringBootApplication
public class RestlimitApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestlimitApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new RequestLimitFilter());
		registration.addUrlPatterns(RequestLimitConfig.getRequestLimitPath());
		return registration;
	}


}
