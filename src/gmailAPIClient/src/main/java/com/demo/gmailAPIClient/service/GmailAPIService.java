package com.demo.gmailAPIClient.service;

import com.demo.gmailAPIClient.model.GmailCredential;
import com.demo.gmailAPIClient.model.GoogleTokenResponse;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

@Service
public class GmailAPIService {

  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private HttpTransport httpTransport;
  private GmailCredential gmailCredential;
  @Value("${spring.google.client-id}")
  private String clientId;
  @Value("${spring.google.client-secret}")
  private String secretKey;
  @Value("${spring.google.refresh-token}")
  private String refreshToken;
  @Value("${spring.google.from-email}")
  private String fromEmail;
  @Value("${spring.google.to-email}")
  private String toEmail;

  public GmailAPIService() {

    try {

      this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();

    } catch (Exception e) {

      e.printStackTrace();

    }

    this.gmailCredential = new GmailCredential(
      clientId,
      secretKey,
      refreshToken,
      null,
      null,
      fromEmail
    );

  }

  public boolean sendMessage(
    String subject,
    String body,
    MultipartFile attachment) throws MessagingException, IOException {

    refreshAccessToken();

    Message message = createMessageWithEmail(
      createEmail(toEmail, gmailCredential.userEmail(), subject, body, attachment));

    return createGmail()
      .users()
      .messages()
      .send(gmailCredential.userEmail(), message)
      .execute()
      .getLabelIds()
      .contains("SENT");

  }

  private Gmail createGmail() {

    Credential credential = authorize();

    return new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
      .build();

  }

  private MimeMessage createEmail(
    String to,
    String from,
    String subject,
    String bodyText,
    MultipartFile attachment) throws MessagingException {

    MimeMessage email = new MimeMessage(Session.getDefaultInstance(new Properties(), null));

    email.setFrom(new InternetAddress(from));

    email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));

    email.setSubject(subject);

    email.setText(bodyText);

    email = addAttachmentToEmail(email, bodyText, attachment);

    return email;

  }

  private MimeMessage addAttachmentToEmail(MimeMessage email, String bodyText,
    MultipartFile attachment) {

    if (attachment.isEmpty()) {
      return email;
    }

    try {

      Multipart multipart = new MimeMultipart();

      MimeBodyPart mimeBodyPart = new MimeBodyPart();

      mimeBodyPart.setContent(bodyText, "text/plain");

      multipart.addBodyPart(mimeBodyPart);

      mimeBodyPart = new MimeBodyPart();

      DataSource ds = new ByteArrayDataSource(attachment.getBytes(), attachment.getContentType());
      mimeBodyPart.setDataHandler(new DataHandler(ds));
      mimeBodyPart.setFileName(attachment.getOriginalFilename());

      multipart.addBodyPart(mimeBodyPart);

      email.setContent(multipart);

    } catch (Exception e) {

      e.printStackTrace();

      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
        "Not able to process request");

    }

    return email;

  }

  private Message createMessageWithEmail(MimeMessage emailContent)
    throws MessagingException, IOException {

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    emailContent.writeTo(buffer);

    return new Message()
      .setRaw(Base64.encodeBase64URLSafeString(buffer.toByteArray()));
  }

  private Credential authorize() {

    try {

      TokenResponse tokenResponse = refreshAccessToken();

      return new Credential(BearerToken.authorizationHeaderAccessMethod()).setFromTokenResponse(
        tokenResponse);

    } catch (Exception e) {

      e.printStackTrace();

      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
        "Not able to process request.");

    }

  }

  private TokenResponse refreshAccessToken() {

    RestTemplate restTemplate = new RestTemplate();

    GmailCredential gmailCredentialsDto = new GmailCredential(
      clientId,
      secretKey,
      refreshToken,
      "refresh_token",
      null,
      null
    );

    HttpEntity<GmailCredential> entity = new HttpEntity(gmailCredentialsDto);

    try {

      GoogleTokenResponse response = restTemplate.postForObject(
        "https://www.googleapis.com/oauth2/v4/token",
        entity,
        GoogleTokenResponse.class);

      gmailCredential = new GmailCredential(
        clientId,
        secretKey,
        refreshToken,
        null,
        response.getAccessToken(),
        fromEmail
      );

      return response;

    } catch (Exception e) {

      e.printStackTrace();

      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
        "Not able to process request.");

    }
  }

}
