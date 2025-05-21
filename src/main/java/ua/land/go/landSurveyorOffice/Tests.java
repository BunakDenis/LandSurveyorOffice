package ua.land.go.landSurveyorOffice;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import ua.land.go.landSurveyorOffice.model.file.JsonFileService;
import ua.land.go.landSurveyorOffice.model.mail.MailMessage;
import ua.land.go.landSurveyorOffice.model.mail.MailMessageService;
import ua.land.go.landSurveyorOffice.model.parser.UkrDotNetParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tests {

    public static void main(String[] args) throws IOException {
/*
        String login = "xisi926@ukr.net";
        String password = "XlvRpnqkrAQQMmo4";

        UkrDotNetParser parser = new UkrDotNetParser(login, password);

        parser.connect();

        List<MailMessage> createdMailMessages = new ArrayList<>();
        List<MailMessage> processedMailMessages = new ArrayList<>();

        List<Message> created = parser.getFolderMessagesOfYear(2025, "Держгеокадастр");
        List<Message> processed = parser.getFolderMessagesOfYear(2025, "опрацьовані");

        System.out.println("Остання заявка з поданих на реєстрацію - " +
                parser.getMailMessageFromMessage(created.get(created.size() - 1), false)
        );

        System.out.println("Остання заявка з опрацьованих - " +
                parser.getMailMessageFromMessage(processed.get(processed.size() - 1), true)
        );


        for (Message message : processed) {
            processedMailMessages.add(parser.getMailMessageFromMessage(message, true));
        }

        for (Message message : created) {
            createdMailMessages.add(parser.getMailMessageFromMessage(message, false));
        }

        parser.closeStore();
*/

        JsonFileService jsonFileService = new JsonFileService("./messages.json");

        jsonFileService.writeFileContentToCsvFile();

        /*
        // Собираем две коллекции в единый объект, чтобы удобно хранить в одном файле
        Map<String, List<MailMessage>> payload = new LinkedHashMap<>();
        payload.put("created",   createdMailMessages);
        payload.put("processed", processedMailMessages);

        jsonFileService.writeDataToFile(payload);
        */

        /*
        Map<String, List<MailMessage>> payload = jsonFileService.loadDataFromFile();

        MailMessageService mailMessageService = new MailMessageService(payload);

        List<MailMessage> noProcessedMessages = mailMessageService.getNoProcessedMessages();
        */

        //System.out.println("Неопрацьовані заявки");
        //noProcessedMessages.forEach(System.out::println);

    }
}
