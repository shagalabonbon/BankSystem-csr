package com.example.demo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.dto.AccountDto;
import com.example.demo.model.entity.Account;
import com.example.demo.service.AccountService;

@SpringBootTest
public class TestAccountService {

	@Autowired
	private AccountService accountService;

	@Test
	public void test() {

		List<AccountDto> accountDtos = accountService.findAllUserAccounts(4L);

		System.out.print(accountDtos);

	}
}
