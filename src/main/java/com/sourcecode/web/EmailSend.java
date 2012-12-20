package com.sourcecode.web;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class EmailSend {

    public static void sendMail(String username, String mail) {
        MailComponent mailComponent = new MailComponent();
        String usernameTrim = StringUtils.trimToEmpty(username);
        String mailTrim = StringUtils.trimToEmpty(mail);
        String text = mailComponent.merge("com/sourcecode/web/mail.vm", "username", usernameTrim);
        Email email = new Email(mailTrim, "contact@airad.com", "大气创媒2012-12月", text, true);
        email.setPersonal("大气创媒");
        mailComponent.sendSingleMail(email);
    }

    public static void main(String[] args) throws IOException {
        Properties p = PropertiesLoaderUtils.loadAllProperties("com/sourcecode/web/name.properties");
        Set<Object> keySet = p.keySet();
        for (Object obj : keySet) {
            String email = obj.toString();
            String name = p.getProperty(email);
            System.out.println("email: " + email + "  name: " + p.getProperty(obj.toString()));
            sendMail(name, email);
        }
        // sendMail("鲍军", "toby941@gmail.com");
        //
        // String[] usernames = new String[]{"chacha", "女皇", "鸭子", "邱", "toby"};
        // String[] mailTos =
        // new String[]{"chacha@airad.com", "zhangyaxi@airad.com", "xiaoxiao@airad.com", "qiuyongquan@airad.com",
        // "toby941@gmail.com"};
        // for (int i = 0; i < usernames.length; i++) {
        // sendMail(usernames[i], mailTos[i]);
        // }
    }
}
