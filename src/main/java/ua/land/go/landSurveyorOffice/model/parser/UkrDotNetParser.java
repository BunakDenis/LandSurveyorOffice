package ua.land.go.landSurveyorOffice.model.parser;


import ua.land.go.landSurveyorOffice.model.mail.MailMessage;


import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.search.FromStringTerm;

import java.io.Console;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*

    TODO Добавить методы:
    1. getSubject()
    2. getApplicant()
    3. getCadNumber()

    В методе getFolderMessagesOfYear добавить проверку на тип сообщения (полученое или отправленое)
    если полученое проверять дату через метод getReceivedDate() если отправленное через getSentDate()

 */

public class UkrDotNetParser implements MailParser {

    private final List<MailMessage> messagesCreatedStatement = new ArrayList<>();
    private final List<MailMessage> messagesProcessedStatement = new ArrayList<>();
    private String login;
    private String password;
    private Store store;

    public UkrDotNetParser(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public void connect() {

        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.ukr.net");
        props.put("mail.imaps.port", "993");
        //props.put("mail.debug", "true");

        try {
            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect("imap.ukr.net", login, password);

        } catch (Exception e) {
            System.err.println("Ошибка при подключении к почте: " + e.getMessage());
        }
    }

    public List<Message> getFolderMessages(String folderName) {

        List<Message> result = new ArrayList<>();

        try {
            //Парсинг созданных заяв
            Folder inboxCreatedStatement = store.getFolder(folderName);
            inboxCreatedStatement.open(Folder.READ_ONLY);

            Message[] foundMessagesCreatedStatement = inboxCreatedStatement.search(new FromStringTerm("e-noreply@land.gov.ua"));

            result.addAll(Arrays.asList(foundMessagesCreatedStatement));

            return result;

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public List<Message> getFolderMessagesOfYear(int year, String folderName) {
        List<Message> result = new ArrayList<>();

        try {
            //Парсинг созданных заяв
            Folder inboxCreatedStatement = store.getFolder(folderName);
            inboxCreatedStatement.open(Folder.READ_ONLY);

            Message[] foundMessagesCreatedStatement = inboxCreatedStatement.search(new FromStringTerm("e-noreply@land.gov.ua"));

            result.addAll(Arrays.asList(foundMessagesCreatedStatement));

            return result.stream().filter(message -> {
                        try {
                            LocalDateTime sentDate = message.getSentDate().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime();

                            if (sentDate.getYear() == year) {
                                return true;
                            }

                        } catch (MessagingException e) {
                            System.err.println(e.getMessage());
                        }
                        return false;
                    })
                    .toList();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public List<MailMessage> getMessagesOfFolderByYear(int year, String folderName) {
        List<MailMessage> result = new ArrayList<>();
        List<Message> messages = getFolderMessagesOfYear(year, folderName);

        try {
            String textFromMessage = getTextFromMessage(messages.get(0));

            System.out.println("textFromMessage = " + textFromMessage);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    @Override
    public void parse() {
        connect();
    }

    public void closeStore() {
        try {
            store.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    public List<MailMessage> getMessagesCreatedStatement() {
        return Collections.unmodifiableList(messagesCreatedStatement);
    }

    public List<MailMessage> getMessagesProcessedStatement() {
        return Collections.unmodifiableList(messagesProcessedStatement);
    }

    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                if (part.isMimeType("text/plain")) {
                    return part.getContent().toString();
                } else if (part.isMimeType("text/html")) {
                    return part.getContent().toString();
                }
            }
        }
        return "";
    }

}
