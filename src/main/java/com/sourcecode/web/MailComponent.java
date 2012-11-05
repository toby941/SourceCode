/*
 * Copyright 2011 Mitian Technology, Co., Ltd. All rights reserved.
 */
package com.sourcecode.web;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

/**
 * MailComponent.java
 * 
 * @author baojun
 */
@Service
public class MailComponent {
    private final Logger logger = Logger.getLogger("mail_log");

    @Autowired
    @Qualifier("mailSender")
    private JavaMailSenderImpl mailSender;// 注入Spring封装的javamail，Spring的ml中已让框架装配

    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;// 注入Spring封装的异步执行器private Log log =
                                      // LogFactory.getLog(getClass());

    public String merge(String path, String key, String value) {
        VelocityEngine ve = new VelocityEngine();
        // 设置模板加载路径，这里设置的是class下
        ve.setProperty(Velocity.RESOURCE_LOADER, "class");
        ve.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        ve.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init();

        /* lets make a Context and put data into it */

        VelocityContext context = new VelocityContext();

        context.put(key, value);
        /* lets render a template */

        StringWriter w = new StringWriter();
        Template t = ve.getTemplate(path, "utf-8");
        t.merge(context, w);
        return w.toString();

    }

    @Autowired
    private VelocityEngine velocityEngine;

    public VelocityEngine getVelocityEngine() {
        if (velocityEngine == null) {

            java.util.Properties properties = new java.util.Properties();
            java.util.Properties subProperties = new java.util.Properties();
            subProperties.put("input.encoding", "UTF-8");
            subProperties.put("output.encoding", "UTF-8");
            properties.put("resourceLoaderPath", "classpath:/com/sourcecode/web");
            properties.put("velocityProperties", subProperties);
            VelocityEngine engine = new VelocityEngine(properties);
            velocityEngine = engine;
        }
        return velocityEngine;
    }

    /**
     * @return the mailSender
     */
    public JavaMailSender getMailSender() {
        if (mailSender == null) {
            mailSender = new JavaMailSenderImpl();
            mailSender.setHost("mail.airad.com");
            mailSender.setUsername("devsupport@airad.com");
            mailSender.setPassword("xxx123456");
            java.util.Properties properties = new java.util.Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.timeout", "25000");
            properties.put("mail.mime.charset", "UTF-8");
            properties.put("mail.smtp.starttls.enable", "true");
            mailSender.setJavaMailProperties(properties);
        }
        return mailSender;
    }

    /**
     * @return the taskExecutor
     */
    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public void sendMail(List<Email> emails) throws IOException {
        if (CollectionUtils.isEmpty(emails)) {
            return;
        }
        if (emails.size() > 5) {// 收件人大于5封时，采用异步发送
            sendMailByAsynchronousMode(emails);
        }
        else {
            sendMailBySynchronizationMode(emails);
        }
    }

    /**
     * 异步发送
     * 
     * @see com.zhangjihao.service.MailService#sendMailByAsynchronousMode(com.zhangjihao.bean.Email)
     */
    public void sendMailByAsynchronousMode(final List<Email> emails) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMailBySynchronizationMode(emails);
                }
                catch (Exception e) {
                    logger.info("send AsynMail error ", e);
                }
            }
        });
    }

    /**
     * 同步发送
     * 
     * @throws IOException
     * @see com.zhangjihao.service.MailServiceMode#sendMail(com.zhangjihao.bean.Email)
     */
    public void sendMailBySynchronizationMode(List<Email> emails) throws IOException {
        for (final Email email : emails) {
            try {
                sendSingleMail(email);
            }
            catch (Exception e) {
                logger.error("sendMailBySynchronizationMode error,mail info" + email.toLogString(), e);
            }
        }
    }

    /**
     * 统一应用捕获运行时异常的邮件通知入口
     * 
     * @param e
     * @param subject
     * @param mailToAddress
     */
    public void sendExceptionMail(Exception e, String subject, String mailToAddress) {
        Email email =
                new Email(mailToAddress, "contact@airad.com", subject, ExceptionUtils.getFullStackTrace(e), false);
        sendSingleMail(email);
    }

    /**
     * 统一应用捕获运行时异常的邮件通知入口
     * 
     * @param e
     * @param subject
     * @param mailToAddress
     * @param cc
     */
    public void sendExceptionMail(Exception e, String subject, String mailToAddress, String cc) {
        Email email =
                new Email(mailToAddress, cc, "contact@airad.com", subject, ExceptionUtils.getFullStackTrace(e), false);
        sendSingleMail(email);
    }

    /**
     * 单封邮件同步发送
     * 
     * @param email
     */
    public void sendSingleMail(final Email email) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "utf-8");
                message.setSubject(email.getSubject());
                message.setSentDate(email.getCreateTime());
                message.setFrom(email.getFromAddress(), email.getPersonal());
                if (StringUtils.isNotBlank(email.getCc())) {
                    message.setCc(email.getCc());
                }
                message.setTo(email.getToAddress());
                // MimeUtility
                message.setText(email.getContent(), email.isHtml());
            }
        };
        getMailSender().send(preparator);
    }

    /**
     * 单封邮件异步发送
     * 
     * @param email
     */
    public void sendSingleMailByAsynchronousMode(final Email email) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendSingleMail(email);
                }
                catch (Exception e) {
                    logger.error("sendSingleMailByAsynchronousMode error ", e);
                }
            }
        });
    }

    /**
     * @param mailSender the mailSender to set
     */
    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * @param taskExecutor the taskExecutor to set
     */
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
