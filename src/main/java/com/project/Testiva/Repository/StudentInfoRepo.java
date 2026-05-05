package com.project.Testiva.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Testiva.Model.StudentInfo;
import com.project.Testiva.Model.StudentInfo.UserRole;

public interface StudentInfoRepo extends JpaRepository<StudentInfo, Long> {

	boolean existsByEmail(String email);

	StudentInfo findByemail(String email);

	

	List<StudentInfo> findAllByRole(UserRole student);

	List<StudentInfo> findAllByRoleAndCourseAndBranchAndYear(UserRole student, String Course, String Branch,
			String Year);

}
