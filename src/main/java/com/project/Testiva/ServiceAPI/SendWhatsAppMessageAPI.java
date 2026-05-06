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

    // Environment Variables
    private final String INSTANCE_ID = System.getenv("ULTRAMSG_INSTANCE_ID");
    private final String TOKEN = System.getenv("ULTRAMSG_TOKEN");

    // UltraMsg API URL
    private final String URL =
            "https://api.ultramsg.com/" + INSTANCE_ID + "/messages/chat";

    // Format mobile number for UltraMsg
    // Example: 919876543210
    private String formatNumber(String contactno) {

        if (contactno == null || contactno.isBlank()) {
            return contactno;
        }

        String cleaned =
                contactno.trim().replaceAll("[\\s\\-()+]", "");

        if (cleaned.startsWith("91")) {
            return cleaned;
        }

        return "91" + cleaned;
    }

    // =========================================================
    // MESSAGE 1 : Test Reminder Message
    // =========================================================

    public String sendTestReminderMessage(
            StudentInfo studentInfo,
            TestInfo testInfo
    ) {

        String message =
                "📢 *Test Reminder*\n\n"
                        + "Dear " + studentInfo.getName() + ",\n\n"

                        + "📝 Your Test *\"" + testInfo.getTestName() + "\"*\n"
                        + "(Test ID: *" + testInfo.getTestId() + "*)\n\n"

                        + "🎓 Course: *" + testInfo.getCourse() + "*\n"
                        + "🏫 Branch: *" + testInfo.getBranch() + "*\n"
                        + "📚 Year: *" + testInfo.getYear() + "*\n\n"

                        + "⏰ Your test will start in *15 minutes*\n\n"

                        + "🕒 Start Time: *"
                        + testInfo.getStartTime().toLocalTime() + "*\n"

                        + "📅 Date: *"
                        + testInfo.getStartTime().toLocalDate() + "*\n\n"

                        + "❓ Total Questions: *"
                        + testInfo.getNumberOfQuestion() + "*\n"

                        + "⏲️ Duration: *"
                        + testInfo.getTestDuration() + "*\n\n"

                        + "🔐 *Login Credentials*\n"
                        + "👤 User ID: "
                        + studentInfo.getEmail() + "\n"

                        + "🔑 Password: "
                        + studentInfo.getPassword() + "\n\n"

                        + "🌐 Login Portal:\n"
                        + "https://testiva.onrender.com/StudentLogin\n\n"

                        + "✅ Please ensure a stable internet connection.\n\n"

                        + "🍀 Good luck!\n\n"

                        + "-- Team Testiva 🧑‍💻⚙️";

        return sendMessage(
                formatNumber(studentInfo.getContactno()),
                message
        );
    }

    // =========================================================
    // MESSAGE 2 : Test Scheduled Message
    // =========================================================

    public String sendTestScheduledMessage(
            StudentInfo studentInfo,
            TestInfo testInfo
    ) {

        String message =
                "📢 *Test Notification – Testiva Portal*\n\n"

                        + "Hello *" + studentInfo.getName() + "* 👋\n\n"

                        + "🎓 Course: *"
                        + testInfo.getCourse() + "*\n"

                        + "🏫 Branch: *"
                        + testInfo.getBranch() + "*\n"

                        + "📚 Year: *"
                        + testInfo.getYear() + "*\n\n"

                        + "📝 Your upcoming test:\n"
                        + "*"
                        + testInfo.getTestName()
                        + "*\n\n"

                        + "🆔 Test ID: *"
                        + testInfo.getTestId() + "*\n\n"

                        + "📅 Date: *"
                        + testInfo.getStartTime().toLocalDate() + "*\n"

                        + "🕒 Start Time: *"
                        + testInfo.getStartTime().toLocalTime() + "*\n\n"

                        + "🌐 Login Portal:\n"
                        + "https://testiva.onrender.com/StudentLogin\n\n"

                        + "⏳ Please login on time and complete the test within the active duration.\n\n"

                        + "🎯 Stay focused and give your best!\n\n"

                        + "🍀 Good luck!\n\n"

                        + "-- Team Testiva 🧑‍💻⚙️";

        return sendMessage(
                formatNumber(studentInfo.getContactno()),
                message
        );
    }

    // =========================================================
    // COMMON SEND MESSAGE METHOD
    // =========================================================

    private String sendMessage(
            String formattedNumber,
            String message
    ) {

        try {

            System.out.println("📤 Sending WhatsApp Message...");
            System.out.println("📱 Number: " + formattedNumber);

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(
                    MediaType.APPLICATION_FORM_URLENCODED
            );

            MultiValueMap<String, String> map =
                    new LinkedMultiValueMap<>();

            map.add("token", TOKEN);
            map.add("to", formattedNumber);
            map.add("body", message);

            HttpEntity<MultiValueMap<String, String>> request =
                    new HttpEntity<>(map, headers);

            ResponseEntity<String> responseEntity =
                    restTemplate.postForEntity(
                            URL,
                            request,
                            String.class
                    );

            System.out.println("✅ WhatsApp Sent Successfully");
            System.out.println("📩 Response: "
                    + responseEntity.getBody());

            return responseEntity.getBody();

        } catch (Exception e) {

            System.err.println("❌ WhatsApp Sending Failed");
            System.err.println("📱 Number: " + formattedNumber);
            System.err.println("⚠️ Error: " + e.getMessage());

            e.printStackTrace();

            return "FAILED : " + e.getMessage();
        }
    }
}