package io.arun.learning.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Test;
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
@DisableJmx(true)
public class SimpleCamelRouteMockTest extends CamelTestSupport {

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

    @Test
    public void testMoveFileMock() throws InterruptedException {
        String msg = "type,sku#,itemdescription,price\n" +
                "ADD,100,Samsung TV,500\n" +
                "ADD,101,LG TV,400";
        String fileName = "fileTest.txt";
        MockEndpoint mockEndpoint = getMockEndpoint(environment.getProperty("toRoute1"));
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived(msg);
        producerTemplate.sendBodyAndHeader(environment.getProperty("startRoute"),msg,"env",environment.getProperty("spring.profiles.active"));
        assertMockEndpointsSatisfied();
    }
}

