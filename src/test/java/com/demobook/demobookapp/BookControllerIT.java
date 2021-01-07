package com.demobook.demobookapp;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerIT {

    @LocalServerPort
    int randomServerPost;

    private TestRestTemplate testRestTemplate;

    @BeforeEach
    public void setup() {
        testRestTemplate = new TestRestTemplate();
    }

    @Test
    public void deleteKnownEntityShouldReturn404AfterDeletion() {
        long bookId = 1;
        String baseUrl = "http://localhost:" + randomServerPost;

        ResponseEntity<JsonNode> firstResult = this.testRestTemplate
                .getForEntity(baseUrl + "/api/books/" + bookId, JsonNode.class);

        assertThat(firstResult.getStatusCode(), is(HttpStatus.OK));

        this.testRestTemplate.delete(baseUrl + "/api/books/" + bookId);
        ResponseEntity<JsonNode> secondResult =
                this.testRestTemplate.getForEntity(baseUrl + "/api/books/" + bookId, JsonNode.class);

        assertThat(secondResult.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }
}
