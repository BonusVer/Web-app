package ag.selm.feedback.controller;

import ag.selm.feedback.entity.ProductReview;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProductReviewsRestControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        this.reactiveMongoTemplate.insertAll(List.of(
                        new ProductReview(UUID.fromString("bd7779c2-cb05-11ee-b5f3-df46f1249898"), 1, 1, "Отзыв №1", "user-1"),
                        new ProductReview(UUID.fromString("be424abc-cb05-11ee-ab16-2b747e61f570"), 1, 3, "Отзыв №2", "user-2"),
                        new ProductReview(UUID.fromString("be77f95a-cb05-11ee-91a3-1bdc94fa9de4"), 1, 5, "Отзыв №3", "user-3")
                ))
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        this.reactiveMongoTemplate.remove(ProductReview.class).all().block();
    }

    @Test
    void findProductReviewsByProductId_ReturnsReviews() {

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
    }

    @Test
    void createProductReview_RequestIsValid_ReturnsCreatedProductReview() {
        //when
        this.webTestClient.mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "productId" : 1,
                            "rating" : 5,
                            "review" : "Отлично!!!"
                        }""")
        //then
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                        {
                            "productId" : 1,
                            "rating" : 5,
                            "review" : "Отлично!!!",
                            "userId": "user-tester"
                        }""").jsonPath("$.id").exists()
                .consumeWith(document("feedback/product_reviews/create_product_review" ,
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").type("int").description("Идентификатор товара"),
                                fieldWithPath("rating").type("int").description("Оценка товара"),
                                fieldWithPath("review").type("string").description("Отзыв товара")
                        ),
                        responseFields(
                                fieldWithPath("id").type("uuid").description("Идентификатор отзвыа"),
                                fieldWithPath("productId").type("int").description("Идентификатор товара"),
                                fieldWithPath("rating").type("int").description("Оценка товара"),
                                fieldWithPath("review").type("string").description("Отзыв товара"),
                                fieldWithPath("userId").type("string").description("Идентификатор пользователя")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION)
                                        .description("Ссылка на созданный отзыв о товаре")

                        )
                        ));
    }

    @Test
    void createProductReview_RequestIsInvalid_ReturnsBadRequest() {
        //when
        this.webTestClient.mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "productId" : null,
                            "rating" : -2,
                            "review" : "Отлично!!!"
                        }""")
                //then
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().doesNotExist(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                            "errors" : [
                              "Товар не указан",
                              "Оценка меньше 1"
                            ]
                        }""");
    }

    @Test
    void findProductReviewsByProductId_UserIsNotAuthenticated_ReturnsNotAuthorized() {

        this.webTestClient
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

}