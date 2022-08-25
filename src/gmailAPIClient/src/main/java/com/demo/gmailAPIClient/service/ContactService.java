package com.demo.gmailAPIClient.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ContactService {

  private final GmailAPIService gmailAPIService;

  public ContactService(GmailAPIService gmailAPIService) {
    this.gmailAPIService = gmailAPIService;
  }

  public void submitContactRequest(
    String subject,
    String description,
    MultipartFile file) {

    try {

      gmailAPIService.sendMessage(
        subject,
        description,
        file
      );

    } catch (MessagingException | IOException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "Not able to process request.");
    }

  }

}
