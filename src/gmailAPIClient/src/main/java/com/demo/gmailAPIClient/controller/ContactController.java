package com.demo.gmailAPIClient.controller;

import com.demo.gmailAPIClient.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/contact")
public class ContactController {

  private final ContactService contactService;

  @PostMapping(path = "/request")
  public void submitContactRequest(
    @RequestParam("subject") String subject,
    @RequestParam("description") String description,
    @RequestBody MultipartFile file) {
    contactService.submitContactRequest(subject, description, file);
  }

}
