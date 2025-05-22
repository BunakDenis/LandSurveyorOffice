package ua.land.go.landSurveyorOffice;


import ua.land.go.landSurveyorOffice.model.file.ExtractGeoCadastr;
import ua.land.go.landSurveyorOffice.model.file.FileWriter;
import ua.land.go.landSurveyorOffice.model.parser.file.GeoCadastralExtractParser;

import java.io.IOException;
import java.util.List;

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

        /*
        JsonFileService jsonFileService = new JsonFileService("./messages.json");

        jsonFileService.writeFileContentToCsvFile();
        */

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

        GeoCadastralExtractParser geoCadastralExtractParser =
                new GeoCadastralExtractParser("f:\\Работа\\Сновський район\\с. Кучинівка\\паи\\ПП Нива Імпульс\\Витяги");

        geoCadastralExtractParser.parse();
        List<ExtractGeoCadastr> extracts = geoCadastralExtractParser.getExtracts();

        FileWriter writer = new FileWriter();

        writer.writeDataToCsvFile(
                extracts, "f:\\Работа\\Сновський район\\с. Кучинівка\\паи\\ПП Нива Імпульс\\parsePdf.csv"
        );

    }
}
