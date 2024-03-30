package com.api.rest.reactiva.apirestreactivamongo.functional;

import com.api.rest.reactiva.apirestreactivamongo.documents.Contact;
import com.api.rest.reactiva.apirestreactivamongo.respository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static org.springframework.web.reactive.function.BodyInserters.*;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ContactHandler {

    @Autowired
    private ContactRepository contactRepository;

    private final Mono<ServerResponse> response404 = ServerResponse.notFound().build();
    private final Mono<ServerResponse> response406 = ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).build();

    public Mono<ServerResponse> getContacts(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(contactRepository.findAll(), Contact.class);
    }

    public Mono<ServerResponse> getById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return contactRepository.findById(id)
                .flatMap(contact -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(contact)))
                .switchIfEmpty(response404);
    }

    public Mono<ServerResponse> getByEmail(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable("email");
        return contactRepository.findFirstByEmail(email)
                .flatMap(contact -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(contact)))
                .switchIfEmpty(response404);
    }

    public Mono<ServerResponse> save(ServerRequest serverRequest) {
        Mono<Contact> contactMono = serverRequest.bodyToMono(Contact.class);
        return contactMono.flatMap(contact -> contactRepository.save(contact)
                        .flatMap(contactSaved -> ServerResponse.accepted()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(contactSaved))))
                .switchIfEmpty(response406);
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        Mono<Contact> contactMono = serverRequest.bodyToMono(Contact.class);
        String id = serverRequest.pathVariable("id");
        Mono<Contact> contactUpdated = contactMono.flatMap(contact ->
                contactRepository.findById(id)
                        .flatMap(contactOld -> {
                            contactOld.setName(contact.getName());
                            contactOld.setEmail(contact.getEmail());
                            contactOld.setPhone(contact.getPhone());
                            return contactRepository.save(contactOld);
                        }));

        return contactUpdated.flatMap(contact -> ServerResponse.accepted()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(contact)))
                .switchIfEmpty(response404);
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Void> contactDeleted = contactRepository.deleteById(id);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(contactDeleted, Void.class);
    }

}
