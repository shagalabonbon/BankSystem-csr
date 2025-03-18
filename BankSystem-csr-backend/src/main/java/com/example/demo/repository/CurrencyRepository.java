package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency,Long> {
	
	Optional<Currency> findByCode(String code);
	
}
