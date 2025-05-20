package ua.land.go.landSurveyorOffice;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import ua.land.go.landSurveyorOffice.model.mail.MailMessage;
import ua.land.go.landSurveyorOffice.model.parser.UkrDotNetParser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tests {

    public static void main(String[] args) throws IOException {

        String login = "xisi926@ukr.net";
        String password = "XlvRpnqkrAQQMmo4";

        UkrDotNetParser parser = new UkrDotNetParser(login, password);

        parser.parse();

        /*
        List<Message> created = parser.getFolderMessagesOfYear(2025, "Держгеокадастр");
        List<Message> processed = parser.getFolderMessagesOfYear(2025, "опрацьовані");

        System.out.println("created.size() = " + created.size());
        System.out.println("processed.size() = " + processed.size());
        */

        parser.getMessagesOfFolderByYear(2025, "опрацьовані");
        parser.closeStore();

        /*
        for (Message msg : foundMessagesCreatedStatement) {
            MailMessage createdMessage = new MailMessage();

            String subject = msg.getSubject() != null ? msg.getSubject() : "(без темы)";
            String body = getTextFromMessage(msg);

            int pos = subject.indexOf("№-");
            if (pos != -1) {
                subject = subject.substring(pos + 2).trim();
            }

            createdMessage.setSubject(subject);

            Pattern pattern = Pattern.compile("<\\s*\\d{10}:\\d{2}:\\d{3}:\\d{4}\\s*>");
            Matcher matcher = pattern.matcher(body);

            if (matcher.find()) {
                String result = matcher.group();

                result = result.replaceAll("[<>\\s]", "");

                createdMessage.setCadNumber(result);
            }

            int start = body.indexOf("Шановний");
            if (start != -1) {
                start += 8; // пропускаем слово
                int comma = body.indexOf(",", start);

                // если запятая не найдена, берём до конца строки
                if (comma == -1) {
                    comma = body.length();
                }

                String applicant = body.substring(start, comma).trim();
                applicant = applicant.replace("&quot;", "\"");

                createdMessage.setApplicant(applicant);
                createdMessage.setProcessed(false);
            }
            messagesCreatedStatement.add(createdMessage);
        }

        inboxCreatedStatement.close(false);

        //Парсинг обработанных заяв
        Folder inboxProcessedStatement = store.getFolder("опрацьовані");
        inboxProcessedStatement.open(Folder.READ_ONLY);

        Message[] foundMessagesProcessedStatement = inboxProcessedStatement.search(new FromStringTerm("e-noreply@land.gov.ua"));
        for (Message msg : foundMessagesProcessedStatement) {
            MailMessage processedMessage = new MailMessage();

            String subject = msg.getSubject() != null ? msg.getSubject() : "(без темы)";
            String body = getTextFromMessage(msg);

            int pos = subject.indexOf("№-");
            if (pos != -1) {
                subject = subject.substring(pos + 2).trim();
            }

            processedMessage.setSubject(subject);

            Pattern pattern = Pattern.compile("<\\s*\\d{10}:\\d{2}:\\d{3}:\\d{4}\\s*>");
            Matcher matcher = pattern.matcher(body);

            if (matcher.find()) {
                String result = matcher.group();

                result = result.replaceAll("[<>\\s]", "");

                processedMessage.setCadNumber(result);
            }

            int start = body.indexOf("Шановний");
            if (start != -1) {
                start += 8; // пропускаем слово
                int comma = body.indexOf(",", start);

                // если запятая не найдена, берём до конца строки
                if (comma == -1) {
                    comma = body.length();
                }

                String applicant = body.substring(start, comma).trim();
                applicant = applicant.replace("&quot;", "\"");
                processedMessage.setApplicant(applicant);
                processedMessage.setProcessed(true);
            }

            messagesProcessedStatement.add(processedMessage);

        }

        parser.parse();

        List<MailMessage> messagesCreatedStatement = parser.getMessagesCreatedStatement();
        List<MailMessage> messagesProcessedStatement = parser.getMessagesProcessedStatement();

        System.out.println("messagesCreatedStatement.size() = " + messagesCreatedStatement.size());

        messagesCreatedStatement.forEach(System.out::println);

        System.out.println("messagesProcessedStatement.size() = " + messagesProcessedStatement.size());

        messagesProcessedStatement.forEach(System.out::println);

        // Собираем две коллекции в единый объект, чтобы удобно хранить в одном файле
        Map<String, List<MailMessage>> payload = new LinkedHashMap<>();
        payload.put("messagesCreatedStatement",   messagesCreatedStatement);
        payload.put("messagesProcessedStatement", messagesProcessedStatement);

        // Сериализация в JSON с «красивым» форматированием
        ObjectMapper mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);

        File jsonFile = new File("./messages.json");
        mapper.writeValue(jsonFile, payload);

        System.out.println("JSON‑файл сохранён: " + jsonFile.getAbsolutePath());
*/

        /*
        ObjectMapper mapper = new ObjectMapper();

        File jsonFile = new File("./messages.json");

        // Десериализация JSON-файла в карту, содержащую две коллекции
        Map<String, List<MailMessage>> payload = mapper.readValue(
                jsonFile,
                new TypeReference<Map<String, List<MailMessage>>>() {}
        );

        List<MailMessage> messagesCreatedStatement   = payload.get("messagesCreatedStatement");
        List<MailMessage> messagesProcessedStatement = payload.get("messagesProcessedStatement");

        System.out.println("Загружено созданных заявок: " + messagesCreatedStatement.size());
        System.out.println("Загружено обработанных заявок: " + messagesProcessedStatement.size());

        String applicant = "";

        for (int i = 0; i < messagesCreatedStatement.size(); i++) {
            int index = messagesProcessedStatement.indexOf(messagesCreatedStatement.get(i));

            if (index >= 0) {
                MailMessage createdMessage = messagesCreatedStatement.get(i);
                MailMessage processedMessage = messagesProcessedStatement.get(index);

                if (!processedMessage.getApplicant().equals(applicant)) {
                    applicant = processedMessage.getApplicant();

                    System.out.println("Опрацьовані заявки від " + applicant);
                }

                System.out.println(processedMessage.getSubject());

            }
        }
*/
    }
}
