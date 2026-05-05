package com.project.Testiva.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Testiva.Model.QuestionBank;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public interface QuestionBankRepo extends JpaRepository<QuestionBank, Long> {

	@SuppressWarnings("unchecked")
	default List<QuestionBank> findByCourseAndBranchAndYear(
			String course, 
			String branch,
			String year,
			int numberOfQuestion,
			EntityManager entityManager
			)
	{
		String sqlQuery = "SELECT * FROM question_bank where course = :course AND branch = :branch AND year =:year ORDER BY RAND() LIMIT  "+numberOfQuestion;    
		
		Query query = entityManager.createNativeQuery(sqlQuery,QuestionBank.class);
		query.setParameter("course", course);
		query.setParameter("branch", branch);
		query.setParameter("year", year);
		
		return query.getResultList();
	}
	
}
