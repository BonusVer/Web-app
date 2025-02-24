package ag.selm.customer.config;

import ag.selm.customer.client.WebClientFavoritesProductsClient;
import ag.selm.customer.client.WebClientProductReviewsClient;
import ag.selm.customer.client.WebClientProductsClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    public WebClientProductsClient webClientProductsClient(
            @Value("${selmag.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUrl) {
        return new WebClientProductsClient(WebClient.builder()
                .baseUrl(catalogueBaseUrl)
                .build());
    }

    @Bean
    public WebClientFavoritesProductsClient webClientFavoritesProductsClient(
            @Value("${selmag.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl) {
        return new WebClientFavoritesProductsClient(WebClient.builder()
                .baseUrl(feedbackBaseUrl)
                .build());
    }


    @Bean
    public WebClientProductReviewsClient webClientProductReviewsClient(
            @Value("${selmag.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl) {
        return new WebClientProductReviewsClient(WebClient.builder()
                .baseUrl(feedbackBaseUrl)
                .build());
    }
}
