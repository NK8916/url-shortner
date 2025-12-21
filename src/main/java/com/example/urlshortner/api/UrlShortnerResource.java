package com.example.urlshortner.api;

import com.example.urlshortner.application.UrlShortnerService;
import com.example.urlshortner.domain.UrlMapping;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/shorten")
public class UrlShortnerResource {
    @Inject
    UrlShortnerService service;

    public static class UrlShortenRequest {
        public String userId;
        public String originalUrl;
    }

    public static class UrlShortenResponse {
        public String alias;
        public String originalUrl;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response shorten(UrlShortenRequest req) {
        System.out.println("Received originalUrl: " + req.originalUrl);
        if (req == null || req.originalUrl == null || req.originalUrl.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("originalUrl is required")
                    .build();
        }
        UrlMapping mapping = service.createShortenedUrl(req.userId, req.originalUrl);
        System.out.println("Created mapping: alias=" + mapping.getAlias() + ", originalUrl=" + mapping.getOriginalUrl());
        UrlShortenResponse resp = new UrlShortenResponse();
        resp.alias = mapping.getAlias();
        System.out.println("alias: " + resp.alias);
        resp.originalUrl = mapping.getOriginalUrl();
        return Response.ok(resp).build();
    }

    @GET
    @Path("/{alias}")
    public Response getOriginalUrl(@PathParam("alias") String alias) {
        UrlMapping mapping = service.getByAlias(alias);
        if (mapping == null || !mapping.isEnable()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapping.getOriginalUrl()).build();
    }

}


