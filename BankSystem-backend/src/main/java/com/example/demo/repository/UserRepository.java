package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.User;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {    
	
	Optional<User> findByIdNumber(String idNumber);
	
	Optional<User> findByEmail(String email);
	
	List<User> findByApprove(String approve);
	
}
