package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.Branch;
import java.util.Optional;


public interface BranchRepository extends JpaRepository<Branch,Long> {
	
	Optional<Branch> findByCode(String branchCode);
}
