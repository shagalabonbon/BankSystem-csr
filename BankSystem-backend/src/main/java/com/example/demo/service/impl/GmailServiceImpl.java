package com.example.demo.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import com.example.demo.service.GmailService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;


@Service
public class GmailServiceImpl implements GmailService {

	private static final String APPLICATION_NAME = "Your App Name";
	
	// JSON_FACTORY: 用於解析 JSON 檔案 ( 金鑰資料 )
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();   
    
    // 定義授權範圍 ( 只能寄信 )
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/gmail.send");
	
	@Override
	public Gmail getGmailService() throws Exception {
		
//      clearStoredTokens();    /* 使用時將此行取消註解 ( 讓 token 重新授權 ) */
			
		// 金鑰檔案路徑
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(GmailServiceImpl.class.getResourceAsStream("/credentials.json")));  // getResourceAsStream: 以流的方式讀取 credentials.json，該檔案應位於 src 目錄下         
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens"))) // 設定存儲資料夾
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return new Gmail.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
        
	}


	@Override
	public MimeMessage createEmail(String to, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
//        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        
        // 取得當前日期與時間
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        // 將日期與時間加入信件內容
        String fullBodyText = bodyText + "\n\nSent on: " + formattedDateTime;
        email.setText(fullBodyText);
        return email;
	}

	@Override
	public Message sendMessage(Gmail service, String userId, MimeMessage email) throws MessagingException, IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return service.users().messages().send(userId, message).execute();
	}

	@Override
	public void clearStoredTokens() throws IOException {
		Path tokensPath = Paths.get("tokens");
        if (Files.exists(tokensPath)) {
            Files.walk(tokensPath)
                .map(Path::toFile)
                .forEach(File::delete);
        }
    
	}
	
	
	// ----------------------------------------------------------
	
	
	
	
}
