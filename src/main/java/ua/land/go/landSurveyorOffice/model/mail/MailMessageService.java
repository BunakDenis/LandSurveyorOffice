package ua.land.go.landSurveyorOffice.model.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailMessageService {

    private Map<String, List<MailMessage>> messages;

    /*
    TODO Добавить методы:
    1. рефактор метода getNoProcessedMessages (номера заяв не сравниваются)
    2. getOverdueApplications
    3. getAllApplicators
    4. getAllApplicatorApplications
     */

    public List<MailMessage> getNoProcessedMessages() {

        List<MailMessage> result = new ArrayList<>();

        List<MailMessage> createdMessages = messages.get("created");
        List<MailMessage> processedMessages = messages.get("processed");

        System.out.println("Ожидаемый результат - " + (createdMessages.size() - processedMessages.size()));

        for (MailMessage msg : createdMessages) {
            boolean isProcessed = false;
            MailMessage noProcessed = new MailMessage();

            for (int i = 0; i < processedMessages.size(); i++) {
                if (processedMessages.get(i).equals(msg)) {
                    isProcessed = true;

                }
            }

            if (!isProcessed) {
                result.add(msg);
            }
        };

        System.out.println("Полученный результат - " + result.size());

        return result;
    }

}
