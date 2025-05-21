package ua.land.go.landSurveyorOffice.model.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "applicationNumber")
public class MailMessage {

    private String applicationNumber;
    private String applicant;
    private String cadNumber;
    private String receivedDate;
    private boolean isProcessed;

}
