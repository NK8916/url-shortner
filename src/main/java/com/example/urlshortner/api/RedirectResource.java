package com.example.urlshortner.api;

import com.example.urlshortner.application.UrlShortnerService;
import com.example.urlshortner.domain.UrlMapping;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/")
public class RedirectResource {
    @Inject
    UrlShortnerService service;

    @GET
    @Path("{alias}")
    public Response redirect(@PathParam("alias") String alias) {
        UrlMapping mapping = service.getByAlias(alias);
        return Response.status(302)
                .location(URI.create(mapping.getOriginalUrl()))
                .build();
    }
}
