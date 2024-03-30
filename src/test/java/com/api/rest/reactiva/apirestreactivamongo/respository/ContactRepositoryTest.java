package com.api.rest.reactiva.apirestreactivamongo.respository;

import com.api.rest.reactiva.apirestreactivamongo.documents.Contact;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @BeforeAll
    public void save() {
        Contact contact1 = new Contact();
        contact1.setName("test1");
        contact1.setEmail("e1@gmail.com");
        contact1.setPhone("987654321");

        Contact contact2 = new Contact();
        contact2.setName("test2");
        contact2.setEmail("e2@gmail.com");
        contact2.setPhone("123456789");

        Contact contact3 = new Contact();
        contact3.setName("test3");
        contact3.setEmail("e3@gmail.com");
        contact3.setPhone("123459876");

        Contact contact4 = new Contact();
        contact4.setName("test4");
        contact4.setEmail("e4@gmail.com");
        contact4.setPhone("123459836");

        StepVerifier.create(contactRepository.insert(contact1).log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(contactRepository.save(contact2).log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(contactRepository.save(contact3).log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(contactRepository.save(contact4).log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }


    @Test
    @Order(1)
    void findAll() {
        StepVerifier.create(contactRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    @Order(2)
    void findFirstByEmail() {
        StepVerifier.create(contactRepository.findFirstByEmail("e2@gmail.com").log())
                .expectSubscription()
                .expectNextMatches(contact -> contact.getEmail().equals("e2@gmail.com"))
                .verifyComplete();
    }

    @Test
    @Order(3)
    void update() {
        Mono<Contact> contactUpdated = contactRepository.findFirstByEmail("e2@gmail.com")
                .map(contact -> {
                    contact.setPhone("111111111");
                    return contact;
                }).flatMap(contact -> {
                    return contactRepository.save(contact);
                });
        StepVerifier.create(contactUpdated.log())
                .expectSubscription()
                .expectNextMatches(contact -> contact.getPhone().equals("111111111"))
                .verifyComplete();
    }

    @Test
    @Order(4)
    void deleteById() {
        Mono<Void> contactDeleted = contactRepository.findFirstByEmail("e2@gmail.com")
                .flatMap(contact -> {
                    return contactRepository.deleteById(contact.getId());
                }).log();

        StepVerifier.create(contactDeleted)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @Order(5)
    void delete() {
        Mono<Void> contactDeleted = contactRepository.findFirstByEmail("e3@gmail.com")
                .flatMap(contact -> contactRepository.delete(contact)).log();

        StepVerifier.create(contactDeleted)
                .expectSubscription()
                .verifyComplete();
    }

    @AfterAll
    public void clearData() {
        Mono<Void> elementsDeleted = contactRepository.deleteAll();
        StepVerifier.create(elementsDeleted.log())
                .expectSubscription()
                .verifyComplete();
    }
}