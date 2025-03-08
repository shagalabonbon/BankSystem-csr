package com.example.demo.repository;



import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.TransactionRecord;

@Repository
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord,Long>  {
	
	// 尋照前50筆
	List<TransactionRecord> findTop50ByAccountIdOrderByTransactionTimeDesc(Long accountId);
	
	// 尋找所有
	List<TransactionRecord> findByAccountIdOrderByTransactionTimeDesc(Long accountId);
	
	// 尋找區間 ( startDate 0:00:00 ~ endDate 23:59:59)
	@Query(value = """ 
			SELECT * From transaction_record 
			WHERE account_id= :id 
			AND transaction_time Between :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY) - INTERVAL 1 SECOND 
			ORDER BY transaction_time desc
			""" , nativeQuery = true)
	List<TransactionRecord> findRecordsByChosenTime( @Param("id")        Long accountId, 
			                     	                 @Param("startDate") Date startDate,
			                                         @Param("endDate")   Date endDate);

}
