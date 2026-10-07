package dev.guillermo.shortener;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;

@Service
public class ShortenerService {
    private final ShortUrlRepository repo;

    public ShortenerService(ShortUrlRepository repo) {
        this.repo = repo;
    }

    /** Only absolute http(s) URLs are accepted. */
    static boolean isValidUrl(String url) {
        try {
            URI uri = URI.create(url);
            return uri.getHost() != null && ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Transactional
    public ShortUrl create(String originalUrl) {
        if (originalUrl == null || !isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("A valid http(s) URL is required");
        }
        ShortUrl entity = new ShortUrl();
        entity.setOriginalUrl(originalUrl);
        entity = repo.save(entity);                  // id is generated here
        entity.setCode(Base62.encode(entity.getId() + 1000)); // offset so codes are never 1 char long
        return repo.save(entity);
    }

    @Transactional
    public Optional<ShortUrl> resolve(String code) {
        Optional<ShortUrl> found = repo.findByCode(code);
        found.ifPresent(ShortUrl::incrementClicks);
        return found;
    }

    @Transactional(readOnly = true)
    public Optional<ShortUrl> stats(String code) {
        return repo.findByCode(code);
    }
}
