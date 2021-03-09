package com.ApolloCrownJob;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.mail.smtp.SMTPTransport;

public class BIZReports {

	// Staging
	// private static final String URL =
	// "jdbc:mysql://13.71.124.208:3306/LocateApollo";

	// Production
	private static final String URL = "jdbc:mysql://13.71.122.152:3306/LocateApollo?autoReconnect=true&useSSL=false";

	// Staging
	private static final String USER_NAME = "root";
	private static final String PASSWORD = "askApolloApp@123";

	private static final String office365_SMTP_USER_NAME1 = "askapollo@apollohospitals.com";
	private static final String office365_SMTP_PASSWORD1 = "AskDigital@4321";

	private static Session office365Session1;

	// Staging
	/*
	 * public static final String orderMediceUrl =
	 * "http://rest.askapollo.com:9047/restservice.svc/OrderMedicineCountDetailsforSourceApp";
	 * public static final String consultUrl =
	 * "http://rest.askapollo.com:9047/restservice.svc/OnlineConsultationCountDetailsforSourceApp";
	 * public static final String consultHospitalUrl =
	 * "http://rest.askapollo.com:9047/restservice.svc/HospitalwiseOnlineConsultationCountDetailsforSourceApp";
	 * public static final String consultSpecialityUrl =
	 * "http://rest.askapollo.com:9047/restservice.svc/SpecialitywiseOnlineConsultationCountDetailsforSourceApp";
	 */
	// public static final String physicalURL =
	// "https://service.askapollo.com:44344/physicalconsultapi/api/eDocHospital/GetAppointmentsCountusingFromDateAndToDate/";

	// Production
	public static final String orderMediceUrl = "https://service.askapollo.com:44344/onlineconsultationRest/restservice.svc/OrderMedicineCountDetailsforSourceApp";
	public static final String consultUrl = "https://service.askapollo.com:44344/onlineconsultationRest/restservice.svc/OnlineConsultationCountDetailsforSourceApp";
	public static final String consultHospitalUrl = "https://service.askapollo.com:44344/onlineconsultationRest/restservice.svc/HospitalwiseOnlineConsultationCountDetailsforSourceApp";
	public static final String consultSpecialityUrl = "https://service.askapollo.com:44344/onlineconsultationRest/restservice.svc/SpecialitywiseOnlineConsultationCountDetailsforSourceApp";
	public static final String physicalURL = "https://www.askapollo.com/edocapiservice/api/eDocHospital/GetAppointmentsCountusingFromDateAndToDate/";

	public static String adminId = "AskApollo";
	public static String adminPwd = "AskApollo";
	public static String SourceApp = "85BB5F00-5F45-464B-8965-1F0A7E331D29~AskApolloWeb";

	public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static DateFormat physicalDateFormat = new SimpleDateFormat("MM-dd-yyyy");
	public static String orderAndOnlineFromDateFinal = null;
	public static String orderAndOnlineToDateFinal = null;
	public static String physicalFromDateFinal = null;
	public static String physicalToDateFinal = null;

	public static int orderAppoinTotal = 0;
	public static int orderCancelTotal = 0;
	public static int onlineConsultTotal = 0;
	public static int onlineCancelTotal = 0;
	public static int physicalApoiTotal = 0;
	public static int internationalTotal = 0;
	public static int homeCareTotal = 0;
	public static int diagnosticsTotal = 0;
	public static int healthCheckTotal = 0;

	public static String status = null;
	public static StringBuilder sb = new StringBuilder();

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

		try {

			status = args[0];
			// status = "1";

			String input = null;

			if (status.equals("1")) {

				Calendar fromCall = Calendar.getInstance();
				fromCall.add(Calendar.DATE, -1);
				Date fromCalResult = fromCall.getTime();
				orderAndOnlineFromDateFinal = dateFormat.format(fromCalResult);
				orderAndOnlineToDateFinal = dateFormat.format(fromCalResult);

				physicalFromDateFinal = physicalDateFormat.format(fromCalResult);
				physicalToDateFinal = physicalDateFormat.format(fromCalResult);

			}

			if (status.equals("2")) {

				Calendar fromCall_Monthly = Calendar.getInstance();
				fromCall_Monthly.add(Calendar.MONTH, -1);
				fromCall_Monthly.set(Calendar.DATE, 1);
				Date fromCall_Monthly_Result = fromCall_Monthly.getTime();

				Calendar calendar_Monthly = Calendar.getInstance();
				calendar_Monthly.add(Calendar.MONTH, -1);
				calendar_Monthly.set(Calendar.DAY_OF_MONTH, calendar_Monthly.getActualMaximum(Calendar.DAY_OF_MONTH));

				orderAndOnlineFromDateFinal = dateFormat.format(fromCall_Monthly_Result);
				orderAndOnlineToDateFinal = dateFormat.format(calendar_Monthly.getTime());

				physicalFromDateFinal = physicalDateFormat.format(fromCall_Monthly_Result);
				physicalToDateFinal = physicalDateFormat.format(calendar_Monthly.getTime());

			}

			System.out.println("Online And Order Medicine: From Date: " + orderAndOnlineFromDateFinal + " To Date: "
					+ orderAndOnlineToDateFinal);
			System.out.println(
					"Physical From date: " + physicalFromDateFinal + " Physical To Date: " + physicalToDateFinal);

			input = "{\"adminId\":\"" + adminId + "\",\"adminPassword\":\"" + adminPwd + "\",\"FromDate\":\""
					+ orderAndOnlineFromDateFinal + "\",\"ToDate\":\"" + orderAndOnlineToDateFinal
					+ "\",\"SourceApp\":\"" + SourceApp + "\"}";

			// Order Medicine
			String orderMediceoutput = serviceOutput(input, orderMediceUrl);
			System.out.println(orderAndOnlineToDateFinal + " Order Medicine Service Response " + orderMediceoutput);
			JSONObject retrieveOrderMedicineJSON = retrieveOrderMedicine(orderMediceoutput);
			System.out
					.println(orderAndOnlineToDateFinal + " Order Medicine JSON Response " + retrieveOrderMedicineJSON);

			insertOrderMedicine(retrieveOrderMedicineJSON);

			// Online Consultation
			String consultoutput = serviceOutput(input, consultUrl);
			System.out.println(orderAndOnlineToDateFinal + " Consult Online Service Response " + consultoutput);
			JSONObject retrieveConsultJSON = retrieveConsult(consultoutput);
			System.out.println(orderAndOnlineToDateFinal + " Consult Online JSON Response " + retrieveConsultJSON);
			insertConsult(retrieveConsultJSON);

			// Online Consultation Hospitals
			String consultHospitalOutput = serviceOutput(input, consultHospitalUrl);
			List<JSONObject> retrieveConsultHospitalJSON = retrieveConsultHospital(consultHospitalOutput);
			System.out.println(
					orderAndOnlineToDateFinal + " Consult Online Hospital Response " + retrieveConsultHospitalJSON);
			insertConsultHospital(retrieveConsultHospitalJSON);

			// Online Consultation Specialties
			String consultSpecialityoutput = serviceOutput(input, consultSpecialityUrl);

			List<JSONObject> retrieveConsultSpecialityJSON = retrieveConsultSpeciality(consultSpecialityoutput);
			System.out.println(
					orderAndOnlineToDateFinal + " Consult Online Speciality Response " + retrieveConsultSpecialityJSON);
			// insertConsultSpeciality(retrieveConsultSpecialityJSON);

			// Physical Appointment
			JSONObject physicalOutput = physicalServiceOutput();
			System.out.println(orderAndOnlineToDateFinal + " Physical Appointment Response " + physicalOutput);
			insertPhysicalCount(physicalOutput);

			JSONObject healthCheckJson = healthCheckOutput();
			insertHealthCheck(healthCheckJson);

			ArrayList<JSONObject> jsonArray = new ArrayList<JSONObject>();
			if (status.equals("1")) {

				Calendar calendar_Monthly = Calendar.getInstance();

				if (calendar_Monthly.getTime().getDate() == 1) {
					calendar_Monthly.set(Calendar.DAY_OF_MONTH,
							calendar_Monthly.getActualMinimum(Calendar.DAY_OF_MONTH));
					calendar_Monthly.add(Calendar.MONTH, -1);
				} else {
					calendar_Monthly.set(Calendar.DAY_OF_MONTH,
							calendar_Monthly.getActualMinimum(Calendar.DAY_OF_MONTH));
				}

				for (;;) {

					String dt = dateFormat.format(calendar_Monthly.getTime());

					if (dt.equals(LocalDate.now().toString())) {
						break;
					}

					jsonArray.add(retrieveListObjects(dt));
					calendar_Monthly.add(Calendar.DATE, 1);
				}
				String subject = htmlFormat(jsonArray);

				office365SMTP1(subject);
				// getPepipost(subject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String htmlFormat(ArrayList<JSONObject> jsonArray) {

		String startDate = "";
		String lastDate = "";

		try {

			if (sb != null) {
				sb.append(
						"Order Medicine, Online Consultation,Physical Appointments and HealthCheck count for Android and IOS \n");
				sb.append(" \n \n");
				sb.append("<html>");
				sb.append("<head>");
				sb.append("<style>");
				sb.append("table, th, td { border: 0.1px solid black; padding: 0px }");
				sb.append("</style>");
				sb.append("</head>");
				sb.append("<table>");
				sb.append("<th>Date</th>");
				sb.append("<th>Order Medicine Booked</th>");
				sb.append("<th>Order Medicine Cancelled</th>");
				sb.append("<th>Online Consultation Booked</th>");
				sb.append("<th>Online Consultation cancelled</th>");
				sb.append("<th>Physical Appointments</th>");
				sb.append("<th>International Bookings</th>");
				sb.append("<th>Home Care Bookings</th>");
				sb.append("<th>Diagnostics Bookings</th>");
				sb.append("<th>HealthCheck Bookings</th>");

			}

			int i = 0;

			for (JSONObject jsonObject : jsonArray) {

				orderAppoinTotal += jsonObject.getInt("orderApoinCount");
				orderCancelTotal += jsonObject.getInt("orderCancelCount");
				onlineConsultTotal += jsonObject.getInt("onlineApoinCount");
				onlineCancelTotal += jsonObject.getInt("onlineCancelCount");
				physicalApoiTotal += jsonObject.getInt("physicalApoinCount");
				internationalTotal += jsonObject.getInt("internationalCount");
				homeCareTotal += jsonObject.getInt("homeCareCount");
				diagnosticsTotal += jsonObject.getInt("diagnosticsCount");
				healthCheckTotal += jsonObject.getInt("healthCheckCount");

				sb.append("<tr>");
				sb.append("<td align=\"center\"> " + jsonObject.getString("date") + " </td>");
				sb.append("<td align=\"center\"> " + jsonObject.getInt("orderApoinCount") + " </td>");
				sb.append("<td align=\"center\"> " + jsonObject.getInt("orderCancelCount") + " </td>");
				sb.append("<td align=\"center\"> " + jsonObject.getInt("onlineApoinCount") + " </td>");
				sb.append("<td align=\"center\"> " + jsonObject.getInt("onlineCancelCount") + " </td>");
				sb.append("<td align=\"center\"> " + jsonObject.getInt("physicalApoinCount") + " </td>");
				sb.append("<td align=\"center\"> " + jsonObject.getInt("internationalCount") + " </td>");
				sb.append("<td align=\"center\"> " + jsonObject.getInt("homeCareCount") + " </td>");
				sb.append("<td align=\"center\"> " + jsonObject.getInt("diagnosticsCount") + " </td>");
				sb.append("<td align=\"center\"> " + jsonObject.getInt("healthCheckCount") + " </td>");
				sb.append("</tr>");

				if (i == 0) {
					startDate = jsonObject.getString("date");
				}
				lastDate = jsonObject.getString("date");
				i++;
			}

			sb.append("<tr>");
			sb.append(
					"<td align=\"center\"> " + "Total " + "\n" + " (" + startDate + " to " + lastDate + ")" + " </td>");
			sb.append("<td align=\"center\"> " + orderAppoinTotal + " </td>");
			sb.append("<td align=\"center\"> " + orderCancelTotal + " </td>");
			sb.append("<td align=\"center\"> " + onlineConsultTotal + " </td>");
			sb.append("<td align=\"center\"> " + onlineCancelTotal + " </td>");
			sb.append("<td align=\"center\"> " + physicalApoiTotal + " </td>");
			sb.append("<td align=\"center\"> " + internationalTotal + " </td>");
			sb.append("<td align=\"center\"> " + homeCareTotal + " </td>");
			sb.append("<td align=\"center\"> " + diagnosticsTotal + " </td>");
			sb.append("<td align=\"center\"> " + healthCheckTotal + " </td>");
			sb.append("</tr>");

			sb.append("</table>");
			sb.append("</body>");
			sb.append("</html>");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static JSONObject retrieveListObjects(String monthStartDate) {

		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rs6 = null;
		ResultSet rs7 = null;

		Connection con = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		PreparedStatement stmt5 = null;
		PreparedStatement stmt6 = null;
		PreparedStatement stmt7 = null;

		JSONObject jsonObject = new JSONObject();

		try {

			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(URL, USER_NAME, PASSWORD);

			String getOrderMedicineSql = "SELECT SUM(Order_Medicine_Count_for_Android) + SUM(Order_Medicine_Count_for_iOS)"
					+ "as orderApointtotal, SUM(Order_Cancellations_Count_for_Android) + SUM(Order_Cancellations_Count_for_iOS) as onlineCanceltotal"
					+ "  FROM Order_Medicine_Count WHERE FromDate = '" + monthStartDate + "' and ToDate = '"
					+ monthStartDate + "'";

			String getOnlineConsultSql = "SELECT SUM(Online_Appointments_Count_for_Android) + SUM(Online_Appointments_Count_for_iOS)"
					+ "as onlineApointtotal, SUM(Online_Appointments_Cancellations_Count_for_Android) + SUM(Online_Appointments_Cancellations_Count_for_iOS) "
					+ " as onlineCanceltotal  FROM Online_Consultations_Count WHERE " + "FromDate = '" + monthStartDate
					+ "' and ToDate = '" + monthStartDate + "'";
			String getPhysicalSql = "select sum(CountOfAndroidAppointments) + sum(CountOfiOSAppointments) as "
					+ "physicalAppoinCount from Physical_Appointments_Count where FromDate = '" + monthStartDate
					+ "' and ToDate = '" + monthStartDate + "'";
			String getHomeCareSql = "select count(*) from Apollo_HomeCare_Details where DATE(CreatedDT) = '"
					+ monthStartDate + "' ORDER BY CreatedDT";

			String getInternationalSql = "select count(*) from PA_InternationalFlow_App_FeedBack_Mails where ToAddress = 'internationalcare@apollohospitals.com' and "
					+ "MailSubject = 'Physical Appointment Request' and CreatedDT like '%" + monthStartDate
					+ "%' ORDER BY CreatedDT";

			String getDiagnosticsSql = "SELECT count(*) FROM Log_details where Error_Id = 15 "
					+ "and Logged_Date like '%" + monthStartDate + "%' ORDER BY Logged_Date";

			String healthCheckSql = "SELECT SUM(CountOfAndroidAppointments) + SUM(CountOfiOSAppointments)FROM Health_Check_Count where FromDate ='"
					+ monthStartDate + " 00:00:00'and ToDate = '" + monthStartDate + " 00:00:00'";

			stmt1 = con.prepareCall(getOrderMedicineSql);
			stmt2 = con.prepareCall(getOnlineConsultSql);
			stmt3 = con.prepareCall(getPhysicalSql);
			stmt4 = con.prepareCall(getHomeCareSql);
			stmt5 = con.prepareCall(getInternationalSql);
			stmt6 = con.prepareCall(getDiagnosticsSql);
			stmt7 = con.prepareCall(healthCheckSql);
			rs1 = stmt1.executeQuery();
			rs2 = stmt2.executeQuery();
			rs3 = stmt3.executeQuery();
			rs4 = stmt4.executeQuery();
			rs5 = stmt5.executeQuery();
			rs6 = stmt6.executeQuery();
			rs7 = stmt7.executeQuery();
			jsonObject.put("date", monthStartDate);

			while (rs1.next()) {
				jsonObject.put("orderApoinCount", rs1.getInt(1));
				jsonObject.put("orderCancelCount", rs1.getInt(2));
			}
			while (rs2.next()) {
				jsonObject.put("onlineApoinCount", rs2.getInt(1));
				jsonObject.put("onlineCancelCount", rs2.getInt(2));
			}

			while (rs3.next()) {
				jsonObject.put("physicalApoinCount", rs3.getInt(1));
			}

			while (rs4.next()) {
				jsonObject.put("homeCareCount", rs4.getInt(1));
			}

			while (rs5.next()) {
				jsonObject.put("internationalCount", rs5.getInt(1));
			}

			while (rs6.next()) {
				jsonObject.put("diagnosticsCount", rs6.getInt(1));
			}
			while (rs7.next()) {
				jsonObject.put("healthCheckCount", rs7.getInt(1));
			}

		} catch (Exception e) {
			try {
				if (stmt1 != null) {
					stmt1.close();
				}
				if (stmt2 != null) {
					stmt2.close();
				}
				if (stmt3 != null) {
					stmt3.close();
				}
				if (stmt4 != null) {
					stmt4.close();
				}
				if (stmt5 != null) {
					stmt5.close();
				}
				if (stmt6 != null) {
					stmt6.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				if (stmt1 != null) {
					stmt1.close();
				}
				if (stmt2 != null) {
					stmt2.close();
				}
				if (stmt3 != null) {
					stmt3.close();
				}
				if (stmt4 != null) {
					stmt4.close();
				}
				if (stmt5 != null) {
					stmt5.close();
				}
				if (stmt6 != null) {
					stmt6.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonObject;
	}

	private static void office365SMTP1(String subject) {

		try {

			String from = "askapollo@apollohospitals.com";

			Session office365SessionObject = getOffice365SMTPSession1();
			Message message = new MimeMessage(office365SessionObject);

			String toAddress = "anurag_v@apollohospitals.com,prashant_sharma@apollohospitals.com,sivananda_p@apollohospitals.com,chaitanya_a@apollohospitals.com,saaduddin_m@apollohospitals.com,mohanakrishna_s@apollohospitals.com,ravinder_kp@apollohospitals.com";
			//String toAddress = "ravinder_kp@apollohospitals.com";
			InternetAddress[] torecipientAddress = null;
			if (!toAddress.equalsIgnoreCase("")) {
				String[] bccAddressList = toAddress.replaceAll("\\s", "").split(",");
				torecipientAddress = new InternetAddress[bccAddressList.length];
				int bcccounter = 0;
				for (String recipient : bccAddressList) {
					torecipientAddress[bcccounter] = new InternetAddress(recipient.trim());
					bcccounter++;
				}
			}

			message.setFrom(new InternetAddress(from, "Ask Apollo"));
			message.setRecipients(Message.RecipientType.TO, torecipientAddress);
			message.setSubject("MIS Reports");

			message.setContent(subject, "text/html");
			SMTPTransport transport = (SMTPTransport) office365SessionObject.getTransport("smtp");

			transport.connect("smtp.office365.com", office365_SMTP_USER_NAME1, office365_SMTP_PASSWORD1);
			SMTPTransport.send(message);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private static void insertConsult(JSONObject retrieveConsultJSON) {

		Connection con = null;
		CallableStatement stmt = null;
		try {

			if (con == null) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(URL, USER_NAME, PASSWORD);

				String insertConsultSql = null;

				if (status.equalsIgnoreCase("1")) {
					insertConsultSql = "{call usp_Insert_Online_Consultations_Count(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
				} else {
					insertConsultSql = "{call usp_Insert_Online_Consultations_Count_Monthwise(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
				}

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

				Date fromDate = (Date) retrieveConsultJSON.get("fromDate");
				String strFromDate = dateFormat.format(fromDate);
				java.util.Date fromUtilDate = simpleDateFormat.parse(strFromDate);
				java.sql.Date sqlStartDate = new java.sql.Date(fromUtilDate.getTime());

				Date toDate = (Date) retrieveConsultJSON.get("toDate");
				String strToDate = dateFormat.format(toDate);
				java.util.Date toUtilDate = simpleDateFormat.parse(strToDate);
				java.sql.Date sqlToDate = new java.sql.Date(toUtilDate.getTime());

				stmt = con.prepareCall(insertConsultSql);

				stmt.setDate(1, sqlStartDate);
				stmt.setDate(2, sqlToDate);
				stmt.setInt(3, retrieveConsultJSON.getInt("appointments_Android"));
				stmt.setInt(4, retrieveConsultJSON.getInt("appointments_iOS"));
				stmt.setInt(5, retrieveConsultJSON.getInt("appointments_Web"));
				stmt.setInt(6, retrieveConsultJSON.getInt("review_Android"));
				stmt.setInt(7, retrieveConsultJSON.getInt("review_iOS"));
				stmt.setInt(8, retrieveConsultJSON.getInt("review_Web"));
				stmt.setInt(9, retrieveConsultJSON.getInt("cancellations_Android"));
				stmt.setInt(10, retrieveConsultJSON.getInt("cancellations_iOS"));
				stmt.setInt(11, retrieveConsultJSON.getInt("cancellations_Web"));
				stmt.setInt(12, retrieveConsultJSON.getInt("completed_Android"));
				stmt.setInt(13, retrieveConsultJSON.getInt("completed_iOS"));
				stmt.setInt(14, retrieveConsultJSON.getInt("completed_Web"));

				stmt.executeQuery();
			}

		} catch (Exception e) {
			try {
				if (con != null) {
					con.close();
				}
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
	}

	private static void insertConsultHospital(List<JSONObject> retrieveConsultHospitalJSON) {

		Connection con = null;
		CallableStatement stmt = null;
		try {

			if (con == null) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(URL, USER_NAME, PASSWORD);

				String insertConsultHospitalSql = null;

				if (status.equalsIgnoreCase("1")) {
					insertConsultHospitalSql = "{call usp_Insert_Online_Appointments_Hospitals_Count(?,?,?,?)}";
				} else {
					insertConsultHospitalSql = "{call usp_Insert_Online_Appointments_Hospitals_Count_Monthwise(?,?,?,?)}";
				}

				stmt = con.prepareCall(insertConsultHospitalSql);
				con.setAutoCommit(false);

				SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");

				String startDate = orderAndOnlineFromDateFinal;
				java.util.Date startUtilDate = sdf1.parse(startDate);
				java.sql.Date sqlStartDate = new java.sql.Date(startUtilDate.getTime());

				String toDate = orderAndOnlineToDateFinal;
				java.util.Date toUtilDate = sdf1.parse(toDate);
				java.sql.Date sqltoDate = new java.sql.Date(toUtilDate.getTime());

				for (JSONObject jsonObject : retrieveConsultHospitalJSON) {

					stmt.setDate(1, sqlStartDate);
					stmt.setDate(2, sqltoDate);
					stmt.setString(3, jsonObject.getString("HospitalName"));
					stmt.setInt(4, jsonObject.getInt("Count"));

					stmt.addBatch();
				}

				stmt.executeQuery();
				con.commit();
			}

		} catch (Exception e) {
			try {
				con.rollback();
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

	}

	private static void insertPhysicalCount(JSONObject response) {

		Connection con = null;
		CallableStatement stmt = null;
		CallableStatement hospitalStmt = null;
		CallableStatement specialityStmt = null;

		try {

			if (con == null) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(URL, USER_NAME, PASSWORD);

				String insertPhysicalCountSql = "";
				String insertPhysicalHospitalCountSql = "";
				String insertPhysicalSpecialityCountSql = "";

				if (status.equalsIgnoreCase("1")) {
					insertPhysicalCountSql = "{call usp_Insert_Physical_Appointments_Count(?,?,?,?,?)}";
					insertPhysicalHospitalCountSql = "{call usp_Insert_Physical_Appointments_Hospitals_Count(?,?,?,?)}";
					insertPhysicalSpecialityCountSql = "{call usp_Insert_Physical_Appointments_Specialities_Count(?,?,?,?)}";
				} else {
					insertPhysicalCountSql = "{call usp_Insert_Physical_Appointments_Count_Monthwise(?,?,?,?,?)}";
					insertPhysicalHospitalCountSql = "{call usp_Insert_Physical_Appointments_Hospitals_Count_Monthwise(?,?,?,?)}";
					insertPhysicalSpecialityCountSql = "{call usp_Insert_Physical_Appointments_Specialities_Count_Monthwise(?,?,?,?)}";
				}

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");

				String fromDate = (String) response.get("FromDate");
				java.util.Date fromUtilDate = simpleDateFormat.parse(fromDate);
				java.sql.Date sqlStartDate = new java.sql.Date(fromUtilDate.getTime());

				String toDate = (String) response.get("ToDate");
				java.util.Date toUtilDate = simpleDateFormat.parse(toDate);
				java.sql.Date sqlEndDate = new java.sql.Date(toUtilDate.getTime());

				stmt = con.prepareCall(insertPhysicalCountSql);
				hospitalStmt = con.prepareCall(insertPhysicalHospitalCountSql);
				specialityStmt = con.prepareCall(insertPhysicalSpecialityCountSql);

				stmt.setDate(1, sqlStartDate);
				stmt.setDate(2, sqlEndDate);
				stmt.setInt(3, response.getInt("CountOfAndroidAppointments"));
				stmt.setInt(4, response.getInt("CountOfWebAppointments"));
				stmt.setInt(5, response.getInt("CountOfiOSAppointments"));

				stmt.executeQuery();

				JSONArray jsonHospitalArray = response.getJSONArray("lstAppointmentsCountbyHospitalBO");
				JSONArray jsonSpecialityArray = response.getJSONArray("lstAppointmentsCountbySpcialityBO");

				for (int i = 0; i < jsonHospitalArray.length(); i++) {
					hospitalStmt.setDate(1, sqlStartDate);
					hospitalStmt.setDate(2, sqlEndDate);
					hospitalStmt.setString(3, jsonHospitalArray.getJSONObject(i).getString("hospitalName"));
					hospitalStmt.setInt(4, jsonHospitalArray.getJSONObject(i).getInt("appointmentCount"));
					hospitalStmt.addBatch();
					;
				}
				hospitalStmt.executeBatch();

				for (int i = 0; i < jsonSpecialityArray.length(); i++) {
					specialityStmt.setDate(1, sqlStartDate);
					specialityStmt.setDate(2, sqlEndDate);
					specialityStmt.setString(3, jsonSpecialityArray.getJSONObject(i).getString("specialityName"));
					specialityStmt.setInt(4, jsonSpecialityArray.getJSONObject(i).getInt("appointmentCount"));
					specialityStmt.addBatch();
				}
				specialityStmt.executeBatch();
			}

		} catch (Exception e) {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (hospitalStmt != null) {
					hospitalStmt.close();
				}
				if (specialityStmt != null) {
					specialityStmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (hospitalStmt != null) {
					hospitalStmt.close();
				}
				if (specialityStmt != null) {
					specialityStmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public static void insertOrderMedicine(JSONObject orderMedicineJSON) {

		Connection con = null;
		CallableStatement stmt = null;

		try {

			if (con == null) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(URL, USER_NAME, PASSWORD);

				String insertOrderMedicineIdSql = "";

				if (status.equalsIgnoreCase("1")) {
					insertOrderMedicineIdSql = "{call usp_Insert_Into_Order_Medicine_Count(?,?,?,?,?,?,?,?,?,?,?)}";
				} else {
					insertOrderMedicineIdSql = "{call usp_Insert_Into_Order_Medicine_Count_Monthwise(?,?,?,?,?,?,?,?,?,?,?)}";
				}

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

				Date fromDate = (Date) orderMedicineJSON.get("fromDate");

				String strFromDate = dateFormat.format(fromDate);
				java.util.Date fromUtilDate = simpleDateFormat.parse(strFromDate);
				java.sql.Date sqlStartDate = new java.sql.Date(fromUtilDate.getTime());
				Date toDate = (Date) orderMedicineJSON.get("toDate");
				String strToDate = dateFormat.format(toDate);
				java.util.Date toUtilDate = simpleDateFormat.parse(strToDate);
				java.sql.Date sqlToDate = new java.sql.Date(toUtilDate.getTime());

				stmt = con.prepareCall(insertOrderMedicineIdSql);
				stmt.setDate(1, sqlStartDate);
				stmt.setDate(2, sqlToDate);
				stmt.setInt(3, orderMedicineJSON.getInt("order_Android"));
				stmt.setInt(4, orderMedicineJSON.getInt("order_iOS"));
				stmt.setInt(5, orderMedicineJSON.getInt("order_Web"));
				stmt.setInt(6, orderMedicineJSON.getInt("cancellations_Android"));
				stmt.setInt(7, orderMedicineJSON.getInt("cancellations_iOS"));
				stmt.setInt(8, orderMedicineJSON.getInt("cancellations_Web"));
				stmt.setInt(9, orderMedicineJSON.getInt("delivered_Android"));
				stmt.setInt(10, orderMedicineJSON.getInt("delivered_iOS"));
				stmt.setInt(11, orderMedicineJSON.getInt("delivered_Web"));

				stmt.executeQuery();
			}

		} catch (Exception e) {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static JSONObject physicalServiceOutput() {

		JSONObject jsonObject = new JSONObject();

		try {

			Client client = Client.create();

			String url = physicalURL + physicalFromDateFinal + "/" + physicalToDateFinal
					+ "/CF662D63-D1F5-47F0-9EAF-D305ED82727A";
			WebResource webResource = client.resource(url);
			ClientResponse clientResponse = webResource.type("application/json").get(ClientResponse.class);
			String output = clientResponse.getEntity(String.class);

			JSONArray jsonArray = new JSONArray(output);
			jsonObject = (JSONObject) jsonArray.get(0);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonObject;

	}

	private static JSONObject retrieveConsult(String consultoutput) {

		JSONObject onlineConsultOutput = null;

		try {

			String OnlineResult = "[" + consultoutput + "]";
			JSONArray onlineResultsonArray = new JSONArray(OnlineResult);

			JSONObject jsonObject = null;

			for (int i = 0; i < onlineResultsonArray.length(); i++) {
				onlineConsultOutput = new JSONObject();
				jsonObject = onlineResultsonArray.getJSONObject(i);
				onlineConsultOutput.put("status", jsonObject.getString("ResponceCode"));

				String statusResult = jsonObject.getString("Result");

				if (onlineConsultOutput.getString("status").equalsIgnoreCase("0")) {

					JSONArray jsonArray = new JSONArray(statusResult);

					DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

					onlineConsultOutput.put("fromDate",
							dateFormat.parse(jsonArray.getJSONObject(0).getString("FromDate")));
					onlineConsultOutput.put("toDate", dateFormat.parse(jsonArray.getJSONObject(0).getString("Todate")));
					onlineConsultOutput.put("appointments_Android",
							jsonArray.getJSONObject(0).getInt("Online_Appointments_Count_for_Android"));
					onlineConsultOutput.put("appointments_iOS",
							jsonArray.getJSONObject(0).getInt("Online_Appointments_Count_for_iOS"));
					onlineConsultOutput.put("appointments_Web",
							jsonArray.getJSONObject(0).getInt("Online_Appointments_Count_for_Web"));
					onlineConsultOutput.put("review_Android",
							jsonArray.getJSONObject(0).getInt("Online_Review_Appointments_Count_for_Android"));
					onlineConsultOutput.put("review_iOS",
							jsonArray.getJSONObject(0).getInt("Online_Review_Appointments_Count_for_iOS"));
					onlineConsultOutput.put("review_Web",
							jsonArray.getJSONObject(0).getInt("Online_Review_Appointments_Count_for_Web"));
					onlineConsultOutput.put("cancellations_Android",
							jsonArray.getJSONObject(0).getInt("Online_Appointments_Cancellations_Count_for_Android"));
					onlineConsultOutput.put("cancellations_iOS",
							jsonArray.getJSONObject(0).getInt("Online_Appointments_Cancellations_Count_for_iOS"));
					onlineConsultOutput.put("cancellations_Web",
							jsonArray.getJSONObject(0).getInt("Online_Appointments_Cancellations_Count_for_Web"));
					onlineConsultOutput.put("completed_Android",
							jsonArray.getJSONObject(0).getInt("Online_Appointments_Completed_Count_for_Android"));
					onlineConsultOutput.put("completed_iOS",
							jsonArray.getJSONObject(0).getInt("Online_Appointments_Completed_Count_for_iOS"));
					onlineConsultOutput.put("completed_Web",
							jsonArray.getJSONObject(0).getInt("Online_Appointments_Completed_Count_for_Web"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return onlineConsultOutput;
	}

	private static JSONObject retrieveOrderMedicine(String orderMediceoutput) {

		JSONObject orderMedicineJSON = null;

		try {

			String outpurResu = "[" + orderMediceoutput + "]";
			JSONArray jsonArray = new JSONArray(outpurResu);
			String json = null;
			orderMedicineJSON = new JSONObject();
			if (jsonArray.length() == 1) {
				orderMedicineJSON.put("status", jsonArray.getJSONObject(0).getString("ResponceCode"));
				json = jsonArray.getJSONObject(0).getString("Result");
			}

			if (orderMedicineJSON.getString("status").equalsIgnoreCase("0")) {

				JSONArray stringArray = new JSONArray(json);

				JSONObject orderMedicine = null;
				if (stringArray.length() > 0) {
					orderMedicine = new JSONObject();
					orderMedicine = stringArray.getJSONObject(0);
				}

				DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

				orderMedicineJSON.put("fromDate", dateFormat.parse(orderMedicine.getString("FromDate")));
				orderMedicineJSON.put("toDate", dateFormat.parse(orderMedicine.getString("Todate")));
				orderMedicineJSON.put("order_Android", orderMedicine.getInt("Order_Medicine_Count_for_Android"));
				orderMedicineJSON.put("order_iOS", orderMedicine.getInt("Order_Medicine_Count_for_iOS"));
				orderMedicineJSON.put("order_Web", orderMedicine.getInt("Order_Medicine_Count_for_Web"));
				orderMedicineJSON.put("cancellations_Android",
						orderMedicine.getInt("Order_Cancellations_Count_for_Android"));
				orderMedicineJSON.put("cancellations_iOS", orderMedicine.getInt("Order_Cancellations_Count_for_iOS"));
				orderMedicineJSON.put("cancellations_Web", orderMedicine.getInt("Order_Cancellations_Count_for_Web"));
				orderMedicineJSON.put("delivered_Android", orderMedicine.getInt("Order_Delivered_Count_for_Android"));
				orderMedicineJSON.put("delivered_iOS", orderMedicine.getInt("Order_Delivered_Count_for_iOS"));
				orderMedicineJSON.put("delivered_Web", orderMedicine.getInt("Order_Delivered_Count_for_Web"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderMedicineJSON;
	}

	private static List<JSONObject> retrieveConsultHospital(String consultHospitalOutput) {

		List<JSONObject> responseArray = new ArrayList<JSONObject>();

		try {

			consultHospitalOutput = "[" + consultHospitalOutput + "]";
			JSONArray jsonArray = new JSONArray(consultHospitalOutput);

			if (jsonArray.getJSONObject(0).get("ResponceCode").equals("0")) {
				String result = jsonArray.getJSONObject(0).getString("Result");
				JSONArray resultArray = new JSONArray(result);
				JSONObject responseObject;
				for (int i = 0; i < resultArray.length(); i++) {
					responseObject = resultArray.getJSONObject(i);
					responseArray.add(responseObject);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseArray;
	}

	private static List<JSONObject> retrieveConsultSpeciality(String consultSpecialityoutput) {

		List<JSONObject> responseArray = new ArrayList<JSONObject>();

		try {

			consultSpecialityoutput = "[" + consultSpecialityoutput + "]";
			JSONArray jsonArray = new JSONArray(consultSpecialityoutput);

			if (jsonArray.getJSONObject(0).getString("ResponceCode").equalsIgnoreCase("0")) {
				String result = jsonArray.getJSONObject(0).getString("Result");
				JSONArray resultArray = new JSONArray(result);
				JSONObject responseObject;
				for (int i = 0; i < resultArray.length(); i++) {
					responseObject = resultArray.getJSONObject(i);
					responseArray.add(responseObject);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseArray;

	}

	public static String serviceOutput(String input, String inputURL) {

		Client client = Client.create();

		WebResource webResource = client.resource(inputURL);
		ClientResponse clientResponse = webResource.type("application/json").post(ClientResponse.class, input);
		String output = clientResponse.getEntity(String.class);

		return output;

	}

	public static JSONObject healthCheckOutput() {

		JSONObject jsonObject = new JSONObject();
		JSONObject resultJson = null;
		try {

			Client client = Client.create();
			String url = "https://www.askapollo.com/eDocAPIService/api/eDocHospital/GetHcApptsCountBySourceBetweenDates/"
					+ physicalFromDateFinal + "/" + physicalToDateFinal + "/CF662D63-D1F5-47F0-9EAF-D305ED82727A";
			WebResource webResource = client.resource(url);
			ClientResponse clientResponse = webResource.type("application/json").get(ClientResponse.class);
			String output = clientResponse.getEntity(String.class);
			JSONArray jsonArray = new JSONArray(output);
			jsonObject = (JSONObject) jsonArray.get(0);
			resultJson = new JSONObject();
			resultJson.put("CountOfAndroidAppointments", jsonObject.getInt("CountOfAndroidAppointments"));
			resultJson.put("CountOfiOSAppointments", jsonObject.getInt("CountOfiOSAppointments"));
			resultJson.put("CountOfWebAppointments", jsonObject.getInt("CountOfWebAppointments"));
			resultJson.put("FromDate", jsonObject.get("FromDate"));
			resultJson.put("ToDate", jsonObject.get("ToDate"));
			System.out.println(physicalFromDateFinal + " HealthCheck Response " + resultJson.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultJson;

	}

	private static void insertHealthCheck(JSONObject retrieveConsultJSON) {

		Connection con = null;
		CallableStatement stmt = null;
		try {

			if (con == null) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(URL, USER_NAME, PASSWORD);

				String insertConsultSql = null;

				if (status.equalsIgnoreCase("1")) {
					insertConsultSql = "{call usp_Insert_Health_Check_Count(?,?,?,?,?)}";
				} else {
					insertConsultSql = "{call usp_Insert_HealthCheck_Count_Monthwise(?,?,?,?,?)}";
				}
				stmt = con.prepareCall(insertConsultSql);

				String str = (String) retrieveConsultJSON.get("FromDate");
				String[] inDate = str.split("-");
				str = inDate[2] + "-" + inDate[0] + "-" + inDate[1] + " 00:00:00";

				stmt.setString(1, str);
				stmt.setString(2, str);
				stmt.setInt(3, retrieveConsultJSON.getInt("CountOfAndroidAppointments"));
				stmt.setInt(4, retrieveConsultJSON.getInt("CountOfiOSAppointments"));
				stmt.setInt(5, retrieveConsultJSON.getInt("CountOfWebAppointments"));
				stmt.executeQuery();
			}

		} catch (Exception e) {
			try {
				if (con != null) {
					con.close();
				}
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
	}

	public static String getPepipost(String subject) {
		String resp = "fail";
		try {

			RestTemplate template = new RestTemplate();
			HttpHeaders http = new HttpHeaders();
			http.setContentType(MediaType.APPLICATION_JSON);
			http.set("api_key", "87a840a628bb818014be8a703c414968");
			JSONObject json = new JSONObject();
			JSONArray innArry = new JSONArray();
			JSONObject innjson = new JSONObject();
			JSONArray innOutArry = new JSONArray();
			JSONObject fromJson = new JSONObject();
			innArry.put(
					"prashant_sharma@apollohospitals.com,sivananda_p@apollohospitals.com,chaitanya_a@apollohospitals.com,saaduddin_m@apollohospitals.com,mohanakrishna_s@apollohospitals.com,ravinder_kp@apollohospitals.com");
			innjson.put("recipient_cc", innArry);
			innjson.put("recipient", "anurag_v@apollohospitals.com");
			innOutArry.put(innjson);
			json.put("personalizations", innOutArry);
			fromJson.put("fromEmail", "info@info.askapollo.com");
			fromJson.put("fromName", "AskApollo");
			json.put("from", fromJson);
			json.put("subject", "MIS Reports");
			json.put("content", subject);
			HttpEntity<String> entity = new HttpEntity<>(json.toString(), http);
			ResponseEntity<String> response = template.postForEntity("https://api.pepipost.com/v2/sendEmail", entity,
					String.class);
			JSONObject result = new JSONObject(response.getBody());
			resp = result.getString("message");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

}
