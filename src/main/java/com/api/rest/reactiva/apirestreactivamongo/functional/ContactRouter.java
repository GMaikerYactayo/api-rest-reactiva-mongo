package com.api.rest.reactiva.apirestreactivamongo.functional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ContactRouter {

    @Bean
    public RouterFunction<ServerResponse> routeContact(ContactHandler contactHandler) {
        return RouterFunctions
                .route(GET("/functional/contacts/"), contactHandler::getContacts)
                .andRoute(GET("/functional/contacts/{id}"), contactHandler::getById)
                .andRoute(GET("/functional/contacts/byEmail/{email}"), contactHandler::getByEmail)
                .andRoute(POST("/functional/contacts/"), contactHandler::save)
                .andRoute(PUT("/functional/contacts/{id}"), contactHandler::update)
                .andRoute(DELETE("/functional/contacts/{id}"), contactHandler::delete);
    }

}
