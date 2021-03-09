package com.ApolloCrownJob;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;
import java.io.FileInputStream;
import java.io.File;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.IndexedColors;
import java.util.Date;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.OutputStream;
import java.io.FileOutputStream;

public class ConsultOnlineReport {
	private static String[] columns;
	

	static {
		ConsultOnlineReport.columns = new String[] { "HospitalName", "DoctorName", "Speciality", "CityName",
				"ErrorName", "DeviceName", "UserEmail", "User Mobile", "LoggedTime" };
	}

	public static void main(String[] args) {
		try {
			Workbook book = getWorkBook();
			FileOutputStream fileOut = new FileOutputStream("ConsultOnlineReport.xlsx");
			book.write(fileOut);
			fileOut.close();
			getPepipost();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<ErrorReport> listRecords() {
		Connection con = null;
		PreparedStatement stmt = null;
		ArrayList<ErrorReport> objectList = new ArrayList<ErrorReport>();
		ErrorReport errorReport = null;
		try {
			if (con == null) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://13.71.122.152:3306/LocateApollo", "root",
						"askApolloApp@123");
				Calendar cal = Calendar.getInstance();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				cal.add(5, -1);
				String getFeedBackListQuery = "select * from Log_details where Error_Id=7 and Logged_Date>='"
						+ DayBeforeCurrentDate() + " 18:30:00' and Logged_Date<='" + dateFormat.format(cal.getTime())
						+ " 18:29:59'";
				stmt = con.prepareStatement(getFeedBackListQuery);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					errorReport = new ErrorReport(rs.getString("Hospital_Name"), rs.getString("Doctor_Name"),
							rs.getString("Speciality"), rs.getString("CityName"), rs.getString("Error_Name"),
							rs.getString("Device_Name"), rs.getString("user_email"), rs.getString("user_mobile"),
							updateTime(rs.getString("Logged_Date")));
					objectList.add(errorReport);
					Collections.sort(objectList, new HospitalNameComparator());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objectList;
	}

	public static Workbook getWorkBook() {
		Workbook workbook = null;
		try {
			List<ErrorReport> records = listRecords();
			workbook = new XSSFWorkbook();
			String pattern = "dd-MM-YYYY";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String date = simpleDateFormat.format(new Date());
			Sheet sheet = workbook.createSheet("Report " + date);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 14);
			headerFont.setColor(IndexedColors.BLACK.getIndex());
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < ConsultOnlineReport.columns.length; ++i) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(ConsultOnlineReport.columns[i]);
				cell.setCellStyle(headerCellStyle);
			}
			int rowNum = 1;
			for (ErrorReport errorReport : records) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(errorReport.getHospitalName());
				row.createCell(1).setCellValue(errorReport.getDoctorName());
				row.createCell(2).setCellValue(errorReport.getSpeciality());
				row.createCell(3).setCellValue(errorReport.getCityName());
				row.createCell(4).setCellValue(errorReport.getErrorName());
				row.createCell(5).setCellValue(errorReport.getDeviceName());
				row.createCell(6).setCellValue(errorReport.getUserEmail());
				row.createCell(7).setCellValue(errorReport.getUserMobile());
				row.createCell(8).setCellValue(errorReport.getLoggedTime());
			}
			for (int j = 0; j < ConsultOnlineReport.columns.length; ++j) {
				sheet.autoSizeColumn(j);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workbook;
	}

	public static String updateTime(String time) {
		String newTime = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d = df.parse(time.replace(".0", ""));
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.add(10, 5);
			cal.add(12, 30);
			newTime = df.format(cal.getTime());
		} catch (Exception e) {
			e.getMessage();
		}
		return newTime;
	}

	public static String DayBeforeCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		date.setTime(date.getTime() - 172800000L);
		return dateFormat.format(date);
	}

	public static String getData(String fileName) {
		String base64 = null;
		try {
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			byte[] bytes = new byte[(int) file.length()];
			fis.read(bytes);
			base64 = new BASE64Encoder().encode(bytes);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return base64;
	}

	public static String getPepipost() {
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
			JSONObject attachJson = new JSONObject();
			attachJson.put("fileName", "ConsultOnlineReport.xlsx");
			attachJson.put("fileContent", getData("ConsultOnlineReport.xlsx"));
			JSONArray attachArray = new JSONArray();
			attachArray.put(attachJson);
			json.put("attachments", attachArray);
			innArry.put(
					"drneetal_v@apollohospitals.com,chaitanya_a@apollohospitals.com,anurag_v@apollohospitals.com,vinod_s@apollohospitals.com,ravinder_kp@apollohospitals.com");
			innjson.put("recipient_cc", innArry);
			innjson.put("recipient", "viplav.kirthi@healthnet-global.com");
			innOutArry.put(innjson);
			json.put("personalizations", innOutArry);
			fromJson.put("fromEmail", "info@info.askapollo.com");
			fromJson.put("fromName", "AskApollo");
			json.put("from", fromJson);
			json.put("subject", "Consult Online Daily Report");
			json.put("content", "Hi,\nToday Consult Online No Slot Report");
			HttpEntity<String> entity = new HttpEntity<String>(json.toString(), http);
			ResponseEntity<String> response = template.postForEntity("https://api.pepipost.com/v2/sendEmail", entity,
					String.class, new Object[0]);
			JSONObject result = new JSONObject(response.getBody());
			resp = result.getString("message");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}
}

class ErrorReport {
	private String HospitalName;
	private String DoctorName;
	private String Speciality;
	private String CityName;
	private String ErrorName;
	private String DeviceName;
	private String UserEmail;
	private String UserMobile;
	private String LoggedTime;

	public String getHospitalName() {
		return this.HospitalName;
	}

	public void setHospitalName(final String hospitalName) {
		this.HospitalName = hospitalName;
	}

	public String getDoctorName() {
		return this.DoctorName;
	}

	public void setDoctorName(final String doctorName) {
		this.DoctorName = doctorName;
	}

	public String getSpeciality() {
		return this.Speciality;
	}

	public void setSpeciality(final String speciality) {
		this.Speciality = speciality;
	}

	public String getCityName() {
		return this.CityName;
	}

	public void setCityName(final String cityName) {
		this.CityName = cityName;
	}

	public String getErrorName() {
		return this.ErrorName;
	}

	public void setErrorName(final String errorName) {
		this.ErrorName = errorName;
	}

	public String getDeviceName() {
		return this.DeviceName;
	}

	public void setDeviceName(final String deviceName) {
		this.DeviceName = deviceName;
	}

	public String getUserEmail() {
		return this.UserEmail;
	}

	public void setUserEmail(final String userEmail) {
		this.UserEmail = userEmail;
	}

	public String getUserMobile() {
		return this.UserMobile;
	}

	public void setUserMobile(final String userMobile) {
		this.UserMobile = userMobile;
	}

	public String getLoggedTime() {
		return this.LoggedTime;
	}

	public void setLoggedTime(final String loggedTime) {
		this.LoggedTime = loggedTime;
	}

	public ErrorReport(final String hospitalName, final String doctorName, final String speciality,
			final String cityName, final String errorName, final String deviceName, final String userEmail,
			final String userMobile, final String loggedTime) {
		this.HospitalName = hospitalName;
		this.DoctorName = doctorName;
		this.Speciality = speciality;
		this.CityName = cityName;
		this.ErrorName = errorName;
		this.DeviceName = deviceName;
		this.UserEmail = userEmail;
		this.UserMobile = userMobile;
		this.LoggedTime = loggedTime;
	}
}

class HospitalNameComparator implements Comparator<ErrorReport> {
	@Override
	public int compare(final ErrorReport report1, final ErrorReport report2) {
		return report1.getHospitalName().compareTo(report2.getHospitalName());
	}
}
