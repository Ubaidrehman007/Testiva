package com.project.Testiva.ServiceAPI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.project.Testiva.Model.StudentInfo;
import com.project.Testiva.Model.TestInfo;

@Service
public class SendWhatsAppMessageAPI {

	private final String INSTANCE_ID = "instance128849";
	private final String TOKEN = "t0dtwn8ud0lm4cif";
	private final String URL = "https://api.ultramsg.com/" + INSTANCE_ID + "/messages/chat";

	// ✅ Auto adds +91 — UltraMsg requires international format e.g. +917021335079
	private String formatNumber(String contactno) {
		if (contactno == null || contactno.isBlank()) return contactno;
		String cleaned = contactno.trim().replaceAll("[\\s\\-()]", "");
		if (cleaned.startsWith("+")) return cleaned;                          // already +91xxxxxxxxxx
		if (cleaned.startsWith("91") && cleaned.length() == 12) return "+" + cleaned; // 91xxxxxxxxxx
		return "+91" + cleaned;                                               // plain 10-digit
	}

	// ─── MESSAGE 1 : 15-min reminder with login credentials ───────────────────
	public String sendTestReminderMessage(StudentInfo studentInfo, TestInfo testInfo) {

		String message = "📢 *Test Reminder*\n\n"
				+ "Dear " + studentInfo.getName() + ",\n\n"
				+ "📝 Your Test *\"" + testInfo.getTestName() + "\"*\n"
				+ " (Test ID: *" + testInfo.getTestId() + "*)\n"
				+ "For Course: *" + testInfo.getCourse() + "*,\n"
				+ "Branch: *" + testInfo.getBranch() + "*, \n"
				+ "Year: *" + testInfo.getYear() + "*\n\n"
				+ "is scheduled to begin in *15 minutes*.\n\n"
				+ "🕒 Start Time: *" + testInfo.getStartTime().toLocalTime() + "*\n"
				+ "📅 Date: *" + testInfo.getStartTime().toLocalDate() + "*\n"
				+ "❓ Total Questions: *" + testInfo.getNumberOfQuestion() + "*\n\n"
				+ "⏲️ Test Active Time: *" + testInfo.getTestDuration() + "*\n\n"
				+ "*Important 🔔*\n"
				+ "Login through your credentials and give your test within the Active time.\n"
				+ "Test ID: *" + testInfo.getTestId() + "*\n\n"
				+ "*Login Credentials:*\n"                          // ✅ fixed \n after colon
				+ "User Id : " + studentInfo.getEmail() + "\n"
				+ "Password : " + studentInfo.getPassword() + "\n\n"
				+ "✅ Please be ready and ensure a stable internet connection.\n"
				+ "Good luck! 🍀\n\n"
				+ "-- Team Testiva 🧑‍💻⚙️";

		return sendMessage(formatNumber(studentInfo.getContactno()), message);
	}

	// ─── MESSAGE 2 : Sent when admin schedules the test ───────────────────────
	public String sendTestScheduledMessage(StudentInfo studentInfo, TestInfo testInfo) {

		String message = "📢 *Test Notification – Testiva Portal*\n\n"
				+ "Hello *" + studentInfo.getName() + "*, 👋\n\n"
				+ "🎓 Course: *" + testInfo.getCourse() + "*\n"
				+ "🏫 Branch: *" + testInfo.getBranch() + "*\n\n"
				+ "📝 Your upcoming test *\"" + testInfo.getTestName() + "\"*"
				+ " (ID: *" + testInfo.getTestId() + "*)\n"
				+ "📅 *Date:* " + testInfo.getStartTime().toLocalDate() + "\n"
				+ "🕒 *Start Time:* " + testInfo.getStartTime().toLocalTime() + "\n\n"
				+ "🛠️ Please login on time using your credentials and Test ID.\n"
				+ "🔗 *Login Portal:* http://10.0.0.125:8484/StudentLogin\n\n"
				+ "🎯 Stay focused and give it your best!\n"
				+ "🍀 *Good luck,*\n-- Team Testiva 🧑‍💻⚙️";

		return sendMessage(formatNumber(studentInfo.getContactno()), message);
	}

	// ─── Common API caller ─────────────────────────────────────────────────────
	private String sendMessage(String formattedNumber, String message) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("token", TOKEN);
			map.add("to", formattedNumber);
			map.add("body", message);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(URL, request, String.class);

			System.out.println("✅ WhatsApp sent to: " + formattedNumber);
			System.out.println("   Response: " + responseEntity.getBody());
			return responseEntity.getBody();

		} catch (Exception e) {
			System.err.println("❌ WhatsApp FAILED for: " + formattedNumber + " | " + e.getMessage());
			return "FAILED: " + e.getMessage();
		}
	}
}