package com.project.Testiva.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Testiva.Model.ContactEnquiry;

public interface ContactEnquiryRepo extends JpaRepository<ContactEnquiry, Long> {

	List<ContactEnquiry> findTop5ByOrderByEnquirydateDesc();

	

	

	

	

}
