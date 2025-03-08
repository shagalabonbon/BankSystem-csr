package com.example.demo.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.accountexception.AccountNotFoundException;
import com.example.demo.exception.accountexception.InsufficientFundsException;
import com.example.demo.exception.branchexception.BranchNotFoundException;
import com.example.demo.exception.currencyexception.CurrencyNotFoundException;
import com.example.demo.exception.userexception.UserNotFoundException;
import com.example.demo.model.dto.AccountDto;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.Branch;
import com.example.demo.model.entity.Currency;
import com.example.demo.model.entity.TransactionRecord;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.AccountStatus;
import com.example.demo.model.enums.AccountType;
import com.example.demo.model.enums.TransactionType;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.BranchRepository;
import com.example.demo.repository.CurrencyRepository;
import com.example.demo.repository.TransactionRecordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AccountService;
import com.example.demo.service.CurrencyService;
import com.example.demo.service.UserService;

import jakarta.websocket.Session;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CurrencyRepository currencyRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private BranchRepository branchRepository;
	
	@Autowired
	private ModelMapper modelMapper; 
	
	@Override
	public String generateAccountNumber(Branch branch,String businessCode) {
		
        // 前 4 碼為分行代碼
        String branchCode = branch.getCode(); 

        // 第 4-5 碼為固定業務代碼
//      String businessCode = "53";

        // 從資料庫獲取當前流水號
        long   serialNumber       = accountRepository.countByBranch(branch) + 1;  // 流水號 +1
        String serialNumberPadded = String.format("%06d", serialNumber);          // 補足 6 碼

        // 組合成 12 碼帳號
        return branchCode + businessCode + serialNumberPadded;
    }
	
	// 創建帳戶 ( 台幣 53、外幣 87 )
	
	@Override
	public void createAccount(User user,String curCode,String branchCode,String businessCode) {
		
		// 審核通過註冊帳號  
		
        Currency currency = currencyRepository.findByCode(curCode).orElseThrow(()->new CurrencyNotFoundException());;
           
        Branch branch = branchRepository.findByCode(branchCode).orElseThrow(()->new BranchNotFoundException());
        
        // 創建台幣帳戶
        
        Account account = new Account();
        
        account.setUser       (user);                    // 用戶
        account.setBalance    (BigDecimal.ZERO);         // 初始餘額： 0
        account.setStatus     (AccountStatus.ACTIVE);
        account.setAccountType(AccountType.SAVINGS);
        account.setCreateTime (new Timestamp(System.currentTimeMillis()));
        account.setCurrency   (currency);                // 預設：台幣
        account.setBranch     (branch);
        
        account.setAccountNumber(generateAccountNumber(branch,businessCode)); 
        
        accountRepository.save(account);
        
	}
	
	
	//-----------------------------------------------------------

	@Override
	public List<Account> findAllUserAccounts(Long userId) {
		
		User user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException()); 
		
		List<Account> account = accountRepository.findAllAccountsByUser(user);
		
		return account;
	}
	
	@Override
	public List<Account> findAllUserTWDAccounts(Long userId) {
		
		User user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException()); 
		
		List<Account> accounts = accountRepository.findAllAccountsByUser(user).stream().filter(a->a.getCurrency().getCode().equals("TWD")).toList();
		
		return accounts;
	}
	
	
	@Override
	public List<Account> findAllUserForeignAccounts(Long userId) {
		
		User user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException()); 
		
		List<Account> accounts = accountRepository.findAllAccountsByUser(user).stream().filter(a->!a.getCurrency().getCode().equals("TWD")).toList();
		
		return accounts;
		
	}

	
	@Override
	public Account getAccountByCurrencyCode(Long userId, String currencyCode) {
		Account account = accountRepository.findByUserIdAndCurrency_Code(userId,currencyCode)
                .orElseThrow(()->new AccountNotFoundException());

		return account;
		
	}

	
	
	//-----------------------------------------------------------
	
	// 計算帳戶總和 ( 台、外幣 )

	@Override
	public BigDecimal calcTotalBalance(UserDto userDto,Currency currency) {
		
		User user = modelMapper.map(userDto, User.class);
		
		BigDecimal accountBalance = accountRepository.findAllAccountByUserAndCurrency(user,currency)
				                                     .stream()
				                                     .map(Account::getBalance)                       // 提取每個帳戶的餘額
				                                     .reduce(BigDecimal.ZERO, BigDecimal::add);      // 累加餘額，初始值為 0 
		
		return accountBalance; 
	}

	

	
	
	
	

	
	
	

	
	
}
