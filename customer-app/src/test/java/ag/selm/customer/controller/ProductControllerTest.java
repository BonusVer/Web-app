package ag.selm.customer.controller;

import ag.selm.customer.client.FavoritesProductsClient;
import ag.selm.customer.client.ProductReviewsClient;
import ag.selm.customer.client.ProductsClient;
import ag.selm.customer.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductsClient productsClient;
    @Mock
    FavoritesProductsClient favoritesProductsClient;
    @Mock
    ProductReviewsClient productReviewsClient;

    @InjectMocks
    ProductController controller;

    @Test
    void loadProduct_ProductExists_ReturnNotEmptyMono() {
        //given
        var product = new Product(1, "Товар №1", "Описание товара №1");
        doReturn(Mono.just(product)).when(this.productsClient).findProduct(1);

        //when
        StepVerifier.create(this.controller.loadProduct(1))

        //then
                .expectNext(new Product(1, "Товар №1", "Описание товара №1"))
                .expectComplete()
                .verify();


        verify(this.productsClient).findProduct(1);
        verifyNoMoreInteractions(this.productsClient);
        verifyNoInteractions(this.favoritesProductsClient, this.productReviewsClient);

    }

    @Test
    void loadProduct_ProductDoesNotExists_ReturnsMonoWithNoSuchElementException() {
        //given

        doReturn(Mono.empty()).when(this.productsClient).findProduct(1);

        //when
        StepVerifier.create(this.controller.loadProduct(1))

                //then
                .expectErrorMatches(exception -> exception instanceof NoSuchElementException e &&
                        e.getMessage().equals("customer.products.error.not_found"))
                .verify();


        verify(this.productsClient).findProduct(1);
        verifyNoMoreInteractions(this.productsClient);
        verifyNoInteractions(this.favoritesProductsClient, this.productReviewsClient);

    }


    @Test
    @DisplayName("Исключение NoSuchElementException должно быть транслировано в страницу errors/404")
    void handleNoSuchElementException_ReturnsErrors404() {
        //given
        var exception = new NoSuchElementException("Товар не найден");
        var model = new ConcurrentModel();
        var response = new MockServerHttpResponse();
        //when
        var result = this.controller.handleNoSuchElementException(exception, model, response);
        //then
        assertEquals("errors/404", result);
        assertEquals("Товар не найден", model.getAttribute("error"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}