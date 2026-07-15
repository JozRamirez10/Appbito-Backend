package com.appbito.appbito.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Email Service Test
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private static final String TO = "test@email.com";

    private static final String TOKEN = "token";

    private static final String URL = "http://test/verify";

    private static final String VERIFICATION_URL = "verificationUrl";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, VERIFICATION_URL, URL);
    }

    @Test
    void sendVerificationEmailOk() {

        JavaMailSenderImpl dummySender = new JavaMailSenderImpl();
        MimeMessage dummyMessage = dummySender.createMimeMessage();

        when(mailSender.createMimeMessage()).thenReturn(dummyMessage);
        doNothing().when(mailSender).send(dummyMessage);

        emailService.sendVerificationEmail(TO, TOKEN);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(dummyMessage);
    }

    @Test
    void sendVerificationEmailException() throws Exception {

        MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        doThrow(new MessagingException()).when(mockMessage).setRecipient(any(), any());

        emailService.sendVerificationEmail(TO, TOKEN);

        verify(mailSender).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

}
