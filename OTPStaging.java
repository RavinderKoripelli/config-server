package com.ApolloCrownJob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;

public class OTPStaging {
	
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/LocateApollo?autoReconnect=true";
	private static final String USER_NAME = "root";
	private static final String PASSWORD = "askApolloApp@123";
	
	private static final String office365_SMTP_USER_NAME1 = "askapollo@apollohospitals.com";
	private static final String office365_SMTP_PASSWORD1 = "apollo@1234";
	
	private static Session office365Session1;
	
	
	public static void main(String args[]) {
		
		for (;;) {
		
			List<Object[]> otpList = retriveOTPList();
			
			for (Object[] object : otpList) {
				
				//String indexNo = postFixSMTP(object);
				
				String indexNo = office365SMTP1(object);
				System.out.println("indexNo" + indexNo);
				updateBatchMailRecord(indexNo);
				try {
					//Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}

	private static void updateBatchMailRecord(String indexNo) {
		
		Connection con  = null;
		CallableStatement stmt = null;
		
		try {
			
			if (con == null) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
				String updateRecord = "{call usp_Update_Email_Delivery_Status_Feedback(?)}";
				stmt = con.prepareCall(updateRecord);
				stmt.setString(1, indexNo);
								
				stmt.executeUpdate();
			}
			
		}catch(Exception e) {
				try {
					if(con != null) {
						con.close();
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} finally {
				try {
					if(con != null) {
						con.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
	}

	private static List<Object[]> retriveOTPList() {
		
		Connection con  = null;
		CallableStatement stmt = null;
		ArrayList<Object[]> objectList = new ArrayList<Object[]>();
		
		try {
			
			if (con == null) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
				
				String getDBUSERByUserIdSql = "{call usp_Email_List_For_Inactive_User_Feedback_OTP_Staging()}";
				
				stmt = con.prepareCall(getDBUSERByUserIdSql);
				ResultSet rs = stmt.executeQuery();
				
				Object[] objectArray;

				while(rs.next()){
					String ToAddress = (rs.getString("ToAddress") == null) ? "" : rs.getString("ToAddress");
	                String MailSubject = (rs.getString("MailSubject") == null) ? "" : rs.getString("MailSubject");
	                String MailBody = (rs.getString("MailBody") == null) ? "" : rs.getString("MailBody");
	                /*String CcAddress = (rs.getString("CcAddress") == null) ? "" : rs.getString("CcAddress");
	                String BccAddress = (rs.getString("BccAddress") == null) ? "" : rs.getString("BccAddress");*/
	                                    
	                if((ToAddress!="") && (ToAddress.contains(";"))) {
	    				ToAddress = ToAddress.replaceAll(";", ",");
	                }
	                
	                objectArray = new Object[]{rs.getString("IndexNo"), ToAddress, MailSubject, MailBody};
					objectList.add(objectArray);
				}
			}
			
		} catch(Exception e) {
			try {	
				if(con != null) {
					con.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				if(con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return objectList;
	}
	
	private static String postFixSMTP(Object[] objectArray) {
		
	    String from = "info@askapollo.com";
	    String host = "localhost";
	     
		try {
					 String toAddress = (String) objectArray[1];
					 InternetAddress[] torecipientAddress;
					 if (!toAddress.equalsIgnoreCase("")) {
					 
				     String[] toAddressList = toAddress.replaceAll(" ", "").split(",");
				     torecipientAddress = new InternetAddress[toAddressList.length];
				    	 int counter = 0;
					     for (String recipient : toAddressList) {
					         torecipientAddress[counter] = new InternetAddress(recipient.trim());
					         counter++;
					     }
					} else {
						torecipientAddress = new InternetAddress[0];
					}	     
				     
					     Properties props = new Properties();
					     props.put("mail.smtp.host", host);
					     props.put("mail.debug", "true");
				     
				     	 Session session = Session.getInstance(props);
				     	
				         Message msg = new MimeMessage(session);

				         msg.setFrom(new InternetAddress(from, "Ask Apollo"));
				         msg.setRecipients(Message.RecipientType.TO, torecipientAddress);
				         msg.setSubject((String) objectArray[2]);
			
		        		 msg.setSentDate(new Date());
				         msg.setContent((String) objectArray[3], "text/html");
				         
				         Transport.send(msg);
				         				         
				         Thread.sleep(1000);
				    
		     }
		     catch (Exception mex) {
		         mex.printStackTrace();
		     }
		return (String) objectArray[0];
		
	}	
	
	private static String office365SMTP1(Object[] objectArray) {

	    //String from = "info@askapollo.com";
	    String from = "askapollo@apollohospitals.com";
	    String indexNoSMTP1 = null;
	    Session office365SessionObject;
	    
		try {
				
			String toAddress = (String) objectArray[1];
			 InternetAddress[] torecipientAddress;
			 if (!toAddress.equalsIgnoreCase("")) {
			 
		     String[] toAddressList = toAddress.replaceAll("[^\\x0A\\x0D\\x20-\\x7E]","").split(",");
		     torecipientAddress = new InternetAddress[toAddressList.length];
		    	 int counter = 0;
			     for (String recipient : toAddressList) {
			         torecipientAddress[counter] = new InternetAddress(recipient.trim());
			         counter++;
			     }
			} else {
				torecipientAddress = new InternetAddress[0];
			}
		     
		     
			   office365SessionObject = getOffice365SMTPSession1();
			   Message message = new MimeMessage(office365SessionObject);
		     
			   
			   message.setFrom(new InternetAddress(from, "Ask Apollo"));
			   message.setRecipients(Message.RecipientType.TO, torecipientAddress);
			   message.setSubject((String) objectArray[1]);

			   message.setSentDate(new Date());
			   message.setContent((String) objectArray[3], "text/html");

			   try{
				   
				   SMTPTransport transport = (SMTPTransport) office365SessionObject.getTransport("smtp");
				        
				   transport.connect("smtp.office365.com",office365_SMTP_USER_NAME1, office365_SMTP_PASSWORD1);
				   SMTPTransport.send(message);
			       System.out.println("SMTP_1 Response Code: "+transport.getLastReturnCode());
				   
				   //Transport.send(message);
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
	
	
}
