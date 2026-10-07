package dev.guillermo.shortener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
public class ShortenerController {
    private final ShortenerService service;
    private final String baseUrl;

    public ShortenerController(ShortenerService service, @Value("${app.base-url}") String baseUrl) {
        this.service = service;
        this.baseUrl = baseUrl;
    }

    public record ShortenRequest(String url) {}

    @PostMapping("/api/shorten")
    public ResponseEntity<?> shorten(@RequestBody ShortenRequest req) {
        try {
            ShortUrl s = service.create(req.url());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("code", s.getCode(), "shortUrl", baseUrl + "/" + s.getCode(), "originalUrl", s.getOriginalUrl()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{code:[0-9a-zA-Z]{1,16}}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        return service.resolve(code)
                .map(s -> ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, URI.create(s.getOriginalUrl()).toString()).<Void>build())
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/stats/{code}")
    public ResponseEntity<?> stats(@PathVariable String code) {
        return service.stats(code)
                .<ResponseEntity<?>>map(s -> ResponseEntity.ok(Map.of(
                        "code", s.getCode(), "originalUrl", s.getOriginalUrl(),
                        "clicks", s.getClicks(), "createdAt", s.getCreatedAt().toString())))
                .orElse(ResponseEntity.notFound().build());
    }
}
