package com.ApolloCrownJob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;

public class FeedBackMail {

	// private static final String PRODUCTION_URL = "jdbc:mysql://13.71.124.208:3306/LocateApollo";
	private static final String PRODUCTION_URL = "jdbc:mysql://13.71.122.152:3306/LocateApollo";
	private static final String PRODUCTION_USER_NAME = "root";
	private static final String PRODUCTION_PASSWORD = "askApolloApp@123";

	private static final String office365_SMTP_USER_NAME1 = "askapollo@apollohospitals.com";
	private static final String office365_SMTP_PASSWORD1 = "apollo@1234";

	private static Session office365Session1;

	public static ArrayList<String> batchIndexList = new ArrayList<String>();

	public static void main(String[] args) {

		try {

			ArrayList<Object[]> feedBackListRecords = new ArrayList<Object[]>();

			Calendar date = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

			while (true) {
				feedBackListRecords.clear();
				feedBackListRecords = listFeedBack();

				System.out.println("size: " + feedBackListRecords.size());

				if (feedBackListRecords.size() != 0) {

					for (Object[] feedBackList : feedBackListRecords) {

						String indexNoResultSMTP = office365SMTP1(feedBackList);
						if (indexNoResultSMTP != null) {
							batchIndexList.add(indexNoResultSMTP);
							Thread.sleep(2000);
						}

						if (batchIndexList.size() == 5) {
							updateBatchMailRecord(batchIndexList);
						}
					}
					if (batchIndexList.size() > 0 && batchIndexList.size() < 5) {
						updateBatchMailRecord(batchIndexList);
					}

				}

				date.setTime(new Date());
				date.add(Calendar.HOUR, 5);
				date.add(Calendar.MINUTE, 30);
				if ((dateFormat.parse(dateFormat.format(date.getTime())).after(dateFormat.parse("22:30:00")))
						|| (dateFormat.parse(dateFormat.format(date.getTime())).before(dateFormat.parse("04:00:00")))) {
					Thread.sleep(30 * 60 * 1000);
				} else {
					Thread.sleep(5 * 60 * 1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Session getOffice365SMTPSession1() {

		if (office365Session1 == null) {

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.office365.com");
			props.put("mail.smtp.port", "587");
 			props.put("mail.smtp.connectiontimeout", "10000");
               	        props.put("mail.smtp.timeout", "10000");

			props.put("mail.smtp.localhost", "127.0.0.1");

			office365Session1 = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(office365_SMTP_USER_NAME1, office365_SMTP_PASSWORD1);
				}
			});

		}

		return office365Session1;
	}

	public static ArrayList<Object[]> listFeedBack() {

		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<Object[]> objectList = new ArrayList<Object[]>();

		try {

			if (con == null) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(PRODUCTION_URL, PRODUCTION_USER_NAME, PRODUCTION_PASSWORD);

				String getFeedBackListQuery = "{call usp_Email_List_For_Inactive_User_Feedback()}";

				stmt = con.prepareCall(getFeedBackListQuery);
				ResultSet rs = stmt.executeQuery();

				Object[] objectArray;

				while (rs.next()) {
					objectArray = new Object[] { rs.getString("IndexNo"), rs.getString("ToAddress"),
							rs.getString("MailBody"), rs.getString("MailSubject") };

					objectList.add(objectArray);
				}

			}
		} catch (Exception e) {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return objectList;
	}

	private static String office365SMTP1(Object[] objectArray) {
                
                //String from = "info@askapollo.com";
		String from = "askapollo@apollohospitals.com";
		String indexNoSMTP1 = null;

		try {

			Session office365SessionObject = getOffice365SMTPSession1();
			Message message = new MimeMessage(office365SessionObject);

			String toAddress = (String) objectArray[1];
			InternetAddress[] torecipientAddress = null;
			if (!toAddress.equalsIgnoreCase("")) {
				toAddress = toAddress.replaceAll(" ", "");
				String[] toAddressList = toAddress.replaceAll("[^\\x0A\\x0D\\x20-\\x7E]", "").split(",");
				torecipientAddress = new InternetAddress[toAddressList.length];
				int bcccounter = 0;
				for (String recipient : toAddressList) {
					torecipientAddress[bcccounter] = new InternetAddress(recipient.trim());
					bcccounter++;
				}
			}

			message.setFrom(new InternetAddress(from, "Ask Apollo"));
			message.setRecipients(Message.RecipientType.TO, torecipientAddress);
			message.setSubject((String) objectArray[3]);

			message.setContent((String) objectArray[2], "text/html");

                        message.setReplyTo(new javax.mail.Address[]{
                                    new javax.mail.internet.InternetAddress("info@askapollo.com")
                        });
			
			try{  
			
				SMTPTransport transport = (SMTPTransport) office365SessionObject.getTransport("smtp");

				transport.connect("smtp.office365.com", office365_SMTP_USER_NAME1, office365_SMTP_PASSWORD1);
				SMTPTransport.send(message);

				System.out.println("Response Code " + transport.getLastReturnCode());

				// Success response from office 365
				if (transport.getLastReturnCode() == 235) {
					indexNoSMTP1 = (String) objectArray[0];
				}
			

 			}catch(Exception e){
				   e.printStackTrace();
			 }

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return indexNoSMTP1;

	}

	private static void updateBatchMailRecord(ArrayList<String> indexNoList) {

		Connection con = null;
		CallableStatement stmt = null;

		try {

			if (con == null) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(PRODUCTION_URL, PRODUCTION_USER_NAME, PRODUCTION_PASSWORD);
				con.setAutoCommit(false);
				String updateRecord = "{call usp_Update_Email_Delivery_Status_Feedback(?)}";
				stmt = con.prepareCall(updateRecord);

				for (int i = 0; i < indexNoList.size(); i++) {
					System.out.println("Indexno: " + indexNoList.get(i));
					stmt.setString(1, indexNoList.get(i));
					stmt.addBatch();
				}

				stmt.executeBatch();
				con.setAutoCommit(true);
			}

		} catch (Exception e) {
			try {
				if (con != null) {
					con.rollback();
					con.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				batchIndexList.clear();
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

}
