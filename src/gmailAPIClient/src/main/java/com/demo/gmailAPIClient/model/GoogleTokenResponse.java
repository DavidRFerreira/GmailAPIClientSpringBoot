package com.demo.gmailAPIClient.model;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.util.Key;

public class GoogleTokenResponse extends TokenResponse {

  @Key("expires_in")
  private Integer expiresInSeconds;
}