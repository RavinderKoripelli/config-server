package com.ApolloCrownJob;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SMTPServerAlerts {

	private static Session office365_SMTP_session1;
	private static final String office365_SMTP_USER_NAME1 = "askapollo@apollohospitals.com";
	private static final String office365_SMTP_PASSWORD1 = "apollo@1234";
	public static final String from = "askapollo@apollohospitals.com";

	public static void main(String args[]) {

		String subject = args[0];
		String content = args[1];
		Session sessionObject = getOfficeSMTPSession1();
		Message message = new MimeMessage(sessionObject);
		InternetAddress[] torecipientAddress;
		InternetAddress[] ccrecipientAddress;

		if (content.contains("Edoc")) {
			try {
				String toAddress = "edocsupport@quadone.com,venubabu@quadone.com,sandeep.a@hng.co.in,ganeshraj_n@apollohospitals.com,anshul_s@apollohospitals.com";
				String ccAddress = "anurag_v@apollohospitals.com,natarajan_rk@apollohospitals.com,prashant_sharma@apollohospitals.com,mohanakrishna_s@apollohospitals.com,ravinder_kp@apollohospitals.com";

				String[] toAddressList = toAddress.replaceAll(" ", "").split(",");
				torecipientAddress = new InternetAddress[toAddressList.length];
				int counter = 0;
				for (String recipient : toAddressList) {
					torecipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}

				String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
				ccrecipientAddress = new InternetAddress[ccAddressList.length];
				int cccounter = 0;
				for (String recipient : ccAddressList) {
					ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
					cccounter++;
				}

				message.setFrom(new InternetAddress(from, "AskApollo"));
				message.setRecipients(Message.RecipientType.TO, torecipientAddress);
				message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
				message.setSubject(subject);
				message.setContent(content, "text/html");

				Transport.send(message);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		} else if (content.contains("Online")) {
			try {
				String toAddress = "sandeep.a@hng.co.in,ganeshraj_n@apollohospitals.com,abhinav_k@apollohospitals.com,anshul_s@apollohospitals.com";
				String ccAddress = "anurag_v@apollohospitals.com,natarajan_rk@apollohospitals.com,prashant_sharma@apollohospitals.com,mohanakrishna_s@apollohospitals.com,ravinder_kp@apollohospitals.com";

				String[] toAddressList = toAddress.replaceAll(" ", "").split(",");
				torecipientAddress = new InternetAddress[toAddressList.length];
				int counter = 0;
				for (String recipient : toAddressList) {
					torecipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}

				String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
				ccrecipientAddress = new InternetAddress[ccAddressList.length];
				int cccounter = 0;
				for (String recipient : ccAddressList) {
					ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
					cccounter++;
				}

				message.setFrom(new InternetAddress(from, "AskApollo"));
				message.setRecipients(Message.RecipientType.TO, torecipientAddress);
				message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
				message.setSubject(subject);
				message.setContent(content, "text/html");

				Transport.send(message);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		else if (content.contains("Order")) {
			try {
				String toAddress = "pitchaiah@apollopharmacy.org,balaji_c@apollohospitals.com,sandeep.a@hng.co.in";
				String ccAddress = "anurag_v@apollohospitals.com,natarajan_rk@apollohospitals.com,prashant_sharma@apollohospitals.com,mohanakrishna_s@apollohospitals.com,ravinder_kp@apollohospitals.com";

				String[] toAddressList = toAddress.replaceAll(" ", "").split(",");
				torecipientAddress = new InternetAddress[toAddressList.length];
				int counter = 0;
				for (String recipient : toAddressList) {
					torecipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}

				String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
				ccrecipientAddress = new InternetAddress[ccAddressList.length];
				int cccounter = 0;
				for (String recipient : ccAddressList) {
					ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
					cccounter++;
				}

				message.setFrom(new InternetAddress(from, "AskApollo"));
				message.setRecipients(Message.RecipientType.TO, torecipientAddress);
				message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
				message.setSubject(subject);
				message.setContent(content, "text/html");

				Transport.send(message);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (content.contains("PHR")) {
			try {
				String toAddress = "jayaram.narayanappa@searchlighthealth.com,vijay@searchlighthealth.com,sandeep.a@hng.co.in";
				String ccAddress = "anurag@searchlighthealth.com,anurag_v@apollohospitals.com,natarajan_rk@apollohospitals.com,prashant_sharma@apollohospitals.com,mohanakrishna_s@apollohospitals.com,ravinder_kp@apollohospitals.com";

				String[] toAddressList = toAddress.replaceAll(" ", "").split(",");
				torecipientAddress = new InternetAddress[toAddressList.length];
				int counter = 0;
				for (String recipient : toAddressList) {
					torecipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}

				String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
				ccrecipientAddress = new InternetAddress[ccAddressList.length];
				int cccounter = 0;
				for (String recipient : ccAddressList) {
					ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
					cccounter++;
				}

				message.setFrom(new InternetAddress(from, "AskApollo"));
				message.setRecipients(Message.RecipientType.TO, torecipientAddress);
				message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
				message.setSubject(subject);
				message.setContent(content, "text/html");

				Transport.send(message);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (content.contains("JiYo")) {
			try {

				String toAddress = "mandanna@jiyo.com";
				String ccAddress = "anurag_v@apollohospitals.com,natarajan_rk@apollohospitals.com,prashant_sharma@apollohospitals.com,mohanakrishna_s@apollohospitals.com,ravinder_kp@apollohospitals.com";

				String[] toAddressList = toAddress.replaceAll(" ", "").split(",");
				torecipientAddress = new InternetAddress[toAddressList.length];
				int counter = 0;
				for (String recipient : toAddressList) {
					torecipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}

				String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
				ccrecipientAddress = new InternetAddress[ccAddressList.length];
				int cccounter = 0;
				for (String recipient : ccAddressList) {
					ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
					cccounter++;
				}

				message.setFrom(new InternetAddress(from, "AskApollo"));
				message.setRecipients(Message.RecipientType.TO, torecipientAddress);
				message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
				message.setSubject(subject);
				message.setContent(content, "text/html");

				Transport.send(message);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (content.contains("Membership")) {
			try {

				String toAddress = "mloyalsupport@mobiquest.com,mohit.patney@mobiquest.com,anmol@mobiquest.com,nathan_r@apollohospitals.com";
				String ccAddress = "anurag_v@apollohospitals.com,natarajan_rk@apollohospitals.com,prashant_sharma@apollohospitals.com,mohanakrishna_s@apollohospitals.com,ravinder_kp@apollohospitals.com";
				String[] toAddressList = toAddress.replaceAll(" ", "").split(",");

				torecipientAddress = new InternetAddress[toAddressList.length];
				int counter = 0;
				for (String recipient : toAddressList) {
					torecipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}

				String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
				ccrecipientAddress = new InternetAddress[ccAddressList.length];
				int cccounter = 0;
				for (String recipient : ccAddressList) {
					ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
					cccounter++;
				}

				message.setFrom(new InternetAddress(from, "AskApollo"));
				message.setRecipients(Message.RecipientType.TO, torecipientAddress);
				message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
				message.setSubject(subject);
				message.setContent(content, "text/html");

				Transport.send(message);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (content.contains("OneApollo")) {
			try {

				String toAddress = "narsimhaswamy_j@apollohospitals.com,balaji_c@apollohospitals.com,mloyalsupport@mobiquest.com,mohit.patney@mobiquest.com,anmol@mobiquest.com,nathan_r@apollohospitals.com";
				String ccAddress = "anurag_v@apollohospitals.com,natarajan_rk@apollohospitals.com,prashant_sharma@apollohospitals.com,mohanakrishna_s@apollohospitals.com,ravinder_kp@apollohospitals.com";
				String[] toAddressList = toAddress.replaceAll(" ", "").split(",");

				torecipientAddress = new InternetAddress[toAddressList.length];
				int counter = 0;
				for (String recipient : toAddressList) {
					torecipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}

				String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
				ccrecipientAddress = new InternetAddress[ccAddressList.length];
				int cccounter = 0;
				for (String recipient : ccAddressList) {
					ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
					cccounter++;
				}

				message.setFrom(new InternetAddress(from, "AskApollo"));
				message.setRecipients(Message.RecipientType.TO, torecipientAddress);
				message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
				message.setSubject(subject);
				message.setContent(content, "text/html");

				Transport.send(message);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (content.contains("Lets")) {
			try {

				String toAddress = "ganeshraj_n@apollohospitals.com,anshul_s@apollohospitals.com";
				String ccAddress = "anurag_v@apollohospitals.com,natarajan_rk@apollohospitals.com,prashant_sharma@apollohospitals.com,mohanakrishna_s@apollohospitals.com,ravinder_kp@apollohospitals.com";
				String[] toAddressList = toAddress.replaceAll(" ", "").split(",");

				torecipientAddress = new InternetAddress[toAddressList.length];
				int counter = 0;
				for (String recipient : toAddressList) {
					torecipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}

				String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
				ccrecipientAddress = new InternetAddress[ccAddressList.length];
				int cccounter = 0;
				for (String recipient : ccAddressList) {
					ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
					cccounter++;
				}

				message.setFrom(new InternetAddress(from, "AskApollo"));
				message.setRecipients(Message.RecipientType.TO, torecipientAddress);
				message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
				message.setSubject(subject);
				message.setContent(content, "text/html");

				Transport.send(message);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (content.contains("Membership")) {
			try {

				String toAddress = "sibaprasad_s@apollohospitals.com";
				String ccAddress = "anurag_v@apollohospitals.com,natarajan_rk@apollohospitals.com,prashant_sharma@apollohospitals.com,mohanakrishna_s@apollohospitals.com,ravinder_kp@apollohospitals.com";
				String[] toAddressList = toAddress.replaceAll(" ", "").split(",");

				torecipientAddress = new InternetAddress[toAddressList.length];
				int counter = 0;
				for (String recipient : toAddressList) {
					torecipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}

				String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
				ccrecipientAddress = new InternetAddress[ccAddressList.length];
				int cccounter = 0;
				for (String recipient : ccAddressList) {
					ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
					cccounter++;
				}

				message.setFrom(new InternetAddress(from, "AskApollo"));
				message.setRecipients(Message.RecipientType.TO, torecipientAddress);
				message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
				message.setSubject(subject);
				message.setContent(content, "text/html");

				Transport.send(message);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (content.contains("Locate")) {
			try {

				String toAddress = "anurag_v@apollohospitals.com";
				String ccAddress = "natarajan_rk@apollohospitals.com,prashant_sharma@apollohospitals.com,mohanakrishna_s@apollohospitals.com,ravinder_kp@apollohospitals.com";
				String[] toAddressList = toAddress.replaceAll(" ", "").split(",");

				torecipientAddress = new InternetAddress[toAddressList.length];
				int counter = 0;
				for (String recipient : toAddressList) {
					torecipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}

				String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
				ccrecipientAddress = new InternetAddress[ccAddressList.length];
				int cccounter = 0;
				for (String recipient : ccAddressList) {
					ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
					cccounter++;
				}

				message.setFrom(new InternetAddress(from, "AskApollo"));
				message.setRecipients(Message.RecipientType.TO, torecipientAddress);
				message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
				message.setSubject(subject);
				message.setContent(content, "text/html");

				Transport.send(message);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (content.contains("Apollo Hospitals")) {
			try {

				String toAddress = "ganeshraj_n@apollohospitals.com,natish_c@apollohospitals.com,nutan_m@apollohospitals.com,nathan_r@apollohospitals.com,anshul_s@apollohospitals.com";
				String ccAddress = "anurag_v@apollohospitals.com,natarajan_rk@apollohospitals.com,prashant_sharma@apollohospitals.com,mohanakrishna_s@apollohospitals.com,ravinder_kp@apollohospitals.com";
				String[] toAddressList = toAddress.replaceAll(" ", "").split(",");

				torecipientAddress = new InternetAddress[toAddressList.length];
				int counter = 0;
				for (String recipient : toAddressList) {
					torecipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}

				String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
				ccrecipientAddress = new InternetAddress[ccAddressList.length];
				int cccounter = 0;
				for (String recipient : ccAddressList) {
					ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
					cccounter++;
				}

				message.setFrom(new InternetAddress(from, "AskApollo"));
				message.setRecipients(Message.RecipientType.TO, torecipientAddress);
				message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
				message.setSubject(subject);
				message.setContent(content, "text/html");

				Transport.send(message);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (content.contains("Ask Apollo IP Tracker")) {
			try {
				String toAddress = "sandeep.a@hng.co.in,srinivas_suryadevara@apollohospitals.com";
				String ccAddress = "anurag_v@apollohospitals.com,natarajan_rk@apollohospitals.com,prashant_sharma@apollohospitals.com,mohanakrishna_s@apollohospitals.com,ravinder_kp@apollohospitals.com";

				String[] toAddressList = toAddress.replaceAll(" ", "").split(",");
				torecipientAddress = new InternetAddress[toAddressList.length];
				int counter = 0;
				for (String recipient : toAddressList) {
					torecipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}

				String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
				ccrecipientAddress = new InternetAddress[ccAddressList.length];
				int cccounter = 0;
				for (String recipient : ccAddressList) {
					ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
					cccounter++;
				}

				message.setFrom(new InternetAddress(from, "AskApollo"));
				message.setRecipients(Message.RecipientType.TO, torecipientAddress);
				message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
				message.setSubject(subject);
				message.setContent(content, "text/html");

				Transport.send(message);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			try {
				String toAddress = "ravinder.koripelly@outlook.com";
				String ccAddress = "ravinder_kp@apollohospitals.com";

				String[] toAddressList = toAddress.replaceAll(" ", "").split(",");
				torecipientAddress = new InternetAddress[toAddressList.length];
				int counter = 0;
				for (String recipient : toAddressList) {
					torecipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}
				String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
				ccrecipientAddress = new InternetAddress[ccAddressList.length];
				int cccounter = 0;
				for (String recipient : ccAddressList) {
					ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
					cccounter++;
				}

				message.setFrom(new InternetAddress(from, "AskApollo"));
				message.setRecipients(Message.RecipientType.TO, torecipientAddress);
				message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
				message.setSubject(subject);
				message.setContent(content, "text/plain");

				Transport.send(message);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static Session getOfficeSMTPSession1() {

		if (office365_SMTP_session1 == null) {

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.office365.com");
			props.put("mail.smtp.localhost", "127.0.0.1");
			props.put("mail.smtp.port", "25");

			office365_SMTP_session1 = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(office365_SMTP_USER_NAME1, office365_SMTP_PASSWORD1);
				}
			});

		}

		return office365_SMTP_session1;
	}

}
