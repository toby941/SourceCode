package com.sourcecode.web;

public class EmailSend {

    public static void sendMail(String username, String mailTo) {
        MailComponent mailComponent = new MailComponent();
        String text = mailComponent.merge("com/sourcecode/web/mail.vm", "username", username);
        Email email = new Email(mailTo, "devsupport@airad.com", "看完此邮件请QQ联系 toby 确认效果是否OK 只看不作为的今天下班被风刮跑", text, true);
        mailComponent.sendSingleMail(email);
    }

    public static void main(String[] args) {
        String[] usernames = new String[]{"chacha", "女皇", "鸭子", "邱", "toby"};
        String[] mailTos =
                new String[]{"chacha@airad.com", "zhangyaxi@airad.com", "xiaoxiao@airad.com", "qiuyongquan@airad.com",
                        "toby941@gmail.com"};
        for (int i = 0; i < usernames.length; i++) {
            sendMail(usernames[i], mailTos[i]);
        }
    }
}
