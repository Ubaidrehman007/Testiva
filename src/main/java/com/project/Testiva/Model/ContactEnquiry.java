package com.project.Testiva.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class ContactEnquiry {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60, nullable = false)
    private String name;

    @Column(length = 6)
    private String gender;

    @Column(length = 10)
    private String contactno;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 500)
    private String address;

    @Column(length = 1000)
    private String enquirytext;

    private LocalDateTime enquirydate;

	
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getContactno() {
		return contactno;
	}

	public void setContactno(String contactno) {
		this.contactno = contactno;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEnquirytext() {
		return enquirytext;
	}

	public void setEnquirytext(String enquirytext) {
		this.enquirytext = enquirytext;
	}

	public LocalDateTime getEnquirydate() {
		return enquirydate;
	}

	public void setEnquirydate(LocalDateTime enquirydate) {
		this.enquirydate = enquirydate;
	}

	
}







//Enquiry Table(Model)
// id long, name l=60, gender =6, contactno l=10, email l=100, address l=500, enquirytext lenght l1000 enquirydate datatype(LocalDateTime), 