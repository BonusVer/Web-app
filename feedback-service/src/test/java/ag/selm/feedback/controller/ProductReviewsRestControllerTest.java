package ag.selm.feedback.controller;

import ag.selm.feedback.entity.ProductReview;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest
@AutoConfigureWebTestClient
class ProductReviewsRestControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Test
    void findProductReviewsByProductId_ReturnsReviews() {
        //when
        this.reactiveMongoTemplate.insertAll(List.of(
                        new ProductReview(UUID.fromString("bd7779c2-cb05-11ee-b5f3-df46f1249898"), 1, 1, "Отзыв №1", "user-1"),
                        new ProductReview(UUID.fromString("be424abc-cb05-11ee-ab16-2b747e61f570"), 1, 3, "Отзыв №2", "user-2"),
                        new ProductReview(UUID.fromString("be77f95a-cb05-11ee-91a3-1bdc94fa9de4"), 1, 5, "Отзыв №3", "user-3")
                ))
                .blockLast();

        //then
        this.webTestClient.mutateWith(mockJwt())
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                      [
                          {"id" : "bd7779c2-cb05-11ee-b5f3-df46f1249898", "productId" : 1, "rating" : 1, "review" : "Отзыв №1", "userId" : "user-1"},
                          {"id" : "be424abc-cb05-11ee-ab16-2b747e61f570", "productId" : 1, "rating" : 3, "review" : "Отзыв №2", "userId" : "user-2"},
                          {"id" : "be77f95a-cb05-11ee-91a3-1bdc94fa9de4", "productId" : 1, "rating" : 5, "review" : "Отзыв №3", "userId" : "user-3"}
                      ]""");


        //
    }

}