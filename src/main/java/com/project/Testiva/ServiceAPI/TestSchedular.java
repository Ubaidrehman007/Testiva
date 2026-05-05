package com.project.Testiva.ServiceAPI;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.project.Testiva.Model.StudentInfo;
import com.project.Testiva.Model.StudentInfo.UserRole;
import com.project.Testiva.Model.TestInfo;
import com.project.Testiva.Model.TestInfo.TestStatus;
import com.project.Testiva.Repository.StudentInfoRepo;
import com.project.Testiva.Repository.TestInfoRepo;

@Component
public class TestSchedular {

	@Autowired
	private TestInfoRepo testInfoRepo;

	@Autowired
	private StudentInfoRepo studentInfoRepo;

	@Autowired
	private SendWhatsAppMessageAPI sendWhatsAppMessageAPI;

	@Scheduled(fixedRate = 30000) // runs every 30 seconds
	public void manageScheduleTest() {

		ZonedDateTime zoneTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
		LocalDateTime currentTime = zoneTime.toLocalDateTime();
		List<TestInfo> testList = testInfoRepo.findAll();

		for (TestInfo test : testList) {

			LocalDateTime startTime = test.getStartTime();
			LocalDateTime endTime = test.endtime();

			// ── CASE 1 ────────────────────────────────────────────────────────────
			// Send WhatsApp reminder 15-20 min before test starts
			// Window is 20 min wide so scheduler never misses it
			// ─────────────────────────────────────────────────────────────────────
			if (test.getStatus().equals(TestStatus.Scheduled)
					&& currentTime.isAfter(startTime.minusMinutes(20))  // opens 20 min before
					&& currentTime.isBefore(startTime)) {               // closes at start time

				// ✅ Only fetch students matching this test's course + branch + year
				List<StudentInfo> stdList = studentInfoRepo.findAllByRoleAndCourseAndBranchAndYear(
						UserRole.STUDENT,
						test.getCourse(),
						test.getBranch(),
						test.getYear()
				);

				System.out.println("⏰ Sending reminders for: " + test.getTestName()
						+ " | Course: " + test.getCourse()
						+ " | Branch: " + test.getBranch()
						+ " | Year: " + test.getYear()
						+ " | Students: " + stdList.size());

				for (StudentInfo student : stdList) {
					sendWhatsAppMessageAPI.sendTestReminderMessage(student, test);
				}

				test.setStatus(TestStatus.Reminder_Sent);
				testInfoRepo.save(test);
			}

			// ── CASE 2 ────────────────────────────────────────────────────────────
			// Safety net: if reminder window was MISSED (server was down etc.),
			// skip Reminder_Sent and go directly Active so test still works
			// ─────────────────────────────────────────────────────────────────────
			if (test.getStatus().equals(TestStatus.Scheduled)
					&& currentTime.isAfter(startTime)) {
				System.err.println("⚠️ Reminder missed for: " + test.getTestName()
						+ " — activating directly.");
				test.setStatus(TestStatus.Active);
				testInfoRepo.save(test);
			}

			// ── CASE 3 ────────────────────────────────────────────────────────────
			// Reminder was sent → now activate the test
			// ─────────────────────────────────────────────────────────────────────
			if (test.getStatus().equals(TestStatus.Reminder_Sent)
					&& currentTime.isAfter(startTime)) {
				test.setStatus(TestStatus.Active);
				testInfoRepo.save(test);
				System.out.println("🟢 Test Active: " + test.getTestName());
			}

			// ── CASE 4 ────────────────────────────────────────────────────────────
			// Test duration over → mark as Test_Over
			// ─────────────────────────────────────────────────────────────────────
			if (test.getStatus().equals(TestStatus.Active)
					&& currentTime.isAfter(endTime)) {
				test.setStatus(TestStatus.Test_Over);
				testInfoRepo.save(test);
				System.out.println("🔴 Test ended: " + test.getTestName());
			}
		}
	}
}