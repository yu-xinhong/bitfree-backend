package com.jihai.bitfree.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jihai.bitfree.base.enums.ReturnCodeEnum;
import com.jihai.bitfree.base.enums.UserLevelEnum;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.ConfigDAO;
import com.jihai.bitfree.entity.ConfigDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@Slf4j
public class NotifyService {

    @Autowired
    private ConfigDAO configDAO;

    public void sendNotice(String email, String password, Integer level) {
        ConfigDO emailConfig = configDAO.getByKey(Constants.EMAIL_SECRET);
        JSONObject configJson = JSON.parseObject(emailConfig.getValue());
        sendEMail(email, configJson.getString("sendMail"), configJson.getString("secret"), password, level);
        log.info("send password {} to email {}", password, email);
    }

    private void sendEMail(String toMail, String fromMail, String secret, String toContent, Integer level) {
        // 指定发送邮件的主机为 smtp.qq.com
        String host = "smtp.qq.com";  //QQ 邮件服务器
        // 获取系统属性
        Properties properties = System.getProperties();
        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);

        properties.put("mail.smtp.auth", "true");
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties,new Authenticator(){
            public PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(fromMail , secret); //发件人邮件用户名、密码
            }
        });

        try{
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(fromMail, "极海", "UTF-8"));

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toMail));

            // Set Subject: 头部头字段
            message.setSubject("欢迎加入极海开发者bitfree社区!");

            if (UserLevelEnum.COMMUNITY.getLevel().equals(level)) {
                // 设置消息体
                message.setText("你好！" + toMail + " ,你的默认密码为： " + toContent);
            } else if (UserLevelEnum.ULTIMATE.getLevel().equals(level)) {
                message.setText("你好！" + toMail + " ,你的默认密码为： " + toContent + " , Github 账号直接B站私信极海。");
            }

            // 发送消息
            Transport.send(message);
            log.info("Sent message successfully....from bitfree");
        }catch (Throwable mex) {
            log.error("发送邮件异常 需要尽快手动通知 email {}, password {}", toMail, toContent, mex);
            throw new RuntimeException(ReturnCodeEnum.SEND_MAIL_ERROR.getDesc());
        }
    }

}
