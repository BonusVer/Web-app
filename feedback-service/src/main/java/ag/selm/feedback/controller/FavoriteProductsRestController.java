package ag.selm.feedback.controller;

import ag.selm.feedback.controller.payload.NewFavoriteProductPayload;
import ag.selm.feedback.entity.FavoriteProduct;
import ag.selm.feedback.service.FavoriteProductsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("feedback-api/favorite-products")
public class FavoriteProductsRestController {

    private final FavoriteProductsService favoriteProductsService;

    @GetMapping
    public Flux<FavoriteProduct> findFavoriteProducts() {
        return this.favoriteProductsService.findFavoriteProducts();
    }

    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavoriteProduct> findFavoriteProductByProductId(@PathVariable("productId") int productId) {
        return this.favoriteProductsService.findFavoriteProductByProduct(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<FavoriteProduct>> addProductToFavorites(
            @Valid @RequestBody Mono<NewFavoriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder) {
        return payloadMono
                .flatMap(payload -> this.favoriteProductsService.addProductToFavorites(payload.productId()))
                .map(favoriteProduct -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("feedback-api/favorite-products/{id}")
                                .build(favoriteProduct.getId()))
                .body(favoriteProduct));
    }

    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavorites(@PathVariable("productId") int productId) {
        return this.favoriteProductsService.removeProductFromFavorites(productId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
