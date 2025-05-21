package ua.land.go.landSurveyorOffice.model.parser;


import ua.land.go.landSurveyorOffice.model.mail.MailMessage;


import jakarta.mail.*;
import jakarta.mail.search.FromStringTerm;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*

    TODO Добавить методы:
    1. getSubject()
    2. getApplicant()
    3. getCadNumber()

    В методе getFolderMessagesOfYear добавить проверку на тип сообщения (полученое или отправленое)
    если полученое проверять дату через метод getReceivedDate() если отправленное через getSentDate()

 */

public class UkrDotNetParser implements MailParser {

    private final List<MailMessage> createdMessages = new ArrayList<>();
    private final List<MailMessage> processedMessages = new ArrayList<>();
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

        result.addAll(getFolderMessages(folderName));

        return result.stream().filter(message -> {
                    try {
                        Optional<LocalDateTime> recieveDateOptional = Optional.ofNullable(message.getReceivedDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime());

                        if (recieveDateOptional.isPresent()) {
                            LocalDateTime receiveDate = recieveDateOptional.get();
                            if (receiveDate.getYear() == year) {
                                return true;
                            }
                        }

                    } catch (MessagingException e) {
                        System.err.println(e.getMessage());
                    }
                    return false;
                })
                .toList();
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

    public List<MailMessage> getCreatedMessages() {
        return Collections.unmodifiableList(createdMessages);
    }

    public List<MailMessage> getProcessedMessages() {
        return Collections.unmodifiableList(processedMessages);
    }

    public MailMessage getMailMessageFromMessage(Message msg, boolean isProcessed) {
        MailMessage result = new MailMessage();

        try {
            String subject = msg.getSubject() != null ? msg.getSubject() : "(без темы)";
            String body = getTextFromMessage(msg);

            result.setApplicationNumber(getSubjectFromString(subject));
            result.setApplicant(getApplicantFromMessageBody(body));
            result.setCadNumber(getCadNumberFromMessageBody(body));
            result.setReceivedDate(getReceivedDate(msg).toString());
            result.setProcessed(
                    isProcessed ? true : false
            );


        } catch (Exception e) {
            System.err.println("Ошибка создания mailMessage = " + e.getMessage());
        }

        return result;

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

    private String getSubjectFromString(String text) {
        String result = "";

        int start = text.indexOf("№-");
        int end = start + 13;

        if (start != -1) {
            result = text.substring((start + 2), end).trim();
        }
        return result;
    }

    private String getApplicantFromMessageBody(String body) {
        String result = "";

        if (body == null || body.isEmpty()) return "";

        String lowerBody = body.toLowerCase();
        int index = lowerBody.indexOf("шановний(а)");
        int offset = 11;

        if (index == -1) {
            index = lowerBody.indexOf("шановний");
            offset = 8;
        }

        if (index == -1) return "";

        // Старт после ключевого слова
        int start = index + offset;

        // Ищем запятую, начиная с позиции start
        int end = body.indexOf(",", start);
        if (end == -1) {
            end = body.length();
        }

        result = body.substring(start, end).trim();

        // Очистка
        result = result.replace("&quot;", "\"").replace(",", "");

        return result;
    }

    private LocalDateTime getReceivedDate(Message msg) {
        try {
            Optional<LocalDateTime> recieveDateOptional = Optional.ofNullable(msg.getReceivedDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());

            if (recieveDateOptional.isPresent()) {
                return recieveDateOptional.get();
            }

        } catch (MessagingException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private String getCadNumberFromMessageBody(String body) {
        String result = "відсутній";
        Pattern pattern = Pattern.compile("<\\s*\\d{10}:\\d{2}:\\d{3}:\\d{4}\\s*>");
        Matcher matcher = pattern.matcher(body);

        if (matcher.find()) {
            result = matcher.group();

            result = result.replaceAll("[<>\\s]", "");
        }
        return result;
    }
}
