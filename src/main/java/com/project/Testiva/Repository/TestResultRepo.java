package com.project.Testiva.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Testiva.Model.TestResult;

public interface TestResultRepo extends JpaRepository<TestResult, Long>{

	boolean existsByEmailAndTestId(String email, String testId);

	List<TestResult> findAllByEmail(String email);

	Optional<TestResult> findTopByEmailOrderBySubmittedAtDesc(String email);

	List<TestResult> findTop5ByTestIdOrderByTotalScoreDesc(String testId);

	

}
