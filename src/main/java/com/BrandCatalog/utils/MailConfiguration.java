package com.BrandCatalog.utils;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailConfiguration {
	public void sendMail(String fileName) throws AddressException, MessagingException {
		final String username = "bhuvaneswarikmca@gmail.com";
		final String password = "bhuvi***9";

		Properties props = new Properties();
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("bhuvaneswarikmca@gmail.com"));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("bhuvaneswari@boomerangcommerce.com"));
		message.setSubject("Testing Subject");
		message.setText("PFA");

		MimeBodyPart messageBodyPart = new MimeBodyPart();

		Multipart multipart = new MimeMultipart();

		messageBodyPart = new MimeBodyPart();
		String file = "path of file to be attached";
		DataSource source = new FileDataSource(file);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(fileName);
		multipart.addBodyPart(messageBodyPart);

		message.setContent(multipart);

		System.out.println("Sending");

		Transport.send(message);

		System.out.println("Done");
	}

}
