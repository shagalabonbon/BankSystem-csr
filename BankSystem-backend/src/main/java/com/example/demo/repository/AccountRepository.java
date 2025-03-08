package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.example.demo.model.entity.Branch;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.Currency;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	
	long countByBranch(Branch branch);
	
	List<Account> findAllAccountsByUser(User user);
	
	List<Account> findAllAccountByUserAndCurrency(User user,Currency currency); // 

	Optional<Account> findByAccountNumber(String accountNumber);
	
	Optional<Account> findByUserIdAndId(Long userId,Long accountId);
	
	Optional<Account> findByUserIdAndCurrency_Code(Long userId,String currencyCode);
	
	// JPA 可用 _ 代表屬性
	
}
