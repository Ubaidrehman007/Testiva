package com.project.Testiva.Controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.Testiva.Model.ContactEnquiry;
import com.project.Testiva.Model.StudentInfo;
import com.project.Testiva.Model.StudentInfo.UserRole;
import com.project.Testiva.Model.StudentInfo.UserStatus;
import com.project.Testiva.Repository.ContactEnquiryRepo;
import com.project.Testiva.Repository.StudentInfoRepo;
import com.project.Testiva.ServiceAPI.SendAutoEmail;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

	// Admin email address – notifications will be sent here
	private static final String ADMIN_EMAIL = "urkhan556@gmail.com";

	@Autowired
	private StudentInfoRepo userInfoRepo;

	@Autowired
	private ContactEnquiryRepo contactEnquiryRepo;

	@Autowired
	private SendAutoEmail autoEmail;

	@GetMapping("/")
	public String ShowIndex() {
		return "index";
	}

	@GetMapping("/AboutUs")
	public String ShowAboutUs() {
		return "aboutus";
	}

	@GetMapping("/Services")
	public String ShowServices() {
		return "services";
	}

	@GetMapping("/Registration")
	public String ShowStudentRegistration(Model model) {
		StudentInfo stdinfo = new StudentInfo();
		model.addAttribute("stdinfo", stdinfo);
		return "studentregistration";
	}

	@GetMapping("/StudentLogin")
	public String ShowStudentLogin() {
		return "StudentLogin";
	}

	@PostMapping("/StudentLogin")
	public String StudentLogin(@RequestParam("email") String email, @RequestParam("password") String password,
			RedirectAttributes attributes, HttpSession session) {

		try {

			if (!userInfoRepo.existsByEmail(email)) {
				attributes.addFlashAttribute("msg", "Student dosent Exist");
				return "redirect:/StudentLogin";
			}

			StudentInfo student = userInfoRepo.findByemail(email);
			if (student.getEmail().equals(email) && student.getPassword().equals(password)
					&& student.getRole().equals(UserRole.STUDENT)) {

				if (student.getStatus().equals(UserStatus.VERIFIED)) {
					session.setAttribute("loggedInStudent", student);
					return "redirect:/Student/Dashboard";
				} else if (student.getStatus().equals(UserStatus.PENDING)) {
					attributes.addFlashAttribute("msg", "Status Pending, Please wait for Admin Approval.");
				} else if (student.getStatus().equals(UserStatus.DISABLED)) {
					attributes.addFlashAttribute("msg", "Login Disabled, Please Contact Administration");
				}

			} else {
				attributes.addFlashAttribute("msg", "Invalid User or Wrong Password");
			}

			return "redirect:/StudentLogin";

		} catch (Exception e) {
			System.err.print("Error : " + e.getMessage());
			attributes.addFlashAttribute("msg", "Error :" + e.getMessage());
			return "redirect:/StudentLogin";
		}
	}

	// Admin Login
	@GetMapping("/AdminLogin")
	public String ShowAdminLogin() {
		return "AdminLogin";
	}

	@PostMapping("/AdminLogin")
	public String AdminLogin(@RequestParam("email") String email, @RequestParam("password") String password,
			RedirectAttributes attributes, HttpSession session) {

		try {
			if (!userInfoRepo.existsByEmail(email)) {
				attributes.addFlashAttribute("msg", "User Doesnt Exists ❌");
				return "redirect:/AdminLogin";
			}

			StudentInfo admin = userInfoRepo.findByemail(email);

			if (admin.getPassword().equals(password) && admin.getEmail().equals(email)
					&& admin.getRole().equals(UserRole.ADMIN)) {

				session.setAttribute("loggedInAdmin", admin);
				return "redirect:/Admin/Dashboard";
			} else {
				attributes.addFlashAttribute("msg", "Invalid UserId or Password");
			}

			return "redirect:/AdminLogin";

		} catch (Exception e) {
			return "redirect:/AdminLogin";
		}
	}

	@GetMapping("/ContactUs")
	public String ShowContactUs(Model model) {
		ContactEnquiry contactenquiry = new ContactEnquiry();
		model.addAttribute("contactenquiry", contactenquiry);
		return "contactus";
	}

	@PostMapping("/ContactUs")
	public String submitContactForm(@ModelAttribute("contactenquiry") ContactEnquiry contactEnquiry, Model model) {
		contactEnquiry.setEnquirydate(LocalDateTime.now());
		contactEnquiryRepo.save(contactEnquiry);
		model.addAttribute("msg", "Your enquiry has been submitted successfully! ✅");
		return "contactUs";
	}

	@PostMapping("/Registration")
	public String Registration(@ModelAttribute("stdinfo") StudentInfo newStudent,
			@RequestParam("profilePic") MultipartFile profilePic, RedirectAttributes attributes) {

		try {

			// Profile picture upload
			if (!profilePic.isEmpty() && profilePic != null) {

				String storageFileName = System.currentTimeMillis() + "_" + profilePic.getOriginalFilename();
				String uploadDir = "Public/profilePic/";
				Path uplaodPath = Paths.get(uploadDir);
				if (!Files.exists(uplaodPath)) {
					Files.createDirectories(uplaodPath);
				}

				try (InputStream inputStream = profilePic.getInputStream()) {
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
							StandardCopyOption.REPLACE_EXISTING);
				}
				newStudent.setProfilepic(storageFileName);
			}

			// Set default status/role before saving
			newStudent.setStatus(UserStatus.PENDING);
			newStudent.setRole(UserRole.STUDENT);
			newStudent.setRegdate(LocalDateTime.now());

			userInfoRepo.save(newStudent);

			// ✅ FIX: Notify admin about the new registration
			try {
				autoEmail.NewRegistrationAlertEmail(newStudent, ADMIN_EMAIL);
				System.out.println("Admin notified about new registration: " + newStudent.getEmail());
			} catch (Exception mailEx) {
				// Do not fail the whole registration if email fails; just log it
				System.err.println("Admin notification email failed: " + mailEx.getMessage());
			}

			attributes.addFlashAttribute("msg", "Registration Submitted Successfully ✅");
			return "redirect:/Registration";

		} catch (Exception e) {
			attributes.addFlashAttribute("msg", "Error :" + e.getMessage());
			return "redirect:/Registration";
		}
	}
}