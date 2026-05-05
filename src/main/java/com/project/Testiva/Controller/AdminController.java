package com.project.Testiva.Controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.Testiva.Model.ContactEnquiry;
import com.project.Testiva.Model.QuestionBank;
import com.project.Testiva.Model.StudentInfo;
import com.project.Testiva.Model.StudentInfo.UserRole;
import com.project.Testiva.Model.StudentInfo.UserStatus;
import com.project.Testiva.Model.TestInfo.TestStatus;
import com.project.Testiva.Model.TestResult;
import com.project.Testiva.Model.TestInfo;
import com.project.Testiva.Repository.ContactEnquiryRepo;
import com.project.Testiva.Repository.QuestionBankRepo;
import com.project.Testiva.Repository.StudentInfoRepo;
import com.project.Testiva.Repository.TestInfoRepo;
import com.project.Testiva.Repository.TestResultRepo;
import com.project.Testiva.ServiceAPI.SendAutoEmail;
import com.project.Testiva.ServiceAPI.SendWhatsAppMessageAPI;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Admin")
public class AdminController {

	@Autowired
	private HttpSession session;
	@Autowired
	private StudentInfoRepo userRepo;
	@Autowired
	private QuestionBankRepo qbRepo;
	@Autowired
	private TestInfoRepo testInfoRepo;
	@Autowired
	private ContactEnquiryRepo contactEnquiryRepo;
	@Autowired
	private TestResultRepo testResultRepo;

	@Autowired
	private SendAutoEmail autoEmail;

	@Autowired
	private SendWhatsAppMessageAPI whatsAppMessageAPI;

	@Autowired
	private QuestionBankRepo questionBankRepo;

	@Autowired
	private StudentInfoRepo studentInfoRepo;

	@GetMapping("/Dashboard")
	public String ShowDashboard(Model model) {

		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/AdminLogin";
		}

		long totalStudents = userRepo.count() - 1;
		long totalTests = testInfoRepo.count();
		long totalResults = testResultRepo.count();
		long totalEnquiries = contactEnquiryRepo.count();

		TestInfo testInfo = testInfoRepo.findTopByOrderByTestIdDesc();
		List<TestResult> recentToppers = testResultRepo.findTop5ByTestIdOrderByTotalScoreDesc(testInfo.getTestId());
		List<ContactEnquiry> enquiries = contactEnquiryRepo.findTop5ByOrderByEnquirydateDesc();
		List<ContactEnquiry> notificationenquiry = contactEnquiryRepo.findTop5ByOrderByEnquirydateDesc();

		model.addAttribute("totalStudents", totalStudents);
		model.addAttribute("totalTests", totalTests);
		model.addAttribute("totalResults", totalResults);
		model.addAttribute("totalEnquiries", totalEnquiries);
		model.addAttribute("recentToppers", recentToppers);
		model.addAttribute("enquiries", enquiries);
		model.addAttribute("notificationenquiry", notificationenquiry);

		return "Admin/Dashboard";
	}

	@GetMapping("/UpdateProfilePic")
	public String ShowUpdateProfilePic() {

		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/AdminLogin";
		}

		return "Admin/UpdateProfilePic";
	}

	@PostMapping("/UpdateProfilePic")
	public String EditProfilePic(@RequestParam("profilePic") MultipartFile file, RedirectAttributes attributes) {
		try {

			StudentInfo studentInfo = (StudentInfo) session.getAttribute("loggedInAdmin");

			String storageFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
			String uploadDir = "Public/profilePic/";

			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}

			studentInfo.setProfilepic(storageFileName);
			studentInfoRepo.save(studentInfo);

			System.err.println("Uploaded Successfully.");
			return "redirect:/Admin/UpdateProfilePic";
		} catch (Exception e) {

			System.err.println("Error : " + e.getMessage());
			return "redirect:/Admin/UpdateProfilePic";
		}
	}

	@GetMapping("/ManageStudents")
	public String ShowManageStudents(Model model) {

		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/AdminLogin";
		}

		List<StudentInfo> studentList = userRepo.findAllByRole(UserRole.STUDENT);
		model.addAttribute("studentList", studentList);
		return "Admin/ManageStudents";
	}

	@GetMapping("/ManageStudentEdit")
	public String ShowManageStudentEdit(@RequestParam("id") Long id, Model model) {
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/AdminLogin";
		}

		StudentInfo studentInfo = userRepo.findById(id).get();
		model.addAttribute("student", studentInfo);

		return "Admin/ManageStudentEdit";
	}

	@PostMapping("/ManageStudentEdit")
	public String ManageStudentEdit(RedirectAttributes attributes,
			@ModelAttribute("studentInfo") StudentInfo newstudentInfo, @RequestParam("id") long id) {
		try {

			StudentInfo oldstudentInfo = userRepo.findById(id).get();
			oldstudentInfo.setName(newstudentInfo.getName());
			oldstudentInfo.setEmail(newstudentInfo.getEmail());
			oldstudentInfo.setContactno(newstudentInfo.getContactno());
			oldstudentInfo.setDob(newstudentInfo.getDob());
			oldstudentInfo.setCourse(newstudentInfo.getCourse());
			oldstudentInfo.setBranch(newstudentInfo.getBranch());
			oldstudentInfo.setYear(newstudentInfo.getYear());

			userRepo.save(oldstudentInfo);

			System.err.println("Student : " + newstudentInfo.getName() + " data is updated successfully ");

			attributes.addFlashAttribute("msg", "Student Data Changed Successfully ");

			return "redirect:/Admin/ManageStudents";
		} catch (Exception e) {

			attributes.addFlashAttribute("msg", "Error :" + e.getMessage());

			return "redirect:/Admin/ManageStudents";
		}

	}

	@GetMapping("/ViewEnquiries")
	public String ShowViewEnquiry(Model model) {

		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/AdminLogin";
		}

		ContactEnquiry contactEnquiry = new ContactEnquiry();
		model.addAttribute("contactEnquiry", contactEnquiry);

		List<ContactEnquiry> enquiryList = contactEnquiryRepo.findAll();
		model.addAttribute("enquiryList", enquiryList);
		return "Admin/ViewEnquiries";
	}

	@GetMapping("/SeeResult")
	public String ShowSeeResult(Model model) {

		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/AdminLogin";
		}

		List<TestResult> testResults = testResultRepo.findAll();
		model.addAttribute("testResults", testResults);
		return "Admin/SeeResult";
	}

	@GetMapping("/UpdateStatus")
	public String UpdateStudentStatus(@RequestParam("id") long stdid) {
		try {
			StudentInfo studentInfo = userRepo.findById(stdid).get();
			if (studentInfo.getStatus().equals(UserStatus.PENDING)) {
				studentInfo.setStatus(UserStatus.VERIFIED);
				userRepo.save(studentInfo);
				autoEmail.RegistrationApprovalEMail(studentInfo);
			} else if (studentInfo.getStatus().equals(UserStatus.VERIFIED)) {
				studentInfo.setStatus(UserStatus.DISABLED);
				userRepo.save(studentInfo);

			} else if (studentInfo.getStatus().equals(UserStatus.DISABLED))

			{
				studentInfo.setStatus(UserStatus.VERIFIED);
				userRepo.save(studentInfo);
			}
			System.err.println("Status Updated");

			return "redirect:/Admin/ManageStudents";

		} catch (Exception e) {
			System.err.println("Error :" + e.getMessage());
			return "redirect:/Admin/ManageStudents";
		}
	}

	@GetMapping("/AddQuestion")
	public String ShowAddQuestion(Model model) {
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/AdminLogin";

		}

		QuestionBank questionBank = new QuestionBank();
		model.addAttribute("questionBank", questionBank);
		return "Admin/AddQuestion";

	}

	@PostMapping("/UploadQuestion")
	public String UploadCSVQuestion(@RequestParam("questionFile") MultipartFile csvFile,
			@ModelAttribute("questionBank") QuestionBank questionBank, RedirectAttributes attributes) {
		try {
			if (csvFile.isEmpty()) {
				attributes.addFlashAttribute("msg", "Empty File ❌");
				return "redirect:/Admin/AddQuestion";
			}

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {

				String line;
				reader.readLine(); // remove header line
				List<QuestionBank> qbList = new ArrayList<>();

				while ((line = reader.readLine()) != null) {
					String qbData[] = line.split(",");

					if (qbData.length == 6) {

						QuestionBank qb = new QuestionBank();

						qb.setQuestion(qbData[0]);
						qb.setA(qbData[1]);
						qb.setB(qbData[2]);
						qb.setC(qbData[3]);
						qb.setD(qbData[4]);
						qb.setCorrect(qbData[5]);

						qb.setCourse(questionBank.getCourse());
						qb.setBranch(questionBank.getBranch());
						qb.setYear(questionBank.getYear());

						qbList.add(qb);

					}

				}
				qbRepo.saveAll(qbList);

				attributes.addFlashAttribute("msg", "Question Successfully Uploaded");

				return "redirect:/Admin/AddQuestion";

			}
		} catch (Exception e) {

			attributes.addFlashAttribute("msg", "Error :" + e.getMessage());
			return "redirect:/Admin/AddQuestion";
		}
	}

	@GetMapping("/ManageQuestionBank")
	public String ShowManageQuestion(Model model) {

		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/AdminLogin";
		}

		QuestionBank questionBank = new QuestionBank();
		model.addAttribute("questionBank", questionBank);

		List<QuestionBank> questionList = questionBankRepo.findAll();
		model.addAttribute("questionList", questionList);

		return "Admin/ManageQuestionBank";
	}

	@GetMapping("/ScheduleTest")
	public String ShowScheduleTest(Model model) {

		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/AdminLogin";
		}

		TestInfo testInfo = new TestInfo();
		model.addAttribute("testInfo", testInfo);

		List<TestInfo> testList = testInfoRepo.findAll().reversed();
		model.addAttribute("testList", testList);
		return "Admin/ScheduleTest";

	}

	@PostMapping("ScheduleTest")
	public String ScheduleTest(@ModelAttribute("testInfo") TestInfo testInfo, RedirectAttributes attributes) {
		try {

			long maxId = testInfoRepo.findTopByOrderByIdDesc().map(TestInfo::getId).orElse(0L);

			String generatedTestId = "TTP" + String.format("%03d", maxId + 1);
			testInfo.setTestId(generatedTestId);
			testInfo.setStatus(TestStatus.Scheduled);

			testInfoRepo.save(testInfo);
			attributes.addFlashAttribute("msg", "Test Successfully Scheduled");

			 List<StudentInfo> stdList = userRepo.findAllByRoleAndCourseAndBranchAndYear(
			            UserRole.STUDENT,
			            testInfo.getCourse(),   // must match exactly what's stored in DB
			            testInfo.getBranch(),
			            testInfo.getYear()
			        );

			for (StudentInfo student : stdList) {
				whatsAppMessageAPI.sendTestScheduledMessage(student, testInfo);
			}
			attributes.addFlashAttribute("msg", "Test Scheduled Successfully");
			return "redirect:/Admin/ScheduleTest";
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", "Error : " + e.getMessage());

			return "redirect:/Admin/ScheduleTest";
		}
	}

	@GetMapping("/EditTest")
	public String ShowEditTest(@RequestParam("id") Long id, Model model) {
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/AdminLogin";
		}
		TestInfo testInfo = testInfoRepo.findById(id).get();
		model.addAttribute("testInfo", testInfo);
		return "Admin/EditTest";
	}

	@PostMapping("/EditTest")
	public String EditTest(RedirectAttributes attributes, @ModelAttribute("testInfo") TestInfo newtestInfo,
			@RequestParam("id") long id) {
		try {

			TestInfo oldtestInfo = testInfoRepo.findById(id).get();
			oldtestInfo.setTestDuration(newtestInfo.getTestDuration());
			oldtestInfo.setStartTime(newtestInfo.getStartTime());
			oldtestInfo.setStatus(newtestInfo.getStatus());

			testInfoRepo.save(newtestInfo);

			System.err.println("Student :" + newtestInfo.getTestName() + "data is updated Successfully");
			attributes.addFlashAttribute("msg", "Test data Edited Successfully ");

			return "redirect:/Admin/ScheduleTest";
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", "Error :" + e.getMessage());
			return "redirect:/Admin/ScheduleTest";
		}

	}

	@GetMapping("/ChangePassword")
	public String ShowChangePassword() {

		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/AdminLogin";
		}

		return "Admin/ChangePassword";
	}

	@PostMapping("/ChangePassword")
	public String ChangePassword(HttpServletRequest request, RedirectAttributes attributes) {

		try {

			String currentPassword = request.getParameter("currentPassword");
			String newPassword = request.getParameter("newPassword");
			String confirmPassword = request.getParameter("confirmPassword");

			StudentInfo studentInfo = (StudentInfo) session.getAttribute("loggedInAdmin");

			if (!newPassword.equals(confirmPassword)) {

				attributes.addFlashAttribute("msg", "New Password and Confirm Password are not Same ❌.");
				return "redirect:/Admin/ChangePassword";
			}

			if (currentPassword.equals(studentInfo.getPassword())) {
				// change password logic
				studentInfo.setPassword(confirmPassword);
				studentInfoRepo.save(studentInfo);
				session.getAttribute("loggedInStudent");
				attributes.addFlashAttribute("msg", "Password Change Successfully ✅");
				return "redirect:/AdminLogin";

			} else {

				attributes.addFlashAttribute("msg", "Invalid Current Password ❌");
				return "redirect:/Admin/ChangePassword";
			}

		} catch (Exception e) {
			attributes.addFlashAttribute("msg", "Error :" + e.getMessage());
			return "redirect:/Admin/ChangePassword";
		}

	}

	@GetMapping("/DeleteStudent")
	public String DeleteStudent(@RequestParam("id") long id, RedirectAttributes attributes) {

		userRepo.deleteById(id);
		attributes.addFlashAttribute("msg", "Student Data Deleted Successfully");
		return "redirect:ManageStudents";
	}

	@GetMapping("/DeleteQuestion")
	public String DeleteQuestion(@RequestParam("id") long id, RedirectAttributes attributes) {

		qbRepo.deleteById(id);
		attributes.addFlashAttribute("msg", "Question Data Deleted Successfully");
		return "redirect:ManageQuestionBank";
	}

	@GetMapping("/DeleteEnquiry")
	public String DeleteEnquiry(@RequestParam("id") long id, RedirectAttributes attributes) {

		contactEnquiryRepo.deleteById(id);
		attributes.addFlashAttribute("msg", "Enquiry Data Deleted Successfully ✅");
		return "redirect:ViewEnquiries";
	}

	@GetMapping("/DeleteTest")
	public String DeleteTest(@RequestParam("id") long id, RedirectAttributes attributes) {

		testInfoRepo.deleteById(id);
		attributes.addFlashAttribute("msg", "Test Data Deleted Successfully");
		return "redirect:ScheduleTest";
	}

	@GetMapping("/Logout")
	public String Logout() {
		session.removeAttribute("loggedInAdmin");
		return "redirect:/AdminLogin";
	}
}
