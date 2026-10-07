package dev.guillermo.shortener;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "short_urls")
public class ShortUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(unique = true, length = 16)
    private String code;

    @Column(nullable = false)
    private long clicks;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public long getClicks() { return clicks; }
    public void incrementClicks() { this.clicks++; }
    public Instant getCreatedAt() { return createdAt; }
}
