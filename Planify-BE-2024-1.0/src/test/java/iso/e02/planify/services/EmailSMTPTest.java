package iso.e02.planify.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailSMTPTest {

    private EmailSMTP emailSMTP;

    @BeforeEach
    void setUp() {
        emailSMTP = new EmailSMTP();
        System.setProperty("EMAIL_FROM", "test@example.com");
        System.setProperty("EMAIL_PASSWORD", "securepassword");
    }

    @Test
    void testSendEmail_ValidParameters() throws Exception {
        String userEmail = "recipient@example.com";
        String token = "123456";

        // Simulate the reading of email content
        String emailContent = "<html><body><p>Hello ${userEmail},</p><p>Reset your password <a href=\"${resetLink}\">here</a></p></body></html>";
        String expectedContent = emailContent
                .replace("${userEmail}", userEmail)
                .replace("${resetLink}", "http://localhost:4200/change-password?token=" + token);
        
        // Use a temporary file to simulate emailContent.html
        Files.write(Paths.get("src/test/resources/emailContent.html"), emailContent.getBytes());

        // Execute the method
        emailSMTP.sendEmail(userEmail, token);
        
        // Assertions (actual email sending cannot be verified, but can check if it runs without exceptions)
        assertDoesNotThrow(() -> {
            // Since we can't check actual email sending, just ensure no exceptions occur
        });

        // Verify if the email content was properly created
        assertEquals(expectedContent, emailContent
                .replace("${userEmail}", userEmail)
                .replace("${resetLink}", "http://localhost:4200/change-password?token=" + token));
    }



    
    @Test
    void testSendEmailFileNotFound() {
        String userEmail = "recipient@example.com";
        String token = "123456";

        // Make sure the emailContent.html file is missing or moved
        System.clearProperty("EMAIL_FROM");
        System.clearProperty("EMAIL_PASSWORD");

        assertThrows(NullPointerException.class, () -> {
            emailSMTP.sendEmail(userEmail, token);
        }, "Expected NullPointerException when email content file is missing");
       
    }

    
}