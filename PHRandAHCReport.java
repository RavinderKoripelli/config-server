package com.ApolloCrownJob;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjuster;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class PHRandAHCReport {
	public static String db;
	public static int count;
	public static int successCount;
	public static int failCount;
	public static int ahcSuccess;
	public static int ahcfail;
	public static StringBuilder sb;

	static {
		PHRandAHCReport.db = "jdbc:mysql://127.0.0.1:3306/LocateApollo";
		PHRandAHCReport.count = 0;
		PHRandAHCReport.successCount = 0;
		PHRandAHCReport.failCount = 0;
		PHRandAHCReport.ahcSuccess = 0;
		PHRandAHCReport.ahcfail = 0;
		PHRandAHCReport.sb = new StringBuilder();
	}

	public static void main(final String[] args) {
		final String ahcMsg = AHChtmlFormat();
		final String phrMsg = PHRhtmlFormat();
		final String bin = String.valueOf(ahcMsg) + "\n" + phrMsg;
		getPepipost(phrMsg);
		final SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm");
		final String time = sdf.format(new Date());
		final List<LocalDate> list = weeksInCalendar(YearMonth.now());
		int cnt = 0;
		for (final LocalDate date : list) {
			final String response = date + " 01:34";
			if (time.equals(response)) {
				cnt = deleteData();
			}
		}
		if (cnt > 0) {
			System.out.println("Last One Week Data deleted successfully");
		}
		System.out.println(String.valueOf(getTime()) + " Report has been sent Successfully");
	}

	public static String getTime() {
		String newTime = "";
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final String time = sdf.format(new Date());
			final Date d = sdf.parse(time);
			final Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.add(10, 5);
			cal.add(12, 30);
			newTime = sdf.format(cal.getTime());
			return newTime;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return newTime;
		}
	}

	public static JSONObject selectOperation() {
		final String[] dateInfo = getTime().split(" ");
		final String startDare = String.valueOf(dateInfo[0]) + " 00:00:00";
		final String endDate = String.valueOf(dateInfo[0]) + " 23:59:59";
		JSONObject json = null;
		final String query = "SELECT sum(request),sum(success),sum(fail),sum(AHCSuccess),sum(AHCFail) FROM prismCapture WHERE DATE>='"
				+ startDare + "' and DATE<='" + endDate + "'";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			final Connection con = DriverManager.getConnection(PHRandAHCReport.db, "root", "askApolloApp@123");
			final PreparedStatement ps = con.prepareStatement(query);
			final ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json = new JSONObject();
				json.put("Request", rs.getInt(1));
				json.put("PHRSuccess", rs.getInt(2));
				json.put("PHRFail", rs.getInt(3));
				json.put("AHCSuccess", rs.getInt(4));
				json.put("AHCFail", rs.getInt(5));
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return json;
	}

	public static String AHChtmlFormat() {
		String date = "";
		int request = 0;
		int success = 0;
		int fail = 0;
		float successRate = 0.0f;
		try {
			Connection con = null;
			CallableStatement stmt2 = null;
			int count = 0;
			if (con == null) {
				Class.forName("com.mysql.cj.jdbc.Driver");
				con = DriverManager.getConnection(PHRandAHCReport.db, "root", "askApolloApp@123");
				final String getFeedBackListQuery = "SELECT date,request,ahcSuccess,ahcFail,ahcPercentage FROM PrismAHCResponse";
				final String query = "select count(*) from PrismAHCResponse";
				stmt2 = con.prepareCall(query);
				final ResultSet rs1 = stmt2.executeQuery();
				while (rs1.next()) {
					count = rs1.getInt(1);
				}
				final CallableStatement stmt3 = con.prepareCall(getFeedBackListQuery);
				final ResultSet rs2 = stmt3.executeQuery();
				if (PHRandAHCReport.sb != null) {
					PHRandAHCReport.sb.append("AHC Production Service API  Daily Report \n");
					PHRandAHCReport.sb.append(" \n \n");
					PHRandAHCReport.sb.append("<html>");
					PHRandAHCReport.sb.append("<head>");
					PHRandAHCReport.sb.append("<style>");
					PHRandAHCReport.sb.append("table, th, td { border: 0.1px solid black; padding: 0px }");
					PHRandAHCReport.sb.append("</style>");
					PHRandAHCReport.sb.append("</head>");
					PHRandAHCReport.sb.append("<table>");
					PHRandAHCReport.sb.append("<th>Date</th>");
					PHRandAHCReport.sb.append("<th>Total Request</th>");
					PHRandAHCReport.sb.append("<th>Success Response</th>");
					PHRandAHCReport.sb.append("<th>Fail Response</th>");
					PHRandAHCReport.sb.append("<th>Success %</th>");
				}
				while (rs2.next()) {
					date = rs2.getString("date");
					request += rs2.getInt("request");
					success += rs2.getInt("ahcSuccess");
					fail += rs2.getInt("ahcFail");
					successRate += rs2.getFloat("ahcPercentage") / count;
					PHRandAHCReport.sb.append("<tr>");
					PHRandAHCReport.sb.append("<td align=\"center\"> " + rs2.getString("date") + " </td>");
					PHRandAHCReport.sb.append("<td align=\"center\"> " + rs2.getInt("request") + " </td>");
					PHRandAHCReport.sb.append("<td align=\"center\"> " + rs2.getInt("ahcSuccess") + " </td>");
					PHRandAHCReport.sb.append("<td align=\"center\"> " + rs2.getInt("ahcFail") + " </td>");
					PHRandAHCReport.sb.append("<td align=\"center\"> " + rs2.getFloat("ahcPercentage") + " </td>");
					PHRandAHCReport.sb.append("</tr>");
				}
				PHRandAHCReport.sb.append("<tr>");
				PHRandAHCReport.sb.append("<td align=\"center\"> Total \n to till Date </td>");
				PHRandAHCReport.sb.append("<td align=\"center\"> " + request + " </td>");
				PHRandAHCReport.sb.append("<td align=\"center\"> " + success + " </td>");
				PHRandAHCReport.sb.append("<td align=\"center\"> " + fail + " </td>");
				PHRandAHCReport.sb.append("<td align=\"center\"> " + successRate + " </td>");
				PHRandAHCReport.sb.append("</tr>");
				PHRandAHCReport.sb.append("</table>");
				PHRandAHCReport.sb.append("</body>");
				PHRandAHCReport.sb.append("</html>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return PHRandAHCReport.sb.toString();
	}

	public static String PHRhtmlFormat() {
		String date = "";
		int request = 0;
		int success = 0;
		int fail = 0;
		float successRate = 0.0f;
		try {
			Connection con = null;
			CallableStatement stmt2 = null;
			int count = 0;
			if (con == null) {
				Class.forName("com.mysql.cj.jdbc.Driver");
				con = DriverManager.getConnection(PHRandAHCReport.db, "root", "askApolloApp@123");
				final String getFeedBackListQuery = "SELECT date,request,prismSuccess,prismFail,prismPercentage FROM PrismAHCResponse";
				final String query = "select count(*) from PrismAHCResponse";
				stmt2 = con.prepareCall(query);
				final ResultSet rs1 = stmt2.executeQuery();
				while (rs1.next()) {
					count = rs1.getInt(1);
				}
				final CallableStatement stmt3 = con.prepareCall(getFeedBackListQuery);
				final ResultSet rs2 = stmt3.executeQuery();
				if (PHRandAHCReport.sb != null) {
					PHRandAHCReport.sb.append("PHR Production Service API  Daily Report \n");
					PHRandAHCReport.sb.append(" \n \n");
					PHRandAHCReport.sb.append("<html>");
					PHRandAHCReport.sb.append("<head>");
					PHRandAHCReport.sb.append("<style>");
					PHRandAHCReport.sb.append("table, th, td { border: 0.1px solid black; padding: 0px }");
					PHRandAHCReport.sb.append("</style>");
					PHRandAHCReport.sb.append("</head>");
					PHRandAHCReport.sb.append("<table>");
					PHRandAHCReport.sb.append("<th>Date</th>");
					PHRandAHCReport.sb.append("<th>Total Request</th>");
					PHRandAHCReport.sb.append("<th>Success Response</th>");
					PHRandAHCReport.sb.append("<th>Fail Response</th>");
					PHRandAHCReport.sb.append("<th>Success %</th>");
				}
				while (rs2.next()) {
					date = rs2.getString("date");
					request += rs2.getInt("request");
					success += rs2.getInt("prismSuccess");
					fail += rs2.getInt("prismFail");
					successRate += rs2.getFloat("prismPercentage") / count;
					PHRandAHCReport.sb.append("<tr>");
					PHRandAHCReport.sb.append("<td align=\"center\"> " + rs2.getString("date") + " </td>");
					PHRandAHCReport.sb.append("<td align=\"center\"> " + rs2.getInt("request") + " </td>");
					PHRandAHCReport.sb.append("<td align=\"center\"> " + rs2.getInt("prismSuccess") + " </td>");
					PHRandAHCReport.sb.append("<td align=\"center\"> " + rs2.getInt("prismFail") + " </td>");
					PHRandAHCReport.sb.append("<td align=\"center\"> " + rs2.getFloat("prismPercentage") + " </td>");
					PHRandAHCReport.sb.append("</tr>");
				}
				PHRandAHCReport.sb.append("<tr>");
				PHRandAHCReport.sb.append("<td align=\"center\"> Total \n to till Date </td>");
				PHRandAHCReport.sb.append("<td align=\"center\"> " + request + " </td>");
				PHRandAHCReport.sb.append("<td align=\"center\"> " + success + " </td>");
				PHRandAHCReport.sb.append("<td align=\"center\"> " + fail + " </td>");
				PHRandAHCReport.sb.append("<td align=\"center\"> " + successRate + " </td>");
				PHRandAHCReport.sb.append("</tr>");
				PHRandAHCReport.sb.append("</table>");
				PHRandAHCReport.sb.append("</body>");
				PHRandAHCReport.sb.append("</html>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return PHRandAHCReport.sb.toString();
	}

	public static String getPepipost(final String subject) {
		String resp = "fail";
		try {
			final RestTemplate template = new RestTemplate();
			final HttpHeaders http = new HttpHeaders();
			http.setContentType(MediaType.APPLICATION_JSON);
			http.set("api_key", "87a840a628bb818014be8a703c414968");
			final JSONObject json = new JSONObject();
			final JSONArray innArry = new JSONArray();
			final JSONObject innjson = new JSONObject();
			final JSONArray innOutArry = new JSONArray();
			final JSONObject fromJson = new JSONObject();
			innArry.put(
					"mohanakrishna_s@apollohospitals.com,prashant_sharma@apollohospitals.com,sivananda_p@apollohospitals.com,ravinder_kp@apollohospitals.com");
			innjson.put("recipient_cc", innArry);
			innjson.put("recipient", "anurag_v@apollohospitals.com");
			innOutArry.put(innjson);
			json.put("personalizations", innOutArry);
			fromJson.put("fromEmail", "info@info.askapollo.com");
			fromJson.put("fromName", "AskApollo");
			json.put("from", fromJson);
			json.put("subject", "AHC Portal and Prism Production Service Daily Report");
			json.put("content", subject);
			final HttpEntity<String> entity = new HttpEntity<String>(json.toString(), http);
			final ResponseEntity<String> response = template.postForEntity("https://api.pepipost.com/v2/sendEmail",
					entity, String.class, new Object[0]);
			final JSONObject result = new JSONObject(response.getBody());
			resp = result.getString("message");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	public static List<LocalDate> weeksInCalendar(final YearMonth month) {
		final List<LocalDate> firstDaysOfWeeks = new ArrayList<LocalDate>();
		for (LocalDate day = firstDayOfCalendar(month); stillInCalendar(month, day); day = day.plusWeeks(1L)) {
			firstDaysOfWeeks.add(day);
		}
		return firstDaysOfWeeks;
	}

	private static LocalDate firstDayOfCalendar(final YearMonth month) {
		final DayOfWeek FIRST_DAY_OF_WEEK = DayOfWeek.MONDAY;
		return month.atDay(1).with((TemporalAdjuster) FIRST_DAY_OF_WEEK);
	}

	private static boolean stillInCalendar(final YearMonth yearMonth, final LocalDate day) {
		return !day.isAfter(yearMonth.atEndOfMonth());
	}

	public static int deleteData() {
		try {
			Connection con = null;
			CallableStatement stmt = null;
			int count = 0;
			if (con == null) {
				Class.forName("com.mysql.cj.jdbc.Driver");
				con = DriverManager.getConnection(PHRandAHCReport.db, "root", "askApolloApp@123");
				final String getFeedBackListQuery = "Delete from PrismAHCResponse";
				stmt = con.prepareCall(getFeedBackListQuery);
				count = stmt.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return PHRandAHCReport.count;
	}
}