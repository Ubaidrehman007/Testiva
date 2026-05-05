package com.project.Testiva.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Testiva.Model.TestInfo;

public interface TestInfoRepo extends JpaRepository<TestInfo, Long> {

	Optional<TestInfo> findTopByOrderByIdDesc();

	TestInfo findByTestId(String testId);

	TestInfo findTopByOrderByTestIdDesc();

}
