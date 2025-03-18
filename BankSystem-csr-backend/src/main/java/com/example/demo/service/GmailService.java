package com.example.demo.service;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

public interface GmailService {
	
	Gmail getGmailService()throws Exception;
	
	MimeMessage createEmail(String to, String subject, String bodyText) throws MessagingException;
	
	Message sendMessage(Gmail service, String userId, MimeMessage email) throws MessagingException, IOException;
	
	void clearStoredTokens() throws IOException;
	

}
