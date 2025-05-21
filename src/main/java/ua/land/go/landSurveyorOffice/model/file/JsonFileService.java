package ua.land.go.landSurveyorOffice.model.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.land.go.landSurveyorOffice.model.mail.MailMessage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static org.unbescape.csv.CsvEscape.escapeCsv;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonFileService {

    private String filePath;

    public void writeDataToFile(Map<String, List<MailMessage>> data) {
        // Сериализация в JSON с «красивым» форматированием
        ObjectMapper mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);

        File jsonFile = getFile();

        try {
            mapper.writeValue(jsonFile, data);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("JSON‑файл сохранён: " + jsonFile.getAbsolutePath());
    }

    public Map<String, List<MailMessage>> loadDataFromFile() {
        ObjectMapper mapper = new ObjectMapper();

        File jsonFile = getFile();

        // Десериализация JSON-файла в карту, содержащую две коллекции
        Map<String, List<MailMessage>> result = null;

        try {
            result = mapper.readValue(
                    jsonFile,
                    new TypeReference<Map<String, List<MailMessage>>>() {
                    }
            );
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("Загружено созданных заявок: " + result.get("created").size());
        System.out.println("Загружено обработанных заявок: " + result.get("processed").size());

        return result;
    }

    public void writeFileContentToCsvFile() {
        Map<String, List<MailMessage>> data = loadDataFromFile();

        // Заменяем расширение .json на .csv
        this.filePath = this.filePath.replace(".json", ".csv");

        File file = getFile();

        try (PrintWriter writer = new PrintWriter(file)) {
            // Записываем заголовок
            writer.println("source;applicationNumber;applicant;cadNumber;receivedDate;processed");

            for (Map.Entry<String, List<MailMessage>> entry : data.entrySet()) {
                String key = entry.getKey();
                List<MailMessage> messages = entry.getValue();

                for (MailMessage msg : messages) {
                    writer.printf(
                            "%s;%s;%s;%s;%s;%s%n",
                            key,
                            escapeCsv(msg.getApplicationNumber()),
                            escapeCsv(msg.getApplicant()),
                            escapeCsv(msg.getCadNumber()),
                            msg.getReceivedDate(), // убедитесь, что toString даёт ISO 8601 или нужный формат
                            msg.isProcessed()
                    );
                }
            }

            System.out.println("CSV-файл успешно сохранён: " + file.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Ошибка при записи CSV: " + e.getMessage());
            e.printStackTrace();
        }
        this.filePath = this.filePath.replace(".csv", ".json");
    }


    private File getFile() {
        return new File(filePath);
    }

}
