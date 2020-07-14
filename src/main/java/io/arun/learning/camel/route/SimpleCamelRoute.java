package io.arun.learning.camel.route;

import io.arun.learning.camel.alert.MailProcessor;
import io.arun.learning.camel.domain.Item;
import io.arun.learning.camel.exception.DataException;
import io.arun.learning.camel.processor.BuildSQLProcessor;
import io.arun.learning.camel.processor.SuccessProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.BeanInject;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
public class SimpleCamelRoute extends RouteBuilder {

    @Autowired
    Environment env;

    @Qualifier("dataSource")
    @Autowired
    DataSource dataSource;

    @Autowired
    BuildSQLProcessor sqlProcessor;

    @Autowired
    SuccessProcessor successProcessor;

    @Autowired
    MailProcessor mailProcessor;

    @Override
    public void configure() throws Exception {

        DataFormat bindy = new BindyCsvDataFormat(Item.class);

        errorHandler(deadLetterChannel("log:errorInRoute?level=ERROR&showProperties=true").maximumRedeliveries(3).redeliveryDelay(3000)
                                    .backOffMultiplier(2).retryAttemptedLogLevel(LoggingLevel.ERROR));

        onException(PSQLException.class).log(LoggingLevel.ERROR,"PSQLException in Route: ${body}")
                .maximumRedeliveries(3).redeliveryDelay(3000).backOffMultiplier(2).retryAttemptedLogLevel(LoggingLevel.ERROR);

        onException(DataException.class).log(LoggingLevel.ERROR,"DataException in Route: ${body}").process(mailProcessor)
               /* .maximumRedeliveries(3).redeliveryDelay(3000).backOffMultiplier(2).retryAttemptedLogLevel(LoggingLevel.ERROR)*/;

        from("{{startRoute}}").routeId("mainRoute")
                .log("Timer invoked and the message is "+env.getProperty("message"))
                .choice()
                    .when(header("env").isNotEqualTo("mock"))
                        .pollEnrich("{{fromRoute}}")
                    .otherwise()
                        .log("mock env flow and the body is ${body}")
                .end()
                .to("{{toRoute1}}")
                .unmarshal(bindy)
                .log("Unmarshelled -> ${body}")
                .split(body())
                    .log(" Record is ${body}")
                    .process(sqlProcessor)
                    .to("{{dbRoute1}}")
                .end()
            .process(successProcessor)
            .to("{{toRoute2}}");

    }

}
