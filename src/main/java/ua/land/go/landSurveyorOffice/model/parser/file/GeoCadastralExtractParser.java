package ua.land.go.landSurveyorOffice.model.parser.file;

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

public class GeoCadastralExtractParser implements Parser {
    private String folderPath;
    private List<ExtractGeoCadastr> extracts;

    public GeoCadastralExtractParser(String folderPath) {
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
            return part1 + ", " + part2;
        }
        return null;
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