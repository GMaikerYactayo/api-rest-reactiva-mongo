package com.api.rest.reactiva.apirestreactivamongo.respository;

import com.api.rest.reactiva.apirestreactivamongo.documents.Contact;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ContactRepository extends ReactiveMongoRepository<Contact, String> {

    Mono<Contact> findFirstByEmail(String email);

}
