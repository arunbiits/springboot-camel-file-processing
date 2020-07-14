package io.arun.learning.camel.route;

import io.arun.learning.camel.processor.HealthCheckProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("mock")
@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class HealthCheckRouteTest extends CamelTestSupport {

    @Autowired
    CamelContext camelContext;

    @Autowired
    Environment environment;

    @Autowired
    ProducerTemplate producerTemplate;


    @Autowired
    protected CamelContext createCamelContext() {
        return camelContext;
    }

    @Autowired
    HealthCheckProcessor healthCheckProcessor;

    @Override
    public RouteBuilder createRouteBuilder(){
        return new HealthCheckRoute();
    }

    @Before
    public void setUp(){

    }

    @Test
    public void healthRouteTest() {
        String input = "{\"status\":\"UP\"}";
        System.out.println(environment.getProperty("spring.profiles.active"));
        String response =(String) producerTemplate.requestBodyAndHeader(environment.getProperty("healthRoute"),input,"env", environment.getProperty("spring.profiles.active"));
        System.out.println("Response -> "+response);
        assertEquals(input,response);
    }
}
