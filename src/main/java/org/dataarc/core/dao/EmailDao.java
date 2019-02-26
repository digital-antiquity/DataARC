package org.dataarc.core.dao;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.dataarc.bean.DataArcUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailDao {

    public static final String url = "http://beta.data-arc.org/a/home";

    @Autowired
    private JavaMailSender mailSender;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public String sendEmailToUsers(String subject, DataArcUser user, String text) {
        String result = null;
        MimeMessage message = mailSender.createMimeMessage();
        URL resource = getClass().getClassLoader().getResource("email-template.html");
        String template = null;
        try {
            template = IOUtils.toString(resource, Charset.forName("utf-8"));
        } catch (IOException e1) {
            logger.error("{}", e1, e1);
        }
        template = template.replace("${firstName}", user.getFirstName());
        template = template.replace("${body}", text);

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            message.setContent(template, "text/html");
            helper.setTo(user.getEmail());
            // FIXME: move to a config property
            helper.setBcc("info@data-arc.org");
            helper.setSubject(subject);
            helper.setFrom("info@data-arc.org");
            result = "success";
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new MailParseException(e);
        } finally {
            if (result != "success") {
                result = "fail";
            }
        }

        return result;

    }

    public void sendWelcomeEmail(DataArcUser user, String editorRole) {
        String text = "Welcome to dataARC!";
        sendEmailToUsers("Welcome to dataARC", user, text);
    }

    public void sendEmail(DataArcUser user, String editorRole) {

        String text = "We have granted access for you to use the combinator creation tool.  Please go to <a href=\"" + url
                + "\">a and log-in to create your first combinator!";
        sendEmailToUsers("Updated dataARC permissions", user, text);
    }

}
