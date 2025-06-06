package ag.selm.manager.controller;

import ag.selm.manager.client.BadRequestException;
import ag.selm.manager.client.ProductsRestClient;
import ag.selm.manager.controller.payload.NewProductPayload;
import ag.selm.manager.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("модульныые тесты ProductsController")
class ProductsControllerTest {

    @Mock
    ProductsRestClient productsRestClient;

    @InjectMocks
    ProductsController controller;

    @Test
    @DisplayName("CreateProduct создаст новый товар и перенаправит на стрраницу товара")
    void createProduct_RequestIsValid_RedirectionToProductPage() {
        //given
        var payload = new NewProductPayload("Новый товар","Описание нового товара");
        var model = new ConcurrentModel();

        doReturn(new Product(1, "Новый товар","Описание нового товара"))
                .when(this.productsRestClient)
                .createProduct("Новый товар","Описание нового товара");

        //when
        var result = this.controller.createProduct(payload, model);

        //then
        assertEquals("redirect:/catalogue/products/1", result);

        verify(this.productsRestClient).createProduct("Новый товар","Описание нового товара");
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    @DisplayName("createProduct вернет страницу с ошибками, если запрос не валидный")
    void createProduct_RequestIsInvalid_ReturnsFormWithErrors() {

        //given
        var payload = new NewProductPayload(" ", null);
        var model = new ConcurrentModel();

        doThrow(new BadRequestException(List.of("Ошибка 1", "Ошибка 2")))
                .when(this.productsRestClient)
                .createProduct(" ", null);


        //when
        var result = this.controller.createProduct(payload, model);

        //then

        assertEquals("catalogue/products/new_product", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Ошибка 1", "Ошибка 2"), model.getAttribute("errors"));

        verify(productsRestClient).createProduct(" ", null);
        verifyNoMoreInteractions(productsRestClient);
    }
}