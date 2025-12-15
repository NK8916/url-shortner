package com.example.urlshortner.application;

import com.example.urlshortner.domain.GlobalCounter;
import com.example.urlshortner.domain.UrlMapping;
import com.example.urlshortner.infrastructure.UrlShortnerRepository;
import com.example.urlshortner.utils.Base62IdCodec;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UrlShortnerService {
    private final UrlShortnerRepository urlShortnerRepository;
    private final GlobalCounter globalCounter;

    public UrlShortnerService(UrlShortnerRepository urlShortnerRepository, GlobalCounter globalCounter) {
        this.urlShortnerRepository = urlShortnerRepository;
        this.globalCounter = globalCounter;
    }

    private String createUniqueAlias() {
        long id = globalCounter.nextId();
        return Base62IdCodec.encode(id);
    }

    public UrlMapping getByAlias(String alias) {
        return urlShortnerRepository.getByAlias(alias);
    }

    public UrlMapping createShortenedUrl(String userId,String originalUrl) {
        String alias = createUniqueAlias();
        UrlMapping urlMapping=new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setUserId(userId);
        urlMapping.setCreatedAt(System.currentTimeMillis());
        urlMapping.setUpdatedAt(System.currentTimeMillis());
        urlMapping.setAlias(alias);
        urlMapping.setEnable(true);
        urlShortnerRepository.save(urlMapping);
        return urlMapping;
    }
}
