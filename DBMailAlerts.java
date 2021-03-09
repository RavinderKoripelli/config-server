package com.ApolloCrownJob;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DBMailAlerts {

	private static Session office365_SMTP_session1;

	private static final String office365_SMTP_USER_NAME1 = "askapollo@apollohospitals.com";
	private static final String office365_SMTP_PASSWORD1 = "AskDigital@4321";

	public static void main(String args[]) {

		try {

			//String toAddress = "919555973598.healthnet@smscountry.net,919032947406.healthnet@smscountry.net,919701979482.healthnet@smscountry.net";
			//String ccAddress = "natarajan_rk@apollohospitals.com,anurag_v@apollohospitals.com,mohanakrishna_s@apollohospitals.com,pavankumar_g@apollohospitals.com,lasyasudha_n@apollohospitals.com,ravinder_kp@apollohospitals.com";
			 String toAddress = "ravinder_kp@apollohospitals.com";
			 String ccAddress = "ravinder.koripelli@gmail.com";

			String[] toAddressList = toAddress.replaceAll(" ", "").split(",");
			InternetAddress[] torecipientAddress = new InternetAddress[toAddressList.length];
			int counter = 0;
			for (String recipient : toAddressList) {
				torecipientAddress[counter] = new InternetAddress(recipient.trim());
				counter++;
			}

			String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
			InternetAddress[] ccrecipientAddress = new InternetAddress[ccAddressList.length];
			int cccounter = 0;
			for (String recipient : ccAddressList) {
				ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
				cccounter++;
			}

			Session sessionObject = DBMailAlerts.getOfficeSMTPSession1();
			Message message = new MimeMessage(sessionObject);

			message.setFrom(new InternetAddress("askapollo@apollohospitals.com", "AskApollo"));
			message.setRecipients(Message.RecipientType.TO, torecipientAddress);
			message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
			message.setSubject("Reg:DB Backup Reminder for Prod & Stag Server !!!");
			message.setContent("DB Backup has been completed successfully...", "text/html");

			Transport.send(message);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Session getOfficeSMTPSession1() {

		if (office365_SMTP_session1 == null) {

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.office365.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.localhost", "127.0.0.1");

			office365_SMTP_session1 = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(office365_SMTP_USER_NAME1, office365_SMTP_PASSWORD1);
				}
			});

		}

		return office365_SMTP_session1;
	}

}
