package com.ApolloCrownJob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Calendar;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import com.sun.mail.smtp.SMTPTransport;

public class EDOCMail {

//private static final String URL = "jdbc:mysql://13.71.124.208:3306/LocateApollo";
private static final String URL = "jdbc:mysql://13.71.122.152:3306/LocateApollo";
private static final String USER_NAME = "root";
private static final String PASSWORD = "askApolloApp@123";

private static final String office365_SMTP_USER_NAME1 = "askapollo@apollohospitals.com";
private static final String office365_SMTP_PASSWORD1 = "apollo@1234";

private static final String office365_SMTP_USER_NAME2 = "askapollo1@apollohospitals.com";
private static final String office365_SMTP_PASSWORD2 = "Ap@ll@*1";

private static Session office365Session1;
private static Session office365Session2;

public static ArrayList<String> batchIndexList = new ArrayList<String>();

public static void main(String[] args) {
	
	try {
		
			ArrayList<Object[]> listRecords = new ArrayList<Object[]>();
			int count=0;
			Calendar date = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

			while (true) {
				listRecords = EDOCMail.listOfRecords();
				System.out.println("List Retrieve Size: " + listRecords.size());
				
				if (listRecords.size() != 0) {
					
					for (Object[] objectArray: listRecords) {
								
						if(count==0){
							System.out.println("Smtp_1 : Index Number: "+objectArray[5]);
							String indexNoResultSMTP1 = EDOCMail.office365SMTP1(objectArray);
							if(indexNoResultSMTP1 != null){
							   batchIndexList.add(indexNoResultSMTP1);
							   //Thread.sleep(1000);
							   count=1;
							}else{
								count=1;
							}
						} else{
							System.out.println("Smtp_2 : Index Number: "+objectArray[5]);
							String indexNoResultSMTP2 = EDOCMail.office365SMTP2(objectArray);
							if(indexNoResultSMTP2 != null){
								 batchIndexList.add(indexNoResultSMTP2);
								 //Thread.sleep(1000);
								 count=0;
								}else{
									count=0;
								}
							//String indexNoResultSMTP2 = EDOCMail.office365SMTP2(objectArray);
						}
						
						if (batchIndexList.size() == 5) {
							EDOCMail.updateBatchMailRecord(batchIndexList);
						}
					}
					
					if (batchIndexList.size() > 0 && batchIndexList.size()<5) {
						EDOCMail.updateBatchMailRecord(batchIndexList);
					}
					
				}
				
				date.setTime(new Date());
				date.add(Calendar.HOUR, 5);
				date.add(Calendar.MINUTE, 30);
					
				/*if ((dateFormat.parse(dateFormat.format(date.getTime())).after(dateFormat.parse("23:00:00")))
							|| (dateFormat.parse(dateFormat.format(date.getTime())).before(dateFormat.parse("04:00:00")))) {
					Thread.sleep(30 * 60 * 1000);
				} else {
					Thread.sleep(1 * 60 * 1000);
				} */			
			}
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	
}

private static void updateBatchMailRecord(ArrayList<String> indexNoList) {
	
	Connection con  = null;
	CallableStatement stmt = null;
	
	try {
		
		if (con == null) {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
			con.setAutoCommit(false);
			String updateRecord = "{call usp_Update_Email_Delivery_Status(?)}";
			stmt = con.prepareCall(updateRecord);
			
			for (int i = 0; i < indexNoList.size(); i++) {
				System.out.println("Indexno: " + indexNoList.get(i));
				stmt.setString(1, indexNoList.get(i));
				stmt.addBatch();
			}
			
			stmt.executeBatch();
			con.setAutoCommit(true);
		}
		
	}catch(Exception e) {
			try {
				if(con != null) {
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
				System.out.println("Update: Empty: " + batchIndexList.size());
				if(con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
}

public static ArrayList<Object[]> listOfRecords() {
	
	Connection con  = null;
	CallableStatement stmt = null;
	ArrayList<Object[]> objectList = new ArrayList<Object[]>();
	
	try {
		
		if (con == null) {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
			
			String getDBUSERByUserIdSql = "{call usp_Email_List_For_Inactive_User()}";
			
			stmt = con.prepareCall(getDBUSERByUserIdSql);
			ResultSet rs = stmt.executeQuery();
			
			Object[] objectArray;

			while(rs.next()){
				String ToAddress = (rs.getString("ToAddress") == null) ? "" : rs.getString("ToAddress");
                String CcAddress = (rs.getString("CcAddress") == null) ? "" : rs.getString("CcAddress");
                String BccAddress = (rs.getString("BccAddress") == null) ? "" : rs.getString("BccAddress");
                String MailSubject = (rs.getString("MailSubject") == null) ? "" : rs.getString("MailSubject");
                String MailBody = (rs.getString("MailBody") == null) ? "" : rs.getString("MailBody");
                                    
                if((ToAddress!="") && (ToAddress.contains(";"))) {
    				ToAddress = ToAddress.replaceAll(";", ",");
                }
    
                if ((CcAddress!="") && (CcAddress.contains(";"))) {
    	        	CcAddress = CcAddress.replaceAll(";", ",");
                }
                
                if ((BccAddress!="") && (BccAddress.contains(";"))) {
                	BccAddress = BccAddress.replaceAll(";", ",");
                }
                
                objectArray = new Object[]{ ToAddress, CcAddress, BccAddress, MailSubject, MailBody , rs.getString("IndexNo")};
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



private static String office365SMTP1(Object[] objectArray) {

    //String from = "info@askapollo.com";
    String from = "askapollo@apollohospitals.com";
    String indexNoSMTP1 = null;
    Session office365SessionObject;
    
	try {
			
		String toAddress = (String) objectArray[0];
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
	     
	     
	     
	     String ccAddress = (String) objectArray[1];
	     InternetAddress[] ccrecipientAddress;
	     if (!ccAddress.equalsIgnoreCase("")) {
		     String[] ccAddressList = ccAddress.replaceAll("\\s","").split(",");
		     ccrecipientAddress = new InternetAddress[ccAddressList.length];
		     int cccounter = 0;
		     for (String recipient : ccAddressList) {
		    	 ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
		    	 cccounter++;
		     }	
		} else {
			ccrecipientAddress = new InternetAddress[0];
		}
	     
	     String bccAddress = (String) objectArray[2];
	     InternetAddress[] bccrecipientAddress;
	     if (!bccAddress.equalsIgnoreCase("")) {
		     String[] bccAddressList = bccAddress.replaceAll("\\s","").split(",");
		     bccrecipientAddress = new InternetAddress[bccAddressList.length];
		     int bcccounter = 0;
		     for (String recipient : bccAddressList) {
		    	 bccrecipientAddress[bcccounter] = new InternetAddress(recipient.trim());
		    	 bcccounter++;
		     }
	     } else {
	    	 bccrecipientAddress = new InternetAddress[0];
		}
		   //System.out.println("To: "+toAddress +" cc: "+ccAddress+" BCC "+bccAddress+" Subject: "+(String)objectArray[3]+" Content: "+(String)objectArray[4]);
	       System.out.println("Inside First SMTP To: "+toAddress +" cc: "+ccAddress+" ,BCC: "+bccAddress+" ,Subject: "+(String)objectArray[3]);
		   office365SessionObject = EDOCMail.getOffice365SMTPSession1();
		   Message message = new MimeMessage(office365SessionObject);
	     
		   
		   message.setFrom(new InternetAddress(from, "Ask Apollo"));
		   message.setRecipients(Message.RecipientType.TO, torecipientAddress);
		   message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
		   message.setRecipients(Message.RecipientType.BCC, bccrecipientAddress);
		   message.setSubject((String) objectArray[3]);

		   message.setSentDate(new Date());
		   message.setContent((String) objectArray[4], "text/html");
 
                   message.setReplyTo(new javax.mail.Address[]{
				    new javax.mail.internet.InternetAddress("info@askapollo.com")
			});

		   try{
			   
			   SMTPTransport transport =
			            (SMTPTransport) office365SessionObject.getTransport("smtp");
			        
			   transport.connect("smtp.office365.com",office365_SMTP_USER_NAME1, office365_SMTP_PASSWORD1);
			   SMTPTransport.send(message);
			   //System.out.println("Response: " + transport.getLastServerResponse());
		       System.out.println("SMTP_1 Response Code: "+transport.getLastReturnCode());
			   
			   //Transport.send(message);
		       if (transport.getLastReturnCode() == 235) {
		    	   indexNoSMTP1 = (String) objectArray[5];
		       }
			  
		   }catch(Exception e){
			  
			   e.printStackTrace();
			  
		   }
	      
	       //EDOCMail.updateMailRecord((String) objectArray[5]);
		  
		} catch (Exception e) {
	            throw new RuntimeException(e);
    }
	return indexNoSMTP1;	
	
}

private static String office365SMTP2(Object[] objectArray) {

    //String from = "info@askapollo.com";
    String from = "askapollo1@apollohospitals.com";
    String indexNoSMTP2 = null;
    
	try {
			
		String toAddress = (String) objectArray[0];
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
	     
	     String ccAddress = (String) objectArray[1];
	     InternetAddress[] ccrecipientAddress;
	     if (!ccAddress.equalsIgnoreCase("")) {
		     String[] ccAddressList = ccAddress.replaceAll("\\s","").split(",");
		     ccrecipientAddress = new InternetAddress[ccAddressList.length];
		     int cccounter = 0;
		     for (String recipient : ccAddressList) {
		    	 ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
		    	 cccounter++;
		     }	
		} else {
			ccrecipientAddress = new InternetAddress[0];
		}
	     
	     String bccAddress = (String) objectArray[2];
	     InternetAddress[] bccrecipientAddress;
	     if (!bccAddress.equalsIgnoreCase("")) {
		     String[] bccAddressList = bccAddress.replaceAll("\\s","").split(",");
		     bccrecipientAddress = new InternetAddress[bccAddressList.length];
		     int bcccounter = 0;
		     for (String recipient : bccAddressList) {
		    	 bccrecipientAddress[bcccounter] = new InternetAddress(recipient.trim());
		    	 bcccounter++;
		     }
	     } else {
	    	 bccrecipientAddress = new InternetAddress[0];
		}
		   //System.out.println("To: "+toAddress +" cc: "+ccAddress+" BCC "+bccAddress+" Subject: "+(String)objectArray[3]+" Content: "+(String)objectArray[4]);
	       System.out.println("Inside Second SMTP To: "+toAddress +" ,cc: "+ccAddress+" ,BCC: "+bccAddress+" ,Subject: "+(String)objectArray[3]);
		   Session office365SessionObject = EDOCMail.getOffice365SMTPSession2();
		   Message message = new MimeMessage(office365SessionObject);
	     
		   
		   message.setFrom(new InternetAddress(from, "Ask Apollo"));
		   message.setRecipients(Message.RecipientType.TO, torecipientAddress);
		   message.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
		   message.setRecipients(Message.RecipientType.BCC, bccrecipientAddress);
		   message.setSubject((String) objectArray[3]);

		   message.setSentDate(new Date());
		   message.setContent((String) objectArray[4], "text/html");
       
                   message.setReplyTo(new javax.mail.Address[]{
                                    new javax.mail.internet.InternetAddress("info@askapollo.com")
                        });

	       //Transport.send(message);
	       
	       //indexNoSMTP2 = (String) objectArray[5];
	       //EDOCMail.updateMailRecord((String) objectArray[5]);
		   //Thread.sleep(2000);
		   
		   try{  
			   SMTPTransport transport =
			            (SMTPTransport) office365SessionObject.getTransport("smtp");
			        
			   transport.connect("smtp.office365.com",office365_SMTP_USER_NAME2, office365_SMTP_PASSWORD2);
			   SMTPTransport.send(message);
			   //System.out.println("Response: " + transport.getLastServerResponse());
		       System.out.println("SMTP_2 Response Code: "+transport.getLastReturnCode());
			   //Transport.send(message);
		       if (transport.getLastReturnCode() == 235) {
		    	   indexNoSMTP2 = (String) objectArray[5];
		       }
		   }catch(Exception e){
			   e.printStackTrace();
		   }
	   
		} catch (Exception e) {
			throw new RuntimeException(e);
    }
	return indexNoSMTP2;	
	
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

public static Session getOffice365SMTPSession2() {

	if (office365Session2 == null) {

		 Properties props = new Properties();
		 props.put("mail.smtp.auth", "true");
		 props.put("mail.smtp.starttls.enable", "true");
		 props.put("mail.smtp.host", "smtp.office365.com");
		 props.put("mail.smtp.port", "587");
		 props.put("mail.smtp.connectiontimeout", "10000");
             	 props.put("mail.smtp.timeout", "10000");
		 props.put("mail.smtp.localhost", "127.0.0.1");
		  
		 office365Session2 = Session.getInstance(props, new javax.mail.Authenticator() {
		   protected PasswordAuthentication getPasswordAuthentication() {
			   return new PasswordAuthentication(office365_SMTP_USER_NAME2, office365_SMTP_PASSWORD2);
		   }
		 });

	}

	return office365Session2;
}

private static void postFixSMTP(Object[] objectArray) {
	
    String from = "info@askapollo.com";
    String host = "localhost";
     
	try {
			
				//System.out.println(objectArray[0] + " : " + objectArray[1]  + " : " + objectArray[2]  + " : " + objectArray[3]  + " : " + objectArray[4]  + " : " + objectArray[5]);
				
				 //String toAddress = "alluraviteja30@gmail.com ,alluraviteja3004@gmail.com ";
				 String toAddress = (String) objectArray[0];
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
			     
			     String ccAddress = (String) objectArray[1];
			     InternetAddress[] ccrecipientAddress;
			     if (!ccAddress.equalsIgnoreCase("")) {
				     String[] ccAddressList = ccAddress.replaceAll(" ", "").split(",");
				     ccrecipientAddress = new InternetAddress[ccAddressList.length];
				     int cccounter = 0;
				     for (String recipient : ccAddressList) {
				    	 ccrecipientAddress[cccounter] = new InternetAddress(recipient.trim());
				    	 cccounter++;
				     }	
				} else {
					ccrecipientAddress = new InternetAddress[0];
				}
			     
			     String bccAddress = (String) objectArray[2];
			     InternetAddress[] bccrecipientAddress;
			     if (!bccAddress.equalsIgnoreCase("")) {
				     String[] bccAddressList = bccAddress.replaceAll(" ", "").split(",");
				     bccrecipientAddress = new InternetAddress[bccAddressList.length];
				     int bcccounter = 0;
				     for (String recipient : bccAddressList) {
				    	 bccrecipientAddress[bcccounter] = new InternetAddress(recipient.trim());
				    	 bcccounter++;
				     }
			     } else {
			    	 bccrecipientAddress = new InternetAddress[0];
				}
			     
			     
				     Properties props = new Properties();
				     props.put("mail.smtp.host", host);
				     props.put("mail.debug", "true");
			     
			     	 Session session = Session.getInstance(props);
			     	
			         Message msg = new MimeMessage(session);

			         msg.setFrom(new InternetAddress(from, "Ask Apollo"));
			         msg.setRecipients(Message.RecipientType.TO, torecipientAddress);
			         msg.setRecipients(Message.RecipientType.CC, ccrecipientAddress);
			         msg.setRecipients(Message.RecipientType.BCC, bccrecipientAddress);
			         msg.setSubject((String) objectArray[3]);
		
	        		 msg.setSentDate(new Date());
			         msg.setContent((String) objectArray[4], "text/html");
			         
			         Transport.send(msg);
			    
	     }
	     catch (Exception mex) {
	         mex.printStackTrace();
	     }
	
}

}
