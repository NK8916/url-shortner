package com.example.urlshortner.application;

import com.example.urlshortner.domain.GlobalCounter;
import com.example.urlshortner.domain.UrlMapping;
import com.example.urlshortner.infrastructure.UrlShortnerRepository;
import com.example.urlshortner.utils.Base62IdCodec;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.logging.Logger;

@ApplicationScoped
public class UrlShortnerService {
    private final UrlShortnerRepository urlShortnerRepository;
    private final GlobalCounter globalCounter;

    public UrlShortnerService(UrlShortnerRepository urlShortnerRepository, GlobalCounter globalCounter) {
        this.urlShortnerRepository = urlShortnerRepository;
        this.globalCounter = globalCounter;
    }

    private String createUniqueAlias() {
        Logger.getLogger("UrlShortnerService").info("Generating unique alias using global counter");
        long id = globalCounter.nextId();
        Logger.getLogger("UrlShortnerService").info("Generated unique ID: " + id + " from global counter");
        return Base62IdCodec.encode(id);
    }

    public UrlMapping getByAlias(String alias) {
        return urlShortnerRepository.getByAlias(alias);
    }

    public UrlMapping createShortenedUrl(String userId,String originalUrl) {
        Logger.getLogger("UrlShortnerService").info("Creating shortened URL for originalUrl: " + originalUrl);
        String alias = createUniqueAlias();
        Logger.getLogger("UrlShortnerService").info("Generated alias: " + alias + " for originalUrl: " + originalUrl);
        UrlMapping urlMapping=new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setUserId(userId);
        urlMapping.setCreatedAt(System.currentTimeMillis());
        urlMapping.setUpdatedAt(System.currentTimeMillis());
        urlMapping.setAlias(alias);
        urlMapping.setEnable(true);
        Logger.getLogger("UrlShortnerService").info("Generated alias: " + urlMapping.getAlias() + " for originalUrl: " + originalUrl);
        urlShortnerRepository.save(urlMapping);
        return urlMapping;
    }
}
