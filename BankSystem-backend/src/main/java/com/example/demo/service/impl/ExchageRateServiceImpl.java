package com.example.demo.service.impl;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.RowSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.ExchangeRate;
import com.example.demo.model.entity.Currency;
import com.example.demo.repository.CurrencyRepository;
import com.example.demo.service.AccountService;
import com.example.demo.service.ExchangeRateService;

@Service
public class ExchageRateServiceImpl implements ExchangeRateService {

	@Autowired
	private CurrencyRepository currencyRepository;
	
	@Autowired
	private AccountService accountService;
	
	@Override
	public List<ExchangeRate> getTwdExchangeRate() {
		
		List<String> targetCurrencies = currencyRepository.findAll().stream().map(cur->cur.getCode()).toList();   // 目標貨幣代碼 ( 資料庫 )
		
		try {
			
			String   url = "https://rate.bot.com.tw/xrt?Lang=zh-TW";              // 訪問台灣銀行的匯率網頁
            
			Document document = Jsoup.connect(url).get();
			      
            Element table = document.select("table[title='牌告匯率']").first();    // 取得匯率表格，並驗證
   
            if( table!=null ) {

            	// 獲取行
            	
            	Elements rows = document.select("tbody tr"); 
            	
            	// 遍歷行，過濾出目標貨幣的行
            
                List<Element> targetCurRows = rows.stream()
	                                           	  .filter( row -> {   
	                                           		  String curName = row.select("td[data-table='幣別'] > div > div.xrt-cur-indent").text(); // 貨幣名稱 ( 美元(USD) )
	                                           		  int    start   = curName.indexOf('(');
	                                           		  int    end     = curName.indexOf(')');
	                                           		  String code    = curName.substring(start + 1, end);
	                       
	                                           		  return targetCurrencies.contains(code);                                                 // return boolean 讓 fliter 辨識是否保留
	                                           	  })
	                                           	  .toList();  
                
                // 遍歷篩選後的行，提取數據
            
	            List<ExchangeRate> exchangeRates = new ArrayList<>();
	        		
	        	for (Element row : targetCurRows) {
	        			
	        		    // 取得貨幣名稱
	        			String curName = row.select("td[data-table='幣別'] > div > div.xrt-cur-indent").text(); 
	        			int    start   = curName.indexOf('(');
	             		int    end     = curName.indexOf(')');
	             		String code    = curName.substring(start + 1, end);
	                    
	                    // 取得匯率 
	                    String spotBuy   = row.select("td[data-table='本行即期買入']").first().text();
	                    String spotSell  = row.select("td[data-table='本行即期賣出']").first().text(); 
	                    String cashBuy   = row.select("td[data-table='本行現金買入']").first().text();
	                    String cashSell  = row.select("td[data-table='本行現金賣出']").first().text();
	                    String renewTime = document.select("span.time").first().text();
	                    
	                    ExchangeRate exchangeRate = new ExchangeRate();
	                    
	                    exchangeRate.setCurrencyName(curName);
	                    exchangeRate.setCurrencyCode(code);
	                    exchangeRate.setSpotBuy(new BigDecimal(spotBuy));  
	                    exchangeRate.setSpotSell(new BigDecimal(spotSell));
	                    exchangeRate.setCashBuy(new BigDecimal(cashBuy));
	                    exchangeRate.setCashSell(new BigDecimal(cashSell));
	                    exchangeRate.setRenewTime(renewTime);
	                    
	                    exchangeRates.add(exchangeRate);
	        	} 
	        	
	        	return exchangeRates;
             	    		    
        	}else {
        		
        		System.err.println("找不到匯率表格，請檢查網頁結構或 URL 是否正確。");
        	}         	
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public ExchangeRate getTargetExchangeRate(String currencyCode) {
		
		List<ExchangeRate> exchangeRates = getTwdExchangeRate();
		
		ExchangeRate exchangeRate = exchangeRates.stream()
				                                 .filter( er -> er.getCurrencyCode().equals(currencyCode))
				                                 .findFirst()
				                                 .get();
		return exchangeRate ;
	}

	@Override
	public List<ExchangeRate> getAllForeignAccountExchangeRate(Long userId) {
		
		List<String> foreignCodes = accountService.findAllUserForeignAccounts(userId).stream().map(a->a.getCurrency().getCode()).toList();
		
		List<ExchangeRate> foreignExchangeRates = getTwdExchangeRate().stream()
				                                                      .filter(er->foreignCodes.contains(er.getCurrencyCode()))
				                                                      .toList();
		
		return foreignExchangeRates;
	}

	
	
	
	
	
	
	
	
	

}
