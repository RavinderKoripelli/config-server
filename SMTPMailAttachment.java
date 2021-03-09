package com.ApolloCrownJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.sun.mail.smtp.SMTPTransport;

public class SMTPMailAttachment {
	
	private static final String office365_SMTP_USER_NAME1 = "askapollo@apollohospitals.com";
	private static final String office365_SMTP_PASSWORD1 = "apollo@1234";

	private static Session office365Session1;
	
	public static Session getOffice365SMTPSession1() {

		if (office365Session1 == null) {

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.office365.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.localhost", "127.0.0.1");

			office365Session1 = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(office365_SMTP_USER_NAME1, office365_SMTP_PASSWORD1);
				}
			});

		}

		return office365Session1;
	}
	
	public static void main(String args[]) {
		
		office365SMTP1();
		
	}
	
	
	public static void office365SMTP1() {
		
		String attachmentName = "/tmp/Capture.PNG";
				
		try {
						
			String from = "askapollo@apollohospitals.com";
			
			String toAddress = "alluraviteja30@gmail.com";
						
			Session office365SessionObject = getOffice365SMTPSession1();
			Message message = new MimeMessage(office365SessionObject);
		     
			   
			message.setFrom(new InternetAddress(from, "Ask Apollo"));
		    message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
			message.setSubject("Test Subject");

			message.setSentDate(new Date());
			
			BodyPart messageBodyPart = new MimeBodyPart();
		    messageBodyPart.setText("This is message body");
			
		    String[] noOfAttachments = attachmentName.split(",");

		    BodyPart messageBodyPart1 = null;
		    DataSource source;
		    Multipart multipart = new MimeMultipart();
		    
		    for (int i = 0; i < noOfAttachments.length; i++) {
		    	    
			    messageBodyPart1 = new MimeBodyPart();
			    //source = new FileDataSource(noOfAttachments[i]);
			    InputStream is = new FileInputStream(noOfAttachments[i]);
			    
			    ByteArrayDataSource ds = new ByteArrayDataSource(is, "application/pdf"); 
			    messageBodyPart1.setDataHandler(new DataHandler(ds));
			    
			    messageBodyPart1.setFileName(noOfAttachments[i]);
			    
			    multipart.addBodyPart(messageBodyPart1);
			    
			}
		    
		    multipart.addBodyPart(messageBodyPart);
		    
		    message.setContent(multipart);

			try{
				   
				SMTPTransport transport = (SMTPTransport) office365SessionObject.getTransport("smtp");
				        
				transport.connect("smtp.office365.com",office365_SMTP_USER_NAME1, office365_SMTP_PASSWORD1);
				SMTPTransport.send(message);
				
				if (transport.getLastReturnCode() == 235) {
			  	    System.out.println("SUCCESS");
				
				  }
			}catch(Exception e){
			   e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
