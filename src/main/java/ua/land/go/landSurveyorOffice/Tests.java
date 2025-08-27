package ua.land.go.landSurveyorOffice;


import jakarta.mail.Message;
import ua.land.go.landSurveyorOffice.model.file.ExtractGeoCadastr;
import ua.land.go.landSurveyorOffice.model.file.FileWriter;
import ua.land.go.landSurveyorOffice.model.file.JsonFileService;
import ua.land.go.landSurveyorOffice.model.mail.MailMessage;
import ua.land.go.landSurveyorOffice.model.mail.MailMessageService;
import ua.land.go.landSurveyorOffice.model.parser.file.GeoCadastralExtractParser;
import ua.land.go.landSurveyorOffice.model.parser.mail.UkrDotNetParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tests {

    public static void main(String[] args) throws IOException {

        JsonFileService jsonFileService = new JsonFileService("./messages.json");
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

        // Собираем две коллекции в единый объект, чтобы удобно хранить в одном файле
        Map<String, List<MailMessage>> payload = new LinkedHashMap<>();
        payload.put("created",   createdMailMessages);
        payload.put("processed", processedMailMessages);

        jsonFileService.writeDataToFile(payload);

        jsonFileService.writeFileContentToCsvFile();
*/

        List<String> pdfFilePaths = new ArrayList<>();


        pdfFilePaths.add(
                "g:\\Работа\\ФеодалПроект\\Львівська область\\Яворівський район\\Мостиська ОТГ\\с. Зав_язанці\\Не зарегистрированные\\витяги\\"
        );

        for (String pdfFilePath : pdfFilePaths) {
            GeoCadastralExtractParser geoCadastralExtractParser =
                    new GeoCadastralExtractParser(pdfFilePath);

            geoCadastralExtractParser.parse();
            List<ExtractGeoCadastr> extracts = geoCadastralExtractParser.getExtracts();

            FileWriter writer = new FileWriter();

            writer.writeDataToCsvFile(
                    extracts, pdfFilePath + "parse.csv"
            );
        }
    }
}
