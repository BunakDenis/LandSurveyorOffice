package ua.land.go.landSurveyorOffice;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.land.go.landSurveyorOffice.model.file.ExtractGeoCadastr;
import ua.land.go.landSurveyorOffice.model.parser.file.GeoCadastralExtractParser;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
public class GeoCadastralExtracParserTests {

    private GeoCadastralExtractParser parser;

    private static final String PDF_FOLDER_PATH = "src/test/resources/extract/";

    @BeforeEach
    public void init() {
        File testPdfFolder = new File(PDF_FOLDER_PATH);

        if (testPdfFolder.exists()) {
            System.out.println(PDF_FOLDER_PATH + " is exists!");
            parser = new GeoCadastralExtractParser(testPdfFolder.getAbsolutePath());
        } else {
            System.out.println(PDF_FOLDER_PATH + " is not exists!");
        }
    }

    @Test
    public void testParsePdfFile() {

        //Given
        String expectedApplicationNumber = "9704716262025";
        String expectedCadNumber = "7425883000:02:000:3163";
        String expectedParcelAddress = "Чернігівська область, Корюківський район, Сновська міська рада, " +
                "за межами с. Кучинівка";
        String expectedIntendedPropose = "01.01 Для ведення товарного сільськогосподарського виробництва";
        String expectedParcelArea = "0.5471";
        String expectedParcelOwner = "Кривенко Сергій Григорович";
        String expectedParcelOwnerDocument = "Державний акт від 20.12.2001 ІV-ЧН 027287";
        String expectedFileName = "EXC_81317869_cut.pdf.p7s.pdf";

        parser.parse();

        ExtractGeoCadastr actualExtract = parser.getExtracts().get(0);

        assertEquals(expectedApplicationNumber, actualExtract.getApplicationNumber());
        assertEquals(expectedCadNumber, actualExtract.getCadNumber());
        assertEquals(expectedParcelAddress, actualExtract.getParcelAddress());
        assertEquals(expectedIntendedPropose, actualExtract.getIntendedPropose());
        assertEquals(expectedParcelArea, actualExtract.getParcelArea());
        assertEquals(expectedParcelOwner, actualExtract.getParcelOwner());
        assertEquals(expectedParcelOwnerDocument, actualExtract.getParcelOwnerDocument());
        assertEquals(expectedFileName, actualExtract.getFileName());

    }

}
