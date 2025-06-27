package ua.land.go.landSurveyorOffice.model.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static org.unbescape.csv.CsvEscape.escapeCsv;

@Data
@NoArgsConstructor
public class FileWriter {

    public void writeDataToCsvFile(List<ExtractGeoCadastr> data, String outPutFilePath) {
        File file = new File(outPutFilePath);

        try (PrintWriter writer = new PrintWriter(file)) {

            // Записываем заголовок
            writer.println("Номер заяви;Кадастровий номер;Місце розташування земельної ділянки;Площа;" +
                    "Власник(користувач) зем. діл.;Правовий документ;Назва файлу витяга");


            for (ExtractGeoCadastr extract : data) {

                System.out.println("Запись информации об участке с кад. номером - " + extract.getCadNumber());

                writer.printf(
                        "%s;%s;%s;%s;%s;%s;%s%n",
                        escapeCsv(extract.getApplicationNumber()),
                        escapeCsv(extract.getCadNumber()),
                        escapeCsv(extract.getParcelAddress()),
                        escapeCsv(extract.getParcelArea()),
                        escapeCsv(extract.getParcelOwner()),
                        escapeCsv(extract.getParcelOwnerDocument()),
                        escapeCsv(extract.getFileName())
                );
            }

            System.out.println("CSV-файл успешно сохранён: " + file.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Ошибка при записи CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
