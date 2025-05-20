package ua.land.go.landSurveyorOffice.model.parser;

import jakarta.mail.Message;
import ua.land.go.landSurveyorOffice.model.mail.MailMessage;

import java.util.List;

public interface MailParser extends Parser {

    void connect();

    List<Message> getFolderMessages(String folderName);

}
