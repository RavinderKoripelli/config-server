package com.ApolloCrownJob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class HealthyHeartAutomationReport {

	private static String[] columns = { "Email_Id", "Name", "MobileNumber", "AverageStepCount" };
	private static final String PRODUCTION_URL = "jdbc:mysql://13.71.122.152:3306/LocateApollo";
	private static final String PRODUCTION_USER_NAME = "root";
	private static final String PRODUCTION_PASSWORD = "askApolloApp@123";

	public static void main(String[] args) throws IOException, InvalidFormatException {
		try {
			
			Calendar date = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			Workbook book = getWorkBook();
			FileOutputStream fileOut = new FileOutputStream("HealthyHeartAutomationReport.xlsx");
			book.write(fileOut);
			fileOut.close();
			//book.close();
			//sendOtp("NewExcelFile.xls");
			sendOtp("HealthyHeartAutomationReport.xlsx");
			date.setTime(new Date());
			date.set(Calendar.HOUR, 10);
			System.out.println(dateFormat.format(date.getTime()));
			if ((dateFormat.parse(dateFormat.format(date.getTime())).after(dateFormat.parse("11:00:00")))
			|| (dateFormat.parse(dateFormat.format(date.getTime())).before(dateFormat.parse("22:30:00")))) {
				Thread.sleep(2 * 60 * 1000);
				sendOtp("HealthyHeartAutomationReport.xls");
				
				} else {
					Thread.sleep(1 * 60 * 1000);
				} 		
			/*if ((dateFormat.parse(dateFormat.format(date.getTime())).equals(dateFormat.parse("21:59:00"))))
			{
				Thread.sleep(2000);
				sendOtp("HealthyHeartAutomationReport.xls");
			}*/
			System.out.println("come");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Object[]> listRecords() {
		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<Object[]> objectList = new ArrayList<Object[]>();
		try {
			if (con == null) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(PRODUCTION_URL, PRODUCTION_USER_NAME, PRODUCTION_PASSWORD);

				String getFeedBackListQuery = "{call HH_DAILY_REPORT()}";

				stmt = con.prepareCall(getFeedBackListQuery);
				ResultSet rs = stmt.executeQuery();

				Object[] objectArray = null;

				while (rs.next()) {
					objectArray = new Object[] { rs.getString("Email_Id"), rs.getString("Name"),
							rs.getString("mobileNumber"), rs.getString("AverageSteps") };
					objectList.add(objectArray);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objectList;
	}

	public static String sendOtp(String path) {

		String retval = "";
		String postData = "";
		File path2 = new File(path);
			try {
				postData= "{\"personalizations\":[{\"recipient\":\"ravinder.koripelly@outlook.com\","
						+ "\"recipient_cc\":[\"ravifrnds09@gmail.com\",\"kunchanapallieswar@gmail.com\"]}],"
						+ "\"from\":{\"fromEmail\":\"info@info.askapollo.com\"," + "\"fromName\":\"AskApollo\"},"
						+ "\"subject\":\"HealthyHeart Automation Report\"," + "\"content\":\"Hi Today Automation Report\","
						+ "\"attachments\":[{\"fileContent\":\"HealthyHeart File\",\"fileName\":\"" + path2 + "\"}],"
						+ "\"replyToId\":\"anvesh.gatadi@gmail.com\"}";

				URL url = new URL("https://api.pepipost.com/v2/sendEmail");
				HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
				urlconnection.setRequestMethod("POST");
				urlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				urlconnection.setDoOutput(true);
				urlconnection.setRequestProperty("api_key", "87a840a628bb818014be8a703c414968");
				OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
				out.write(postData);
				out.close();
				BufferedReader in = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
				String decodedString;
				while ((decodedString = in.readLine()) != null) {
					retval += decodedString;
					if (retval.contains("Success")) {
						retval = "{\"mail\":\" sent\",\"status\":\"success\"}";
					} else {
						retval = "{\"mail\":\"not-sent\",\"status\":\"fail\"}";
					}
				}
				in.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		return retval;
	}

	public static Workbook getWorkBook() {
		Workbook workbook = null;
		try {
			List<Object[]> records = listRecords();
			workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("DailyAutomationReport");
			Font headerFont = workbook.createFont();
			//headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 14);
			headerFont.setColor(IndexedColors.BLACK.getIndex());
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerCellStyle);
			}
			int rowNum = 1;
			for (Object[] obj : records) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(obj[0].toString());
				row.createCell(1).setCellValue(obj[1].toString());
				row.createCell(2).setCellValue(obj[2].toString());
				row.createCell(3).setCellValue(obj[3].toString());
			}
			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workbook;
	}
}
