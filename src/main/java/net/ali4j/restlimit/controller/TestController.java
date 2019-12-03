package net.ali4j.restlimit.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class TestController {
    private static Long RequestCounter = 0L;

    private static Logger logger = Logger.getLogger(TestController.class.getName());

    @GetMapping(value = "/testapi")
    public Long testGetHandler(){
        logger.fine("testapi is called");
        return RequestCounter++;
    }


}
