package ua.land.go.landSurveyorOffice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
    TODO 1. Сделать рефактор метода processFiles - загрузить файлы на сервер, после загрузки обработать вытяги и обменные файлы
*/

@Controller
@RequestMapping("/v1/GeoCadastrExtractService")
public class MainPageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/process")
    public String processFiles(@RequestParam("extractsPath") String extractsPath,
                               @RequestParam("exchangesPath") String exchangesPath) {
        System.out.println("extractsPath = " + extractsPath);
        System.out.println("exchangesPath = " + exchangesPath);

        try {
            // 1. Получение списка файлов из первой папки
            List<String> extractFiles = listFilesInDirectory(extractsPath);

            // 2. Получение списка файлов из второй папки
            List<String> exchangeFiles = listFilesInDirectory(exchangesPath);

            // 3. Пример обработки (можно добавить свою логику)
            System.out.println("Files in extracts folder: " + extractFiles);
            System.out.println("Files in exchanges folder: " + exchangeFiles);

        } catch (IOException e) {
            System.out.println("Ошибка загрузки файлов" + e.getMessage());
        }
        return "index";
    }

    private List<String> listFilesInDirectory(String directoryPath) throws IOException {
        try (Stream<Path> pathStream = Files.list(Paths.get(directoryPath))) {
            return pathStream
                    .filter(Files::isRegularFile) // Фильтруем только файлы
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }
}