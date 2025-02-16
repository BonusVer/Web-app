package ag.selm.catalogue.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ProductsRestControllerIT {

    @Autowired
    MockMvc mvc;

    @Test
    @Sql("/sql/products.sql")
    void findProducts_ReturnsProductList() throws Exception {
        //given

        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products")
                .param("filter", "товар")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));

        //when

        this.mvc.perform(requestBuilder)

        //then

                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                  {"id" :  1, "title" :  "Товар 1", "details" : "Описание1"},
                                  {"id" :  3, "title" :  "Товар 3", "details" : "Описание3"}
                                ]""")
                );
    }
  
}