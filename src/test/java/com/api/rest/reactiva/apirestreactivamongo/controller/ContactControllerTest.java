package com.api.rest.reactiva.apirestreactivamongo.controller;

import com.api.rest.reactiva.apirestreactivamongo.documents.Contact;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContactControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private Contact contactSaved;

    @Test
    @Order(0)
    void save() {
        Flux<Contact> contactFlux = webTestClient.post()
                .uri("/api/v1/contacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new Contact("test1", "x@gmail.com", "987654")))
                .exchange()
                .expectStatus().isAccepted()
                .returnResult(Contact.class).getResponseBody()
                .log();

        contactFlux.next().subscribe(contact -> {
            this.contactSaved = contact;
        });

        Assertions.assertNotNull(contactSaved);
    }

    @Test
    @Order(2)
    void getAll() {
        Flux<Contact> contactsFlux = webTestClient.get()
                .uri("/api/v1/contacts")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult(Contact.class).getResponseBody()
                .log();

        StepVerifier.create(contactsFlux)
                .expectSubscription()
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    @Order(1)
    void getContactByEmail() {
        Flux<Contact> contactFlux = webTestClient.get()
                .uri("/api/v1/contacts/byEmail/{email}", "x@gmail.com")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Contact.class).getResponseBody()
                .log();

        StepVerifier.create(contactFlux)
                .expectSubscription()
                .expectNextMatches(contact -> contact.getEmail().equals("x@gmail.com"))
                .verifyComplete();
    }

    @Test
    @Order(3)
    void update() {
        Contact updatedContact = new Contact("wtc", "wtc@gmail.com", "11111");
        updatedContact.setId(contactSaved.getId());
        Flux<Contact> contactFlux = webTestClient.put()
                .uri("/api/v1/contacts/{id}", contactSaved.getId())
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(updatedContact))
                .exchange()
                .returnResult(Contact.class).getResponseBody()
                .log();

        StepVerifier.create(contactFlux)
                .expectSubscription()
                .expectNextMatches(contact -> contact.getEmail().equals("wtc@gmail.com"))
                .verifyComplete();
    }

    @Test
    @Order(4)
    void deleteById() {
        Flux<Void> flux = webTestClient.delete()
                .uri("/api/v1/contacts/{id}", contactSaved.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult(Void.class).getResponseBody();

        StepVerifier.create(flux)
                .expectSubscription()
                .verifyComplete();
    }
}