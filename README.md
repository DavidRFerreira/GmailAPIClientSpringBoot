# Gmail API Client with Java Spring Boot

This repository includes the source code for an API built with Java Spring Boot that makes use of the Gmail API in order to send emails with an attachment.

This tutorial presents how to use the Gmail API with an OAuth Credential requiring a single time authorization action by the sender gmail account owner (using google's refresh token). This means that the sender does not need to login or authorize the API everytime we need to send an email.


## You will learn how to 
- Create OAuth Credential for Google APIs integration with your application.
- Authorize a Gmail account for Gmail API use and and retrive its refresh token.
- Implement Gmail API's access token refreshing in order to need a single-time-only account's owner action to authorize/login. 
- Integrate Google's Gmail API with your Java Spring Boot own API in order to send emails with attachments (for example, a pdf file). 


## Use Case Example

[Google's Gmail API](https://developers.google.com/gmail/api) lets you view and manage your Gmail inbox and supports features like reading and sending emails, manage drafts and attachments and some others. 

This repository and its source code proposes a particular use for this API. Our goal is to use the Gmail API in order to send emails from a specific email account to some other email address. Examples of this use includes:

- Having a specific gmail account from where account confirmation emails are sent to a new user registering in our app. In this case, we send email from an admin@gmail.com account to the user's email.
- Having a contact form in our website where users can submit requests. In this case, we can send an email from a fixed account (admin@gmail.com) to the department's email that is going to handle the request.
- Sending marketing emails.

All these three cases have in common the fact that the emails are going to be sent from a previosly defined and fixed Gmail account, like an administration account or a department's account. But the email can be sent to any account, including to an account belonging to some user of our application.

This could be achieved with the many SMTP providers available but the majority of them requires a more complex setting of a SMTP server in your own application's server. By using the Gmail API we are not require to set and mantain any SMTP server.

The source code on this repository shows an API with a single endpoint to submit a contact form with a subject, a description and a file. Then, an email is sent from a previously defined gmail account containing that subject, description and the file as an attachment. The recipient address in this example is also defined in the environment's variable but it can be defined at runtime (for example, an email address specified by the user on the API request).

## Tutorial

### Creating OAuth Credential and Refresh Token for Gmail Account

For a step-by-step tutorial on how to create a Google Cloud project and how to create an OAuth Credential and a refresh token for the sender Gmail account refer to this [documentation](./docs/credentials.md).

### Adding Credentials as Environment Variables in IntelliJ

This source code on this stores the *Google Client Id*, *Google Client Secret*, *sender account's Gmail Refresh Token*, *sender email address* and *recipient email address* in the environment variables. 

So, in order to build and test the demo code you need to create your own credentials and tokens an save it on your IDE's environment variables (or for testing purposes, replace them directly in the application.yaml file).

The environment variables should be in the form of

```
GOOGLE_CLIENT_ID=XXXXXXX;GOOGLE_CLIENT_SECRET=XXXXXXX;GOOGLE_REFRESH_TOKEN=XXXXXXX;GOOGLE_FROM_EMAIL=XXXXXXX;GOOGLE_TO_EMAIL=XXXXXXX
```

But depending on the use case, the recipient email address *(TO_EMAIL)* can be specified during run-time having no need to store it on the environment variables. 


### Implementation
