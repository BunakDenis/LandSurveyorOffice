package ua.land.go.landSurveyorOffice.model.parser.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import ua.land.go.landSurveyorOffice.model.file.ExtractGeoCadastr;
import ua.land.go.landSurveyorOffice.model.parser.Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GeoCadastralExtractParser implements Parser {
    private String folderPath;
    private List<ExtractGeoCadastr> extracts;

    public GeoCadastralExtractParser(String folderPath) {
        log.debug("GeoCadastralExtractParser init with folder path {}", folderPath);
        this.folderPath = folderPath;
        this.extracts = new ArrayList<>();
    }

    @Override
    public void parse() {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) { // Проверка на null, если папка пуста
            for (File file : files) {
                try {
                    PDDocument document = PDDocument.load(new FileInputStream(file.getAbsolutePath()));
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(document);

                    ExtractGeoCadastr extract = new ExtractGeoCadastr();

                    extract.setApplicationNumber(extractApplicationNumber(text));
                    extract.setCadNumber(extractCadNumber(text));
                    extract.setParcelAddress(extractParcelAddress(text));
                    extract.setIntendedPropose(extractIntendedPropose(text));
                    extract.setParcelArea(extractParcelArea(text));
                    extract.setParcelOwner(extractParcelOwner(text));
                    extract.setParcelOwnerDocument(extractParcelOwnerDocument(text));
                    extract.setFileName(file.getName());

                    extracts.add(extract);

                    document.close();
                    System.out.println("Successfully parsed: " + file.getName()); // Логирование успешной обработки
                } catch (IOException e) {
                    System.err.println("Error parsing PDF " + file.getName() + ": " + e.getMessage());
                }
            }
        } else {
            System.err.println("Folder is empty or does not exist: " + folderPath);
        }
    }

    private String extractApplicationNumber(String text) {
        // Ищем паттерн: "Надано на заяву (запит)" + любые символы (включая переносы строк) + ЗВ-номер
        Pattern pattern = Pattern.compile(
                "Надано на заяву \\(запит\\).*?\\s+ЗВ-(\\d+)",
                Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private String extractCadNumber(String text) {
        Pattern pattern = Pattern.compile("Кадастровий номер земельної ділянки\\s+(\\d+:\\d+:\\d+:\\d+)", Pattern.CANON_EQ);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private String extractParcelAddress(String text) {
        Pattern pattern = Pattern.compile(
                "одиниця\\)\\s+([^\\n]+)\\n\\s*([^\\n]+)",
                Pattern.MULTILINE
        );

        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String part1 = matcher.group(1).replaceAll("\\s+", " ").trim();
            String part2 = matcher.group(2).replaceAll("\\s+", " ").trim();
            return part1 + " " + part2;
        }
        return null;
    }

    /**
     * Этот метод извлекает вид целевого назначения земельного участка.
     * Он ищет блок текста, который начинается с "Вид цільового призначення земельної ділянки"
     * и заканчивается перед "Обліковий номер масиву".
     * Флаг Pattern.DOTALL необходим, так как искомый текст может быть многострочным.
     *
     * @param text - содержимое pdf файла "Витягя з ДЗК"
     *             Если pdf файл "Витягя з ДЗК" распознан, и в файле есть целевое назначение участка метод
     * @return String extractedText
     *
     */
    private String extractIntendedPropose(String text) {
        // Регекс учитывает перенос строки между "Вид цільового призначення" и "земельної ділянки"
        Pattern pattern = Pattern.compile(
                "Вид цільового призначення\\s*земельної\\s*ділянки\\s*(\\d{2}\\.\\d{2}.*?)(?=Обліковий номер|Площа земельної)",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            // Нормализуем пробелы и переносы
            return matcher.group(1).replaceAll("\\s+", " ").trim();
        }

        return null; // ничего не нашли
    }

    private String extractParcelArea(String text) {
        Pattern pattern = Pattern.compile("Площа земельної ділянки, гектарів\\s+([0-9.]+)", Pattern.CANON_EQ);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private String extractParcelOwner(String text) {
        Pattern pattern = Pattern.compile("\\(за наявності\\)/найменування\\s+([^\\n-]+)", Pattern.CANON_EQ);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).replaceAll("\\s+", " ").trim() : null;
    }

    private String extractParcelOwnerDocument(String text) {
        Pattern pattern = Pattern.compile("Документ, що посвідчує право\\s+([^\\n]+)");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    public List<ExtractGeoCadastr> getExtracts() {
        return Collections.unmodifiableList(extracts);
    }

}