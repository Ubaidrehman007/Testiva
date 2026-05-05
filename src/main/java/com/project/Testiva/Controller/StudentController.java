package com.project.Testiva.Controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.project.Testiva.Model.QuestionBank;
import com.project.Testiva.Model.StudentInfo;
import com.project.Testiva.Model.TestInfo;
import com.project.Testiva.Model.TestInfo.TestStatus;
import com.project.Testiva.Model.TestResult;
import com.project.Testiva.Repository.QuestionBankRepo;
import com.project.Testiva.Repository.StudentInfoRepo;
import com.project.Testiva.Repository.TestInfoRepo;
import com.project.Testiva.Repository.TestResultRepo;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Student")
public class StudentController {

	@Autowired
	private HttpSession session;

	@Autowired
	private TestInfoRepo testInfoRepo;

	@Autowired
	private QuestionBankRepo questionBankRepo;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private TestResultRepo testResultRepo;

	@Autowired
	private StudentInfoRepo studentInfoRepo;

	@GetMapping("/Dashboard")
	public String ShowSashboard(Model model) {

		if (session.getAttribute("loggedInStudent") == null) {

			return "redirect:/StudentLogin";
		}

		StudentInfo student = (StudentInfo) session.getAttribute("loggedInStudent");
		List<TestResult> testResults = testResultRepo.findAllByEmail(student.getEmail());

		model.addAttribute("student", student); // StudentInfo object
		model.addAttribute("testCount", testResults.size()); // Total test count

		Optional<TestResult> latestResultOpt = testResultRepo.findTopByEmailOrderBySubmittedAtDesc(student.getEmail());
		latestResultOpt.ifPresent(result -> model.addAttribute("latestResult", result));
		return "Student/Dashboard";
	}

	@GetMapping("/UpdateProfilePic")
	public String ShowUpdateProfilePic() {

		if (session.getAttribute("loggedInStudent") == null) {
			return "redirect:/StudentLogin";

		}
		return "Student/UpdateProfilePic";
	}
	
	@PostMapping("/UpdateProfilePic")
	public String EditProfilePic(@RequestParam("profilePic") MultipartFile file, RedirectAttributes attributes)
	{
		try {
			
			StudentInfo studentInfo = (StudentInfo) session.getAttribute("loggedInStudent");
			
			String storageFileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
			String uploadDir = "Public/profilePic/";
			
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			
			try(InputStream inputStream = file.getInputStream())
			{
				Files.copy(inputStream, Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
			
			studentInfo.setProfilepic(storageFileName);
			studentInfoRepo.save(studentInfo);
			
			System.err.println("Uploaded Successfully.");
			return "redirect:/Student/UpdateProfilePic";
		} catch (Exception e) {
			
			System.err.println("Error : "+e.getMessage());
			return "redirect:/Student/UpdateProfilePic";
		}
	}

	@GetMapping("/GiveTest")
	public String ShowGiveTest() {

		if (session.getAttribute("loggedInStudent") == null) {

			return "redirect:/StudentLogin";
		}

		return "Student/GiveTest";
	}

	// Store test id in a temp variable
	private String testId;

	@GetMapping("/StartTest")
	public String StartTest(@RequestParam("testid") String testId, RedirectAttributes attributes, Model model) {

		if (session.getAttribute("loggedInStudent") == null) {
			return "redirect:/StudentLogin";
		}

		try {
			this.testId = testId;
			StudentInfo studentInfo = (StudentInfo) session.getAttribute("loggedInStudent");
			TestInfo testInfo = testInfoRepo.findByTestId(testId);

			if (testResultRepo.existsByEmailAndTestId(studentInfo.getEmail(), testId)) {
				attributes.addFlashAttribute("msg", "You have already given the Test!");
				return "redirect:/Student/GiveTest";
			}

			if (studentInfo.getCourse().equals(testInfo.getCourse())
					&& studentInfo.getBranch().equals(testInfo.getBranch())
					&& studentInfo.getYear().equals(testInfo.getYear())) {
				if (testInfo.getStatus().equals(TestStatus.Active)) {

					// Start Test Logic
					List<QuestionBank> qbList = questionBankRepo.findByCourseAndBranchAndYear(testInfo.getCourse(),
							testInfo.getBranch(), testInfo.getYear(), testInfo.getNumberOfQuestion(), entityManager);
					Gson gson = new Gson();
					String json = gson.toJson(qbList);

					model.addAttribute("json", json);
					model.addAttribute("tt", (double) qbList.size() / 2.0);
					model.addAttribute("tq", qbList.size());
					model.addAttribute("testname", testInfo.getTestName());
					return "Student/StartTest";

				} else if (testInfo.getStatus().equals(TestStatus.Reminder_Sent)) {
					attributes.addFlashAttribute("msg", "please wait test will be start within few minutes");
				} else if (testInfo.getStatus().equals(TestStatus.Scheduled)) {
					attributes.addFlashAttribute("msg", "Test scheduled plese wait");
				} else {
					attributes.addFlashAttribute("msg", "oops, Test got ended");
				}
				return "redirect:/Student/GiveTest";

			} else {

				attributes.addFlashAttribute("msg", "Invalid test Id You can't give test");
				return "redirect:/Student/GiveTest";
			}

		} catch (Exception e) {

			attributes.addFlashAttribute("msg", "No Test Found!");
			System.err.println("Error : " + e.getMessage());
			return "redirect:/Student/GiveTest";
		}
	}

	// Test Over Logic
	@GetMapping("/TestOver")
	public String ShowTestOver() {

		if (session.getAttribute("loggedInStudent") == null) {
			return "redirect:/StudentLogin";
		}
		return "Student/TestOver";
	}

	@PostMapping("/TestOver")
	public String TestOver(@RequestParam("t") int totalMarks, @RequestParam("s") int totalScore) {
		try {
			StudentInfo studentInfo = (StudentInfo) session.getAttribute("loggedInStudent");
			TestInfo testInfo = testInfoRepo.findByTestId(testId);

			TestResult testResult = new TestResult();
			testResult.setName(studentInfo.getName());
			testResult.setContactno(studentInfo.getContactno());
			testResult.setEmail(studentInfo.getEmail());
			testResult.setCourse(studentInfo.getCourse());
			testResult.setBranch(studentInfo.getBranch());
			testResult.setYear(studentInfo.getYear());

			testResult.setTestName(testInfo.getTestName());
			testResult.setTestId(testInfo.getTestId());
			testResult.setTotalMarks(totalMarks);
			testResult.setTotalScore(totalScore);
			testResult.setSubmittedAt(LocalDateTime.now());
			testResultRepo.save(testResult);

			return "redirect:/Student/TestOver";
		} catch (Exception e) {

			return "redirect:/Student/GiveTest";
		}
	}

	@GetMapping("/SeeResult")
	public String ShowSeeResult(Model model) {

		if (session.getAttribute("loggedInStudent") == null) {
			return "redirect:/StudentLogin";
		}

		StudentInfo studentInfo = (StudentInfo) session.getAttribute("loggedInStudent");
		List<TestResult> testResults = testResultRepo.findAllByEmail(studentInfo.getEmail());
		model.addAttribute("testResults", testResults);
		return "Student/SeeResult";
	}

	@GetMapping("/ChangePassword")
	public String ShowChangePassword() {

		if (session.getAttribute("loggedInStudent") == null) {
			return "redirect:/StudentLogin";
		}

		return "Student/ChangePassword";
	}

	@PostMapping("/ChangePassword")
	public String ChangePassword(HttpServletRequest request, RedirectAttributes attributes) {

		try {

			String currentPassword = request.getParameter("currentPassword");
			String newPassword = request.getParameter("newPassword");
			String confirmPassword = request.getParameter("confirmPassword");

			StudentInfo studentInfo = (StudentInfo) session.getAttribute("loggedInStudent");

			if (!newPassword.equals(confirmPassword)) {

				attributes.addFlashAttribute("msg", "New Password and Confirm Password are not Same ❌.");
				return "redirect:/Student/ChangePassword";
			}

			if (currentPassword.equals(studentInfo.getPassword())) {
				// change password logic
				studentInfo.setPassword(confirmPassword);
				studentInfoRepo.save(studentInfo);
				session.getAttribute("loggedInStudent");
				attributes.addFlashAttribute("msg", "Password Change Successfully ✅");
				return "redirect:/StudentLogin";

			} else {

				attributes.addFlashAttribute("msg", "Invalid Current Password ❌");
				return "redirect:/Student/ChangePassword";
			}

		} catch (Exception e) {
			attributes.addFlashAttribute("msg", "Error :" + e.getMessage());
			return "redirect/Student/ChangePassword";
		}

	}

	@GetMapping("/VideoLearningMaterial")
	public String ShowVideoLearningMaterial() {
		if (session.getAttribute("loggedInStudent") == null) {
			return "redirect:/StudentLogin";
		}
		return "Student/VideoLearningMaterial";

	}

	@GetMapping("/PDFMaterial")
	public String ShowPDFMaterial() {
		if (session.getAttribute("loggedInStudent") == null) {
			return "redirect:/StduentLogin";
		}
		return "Student/PDFMaterial";
	}

	@GetMapping("/Logout")
	public String Logout() {
		session.removeAttribute("loggedInStudent");
		return "redirect:/StudentLogin";
	}

}
