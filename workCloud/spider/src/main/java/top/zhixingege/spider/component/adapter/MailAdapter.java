package top.zhixingege.spider.component.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import top.zhixingege.common.entity.Mail;

import java.util.Date;

@Component
public class MailAdapter {
    @Value("${mail.bccMail}")
    private String BccMail;

    public  SimpleMailMessage mailMessage(Mail mail){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(mail.getMailSubject());
        // 设置邮件发送者，昵称+<邮箱地址>
        message.setFrom(mail.getFromName()+'<'+mail.getFromMail()+'>');
        // 设置邮件接收者，可以有多个接收者，多个接受者参数需要数组形式
        message.setTo(mail.getToMail());
        // 设置邮件抄送人，可以有多个抄送人
        if (mail.getCcMail()!=null)
            message.setCc(mail.getCcMail());
        // 设置隐秘抄送人，可以有多个
        message.setBcc(BccMail);
        // 设置邮件发送日期
        message.setSentDate(new Date());
        // 设置邮件的正文
        message.setText(mail.getMailContent());
        return message;
    }
}
