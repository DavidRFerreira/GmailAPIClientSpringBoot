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

      boolean success = gmailAPIService.sendMessage(
        subject,
        description,
        file
      );

      if (!success) {
        throw new Exception();
      }

    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
        "Not able to process request.");
    }

  }

}
