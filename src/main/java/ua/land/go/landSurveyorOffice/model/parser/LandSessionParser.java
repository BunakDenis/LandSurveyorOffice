package ua.land.go.landSurveyorOffice.model.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Парсер вкладки «Реєстрація ЗД → Історія».
 *
 * <p>Теперь соответствует реальному XHR‑запросу браузера:<br>
 * GET /back/parcel_registration/get_data/list?draw=1&start=0&length=10&search[value]=...&custom_filter=active</p>
 */
public class LandSessionParser {

    private static final String BASE = "https://e.land.gov.ua";
    private final RestTemplate rest;
    private final HttpHeaders headers = new HttpHeaders();
    private final ObjectMapper mapper = new ObjectMapper();

    public LandSessionParser(String phpsessid) {
        BasicCookieStore store = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("PHPSESSID", phpsessid);
        cookie.setDomain("e.land.gov.ua");
        cookie.setPath("/");
        store.addCookie(cookie);

        HttpClient client = HttpClients.custom()
                .setDefaultCookieStore(store)
                .build();
        this.rest = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        headers.set(HttpHeaders.USER_AGENT,
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.6422.60 Safari/537.36");
        headers.set(HttpHeaders.ACCEPT, "application/json, text/javascript, */*; q=0.01");
        headers.set("X-Requested-With", "XMLHttpRequest");
        headers.set(HttpHeaders.REFERER, BASE + "/parcel/parcel-registration/history");
        headers.set("Sec-Fetch-Mode", "cors");
    }

    /**
     * @param apps номера заявок
     * @return Map application → имена файлов
     */
    public Map<String, List<String>> fetchFiles(List<String> apps) throws IOException {
        Map<String, List<String>> out = new LinkedHashMap<>();

        for (String app : apps) {
            JsonNode rows = queryApi(app);
            if (rows == null || !rows.isArray() || rows.isEmpty()) {
                System.out.println("Не найдено: " + app);
                continue;
            }

            String rowHtml = rows.get(0).get("action").asText();
            Document doc = Jsoup.parse(rowHtml);
            Element link = doc.selectFirst("a[data-original-title=Завантажити електронний документ]");
            if (link == null) {
                System.out.println("Ссылка отсутствует: " + app);
                continue;
            }
            String href = BASE + link.attr("href");
            String fname = downloadOnce(href);
            out.computeIfAbsent(app, k -> new ArrayList<>()).add(fname);
            System.out.println(app + " → " + fname);
        }
        return out;
    }

    /**
     * Делает GET запрос, идентичный XHR DataTables.
     */
    private JsonNode queryApi(String app) throws IOException {
        // вручную задаём весь query как строку, имитируя XHR запрос браузера
        String query = String.format("draw=1&start=0&length=10&search[value]=%s&search[regex]=false&custom_filter=active",
                app);

        URI uri = UriComponentsBuilder
                .fromHttpUrl(BASE + "/back/parcel_registration/get_data/list?" + query)
                .build(true) // не трогает encoded [ ]
                .toUri();

        ResponseEntity<String> resp = rest.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            System.err.println("HTTP " + resp.getStatusCode());
            return null;
        }
        return mapper.readTree(resp.getBody()).path("data");
    }


    /**
     * Скачивает файл, достаёт имя, удаляет содержимое.
     */
    private String downloadOnce(String href) throws IOException {
        ResponseEntity<byte[]> resp = rest.exchange(href, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);
        String cd = resp.getHeaders().getFirst("Content-Disposition");
        String name = cd != null && cd.contains("filename=") ?
                URLDecoder.decode(cd.substring(cd.indexOf("filename=") + 9).replace("\"", ""), StandardCharsets.UTF_8)
                : "file_" + System.nanoTime();
        Path tmp = Files.createTempFile("land_", "_" + name);
        Files.write(tmp, Objects.requireNonNull(resp.getBody()));
        Files.deleteIfExists(tmp);
        return name;
    }

    public static void main(String[] args) throws Exception {
        LandSessionParser p = new LandSessionParser("5fd9286f46a1892fe138dd109f808404");
        Map<String, List<String>> r = p.fetchFiles(List.of("9702899102025", "9702899482025"));
        r.forEach((k, v) -> System.out.println(k + " => " + v));
    }
}
