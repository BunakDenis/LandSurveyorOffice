package ua.land.go.landSurveyorOffice.model.parser.mail;

import jakarta.mail.Message;
import ua.land.go.landSurveyorOffice.model.parser.Parser;

import java.util.List;

public interface MailParser extends Parser {

    void connect();

    List<Message> getFolderMessages(String folderName);

}
