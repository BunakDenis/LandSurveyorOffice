package ua.land.go.landSurveyorOffice.model.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailMessage {

    private String subject;
    private String applicant;
    private String cadNumber;
    private boolean isProcessed;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MailMessage message = (MailMessage) o;
        return Objects.equals(subject, message.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, applicant);
    }
}
