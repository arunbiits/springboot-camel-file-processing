package io.arun.learning.camel.alert;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MailProcessor implements Processor {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    Environment environment;


    @Override
    public void process(Exchange exchange) throws Exception {
        Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT,Exception.class);
        log.info("Exception caught in mail processor -> {}",exception.getMessage());

        String msgBody = "Exception caught in route" + exception.getMessage();
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(environment.getProperty("mailFrom"));
        simpleMailMessage.setTo(environment.getProperty("mailTo"));
        simpleMailMessage.setSubject("Exception caught in Route");
        simpleMailMessage.setText(msgBody);

        javaMailSender.send(simpleMailMessage);
    }
}
