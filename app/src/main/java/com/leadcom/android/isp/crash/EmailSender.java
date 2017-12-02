package com.leadcom.android.isp.crash;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * <p>
 * <b>发送邮件 EmailSender 可以当作一个工具类来用，直接调用方法 发邮件是耗时操作，记得起个新线程</b>
 * </p>
 * <h2>例子:</h2>
 * <p/>
 * <pre class="prettyprint">
 * EmailSender sender = new EmailSender();
 * // 设置服务器地址和端口
 * sender.setProperties(&quot;smtp.126.com&quot;, &quot;25&quot;);
 * // 分别设置发件人，邮件标题和文本内容
 * sender.setMessage(&quot;******@126.com&quot;, &quot;Email send test&quot;, &quot;This is isFromMe JavaMail !&quot;);
 * // 设置收件人
 * sender.setReceiver(new String[] { &quot;******@gmail.com&quot;, &quot;******@qq.com&quot; });
 * // 添加附件
 * sender.addAttachment(&quot;/sdcard/debug.txt&quot;);
 * // 发送邮件
 * sender.sendEmail(&quot;smtp.126.com&quot;, &quot;******@126.com&quot;, &quot;密码******&quot;);
 * </pre>
 */
public class EmailSender {
    private Properties properties;
    private Session session;
    private Message message;
    private MimeMultipart multipart;
    private String mailHost;

    public EmailSender() {
        super();
        this.properties = new Properties();
    }

    public void setProperties(String host, String port) {
        mailHost = host;
        // 地址
        this.properties.put("mail.smtp.host", host);
        // 端口号
        if (!port.equals("25")) {
            this.properties.put("mail.smtp.port", port);
        }
        // 是否验证
        this.properties.put("mail.smtp.auth", true);
        this.properties.put("mail.smtp.starttls.enable", "true");
//        this.properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        this.properties.put("mail.smtp.socketFactory.fallback", "false");

//        this.session = Session.getInstance(properties, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(account, pwd);//super.getPasswordAuthentication();
//            }
//        });
        this.session = Session.getInstance(properties);
        this.message = new MimeMessage(session);
        this.multipart = new MimeMultipart("mixed");
    }

    /**
     * 设置收件人
     *
     * @param receiver receiver
     * @throws MessagingException
     */
    public void setReceiver(String[] receiver) throws MessagingException {
        if (null == receiver || receiver.length < 1) {
            throw new IllegalArgumentException("Mail sender need to set one(or more)receiver.");
        }
        Address[] address = new InternetAddress[receiver.length];
        for (int i = 0; i < receiver.length; i++) {
            address[i] = new InternetAddress(receiver[i]);
        }
        this.message.setRecipients(Message.RecipientType.TO, address);
    }

    /**
     * 设置收件人
     *
     * @param receivers receiver
     * @throws MessagingException
     */
    public void setReceiver(List<String> receivers) throws MessagingException {
        if (null == receivers || receivers.size() < 1)
            throw new IllegalArgumentException("Mail sender need to set one(or more)receiver.");
        String[] temp = new String[receivers.size()];
        temp = receivers.toArray(temp);
        setReceiver(temp);
    }

    /**
     * 设置邮件
     *
     * @param from    来源
     * @param title   标题
     * @param content 内容
     * @throws AddressException
     * @throws MessagingException
     */
    public void setMessage(String from, String title, String content) throws MessagingException {
        this.message.setFrom(new InternetAddress(from));
        this.message.setSubject(title);
        // 纯文本的话用setText()就行，不过有附件就显示不出来内容了
        MimeBodyPart textBody = new MimeBodyPart();
        textBody.setContent(content, "text/html;charset=gbk");
        this.multipart.addBodyPart(textBody);
    }

    /**
     * 添加附件
     *
     * @param filePath 文件路径
     * @throws MessagingException
     */
    public void addAttachment(String filePath) throws MessagingException {
        FileDataSource fileDataSource = new FileDataSource(new File(filePath));
        DataHandler dataHandler = new DataHandler(fileDataSource);
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDataHandler(dataHandler);
        mimeBodyPart.setFileName(fileDataSource.getName());
        this.multipart.addBodyPart(mimeBodyPart);
    }

    /**
     * 发送邮件
     *
     * @param account 账户名
     * @param pwd     密码
     * @throws MessagingException
     */
    public void sendEmail(String account, String pwd) throws MessagingException {
        try {
            // 发送时间
            this.message.setSentDate(new Date());
            // 发送的内容，文本和附件
            this.message.setContent(this.multipart);
            this.message.saveChanges();
            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-Java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(mc);
            // 创建邮件发送对象，并指定其使用SMTP协议发送邮件
            Transport transport = session.getTransport("smtp");
            // 登录邮箱
            transport.connect(mailHost, account, pwd);
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            // 关闭连接
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
