package com.lnbinfotech.lnb_tickets.mail;

// Created by lnb on 8/23/2017.

import com.lnbinfotech.lnb_tickets.constant.Constant;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GMailSender extends javax.mail.Authenticator {
    //private String mailhost = "smtp.gmail.com";
    private String mailhost = "mail.lnbinfotech.com";
    private String user;
    private String password;
    private Session session;
    private Multipart _multipart;

    static {
        Security.addProvider(new com.lnbinfotech.lnb_tickets.mail.JSSEProvider());
    }

    public GMailSender(String user, String password) {
        this.user = user;
        this.password = password;
        _multipart = new MimeMultipart();
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        //props.setProperty("mail.host", mailhost);
        props.setProperty("mail.smtp.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        /*props.setProperty("mail.smtp.quitwait", "false");
        props.setProperty("mail.defaultEncoding","UTF-8");
        props.setProperty("mail.smtp.auth","true");
        props.setProperty("mail.smtp.starttls.required","true");
        props.setProperty("mail.smtp.starttls.enable","true");*/

        session = Session.getDefaultInstance(props, this);
    }

    public void addAttachment(String filename,String _filename,String body) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(_filename);
        _multipart.addBodyPart(messageBodyPart);
        BodyPart messageBodyPart1 = new MimeBodyPart();
        messageBodyPart1.setText(body);
        _multipart.addBodyPart(messageBodyPart1);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients) throws Exception {
        //try{
        MimeMessage message = new MimeMessage(session);
        //DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        //message.setDataHandler(handler);
            /*InternetAddress[] addressTo = new InternetAddress[recipients.length];
            for (int i = 0; i < recipients.length; i++) {
                addressTo[i] = new InternetAddress(recipients[i]);
            }*/
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipients));
        //message.addRecipient(MimeMessage.RecipientType.CC, new InternetAddress("patilanup20@gmail.com"));
        //message.addRecipient(MimeMessage.RecipientType.BCC, new InternetAddress("patilanup20@gmail.com"));

            /*if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));*/
        message.setContent(_multipart);
        Transport.send(message);
        /*}catch(Exception e){
            e.printStackTrace();
        }*/
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}
