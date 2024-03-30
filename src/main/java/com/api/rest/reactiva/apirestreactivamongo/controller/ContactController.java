package com.api.rest.reactiva.apirestreactivamongo.controller;

import com.api.rest.reactiva.apirestreactivamongo.documents.Contact;
import com.api.rest.reactiva.apirestreactivamongo.respository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/contacts")
    public Flux<Contact> getAll() {
        return contactRepository.findAll();
    }

    @GetMapping(value = "/contacts/{id}")
    public Mono<ResponseEntity<Contact>> getContactById(@PathVariable String id) {
        return contactRepository.findById(id)
                .map(contact -> new ResponseEntity<>(contact, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/contacts/byEmail/{email}")
    public Mono<ResponseEntity<Contact>> getContactByEmail(@PathVariable String email) {
        return contactRepository.findFirstByEmail(email)
                .map(contact -> new ResponseEntity<>(contact, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/contacts")
    public Mono<ResponseEntity<Contact>> save(@RequestBody Contact contact) {
        return contactRepository.insert(contact)
                .map(contactSaved -> new ResponseEntity<>(contactSaved, HttpStatus.ACCEPTED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE));
    }

    @PutMapping("/contacts/{id}")
    public Mono<ResponseEntity<Contact>> update(@RequestBody Contact contact, @PathVariable String id) {
        return contactRepository.findById(id)
                .flatMap(contactUpdated -> {
                    contact.setId(id);
                    return contactRepository.save(contact)
                            .map(contact1 -> new ResponseEntity<>(contact, HttpStatus.ACCEPTED));
                }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "/contacts/{id}")
    public Mono<Void> deleteById(@PathVariable String id) {
        return contactRepository.deleteById(id);
    }

}
