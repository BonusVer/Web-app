package ag.selm.feedback.controller;

import ag.selm.feedback.controller.payload.NewFavoriteProductPayload;
import ag.selm.feedback.entity.FavoriteProduct;
import ag.selm.feedback.service.FavoriteProductsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
    public Flux<FavoriteProduct> findFavoriteProducts(Mono<JwtAuthenticationToken> authenticationTokenMono) {
        return authenticationTokenMono.flatMapMany( token -> this.favoriteProductsService.findFavoriteProducts(token.getToken().getSubject()));
    }

    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavoriteProduct> findFavoriteProductByProductId(@PathVariable("productId") int productId,
                                                                Mono<JwtAuthenticationToken> authenticationTokenMono) {
        return authenticationTokenMono.flatMap(token ->this.favoriteProductsService.findFavoriteProductByProduct(productId, token.getToken().getSubject()));
    }

    @PostMapping
    public Mono<ResponseEntity<FavoriteProduct>> addProductToFavorites(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @Valid @RequestBody Mono<NewFavoriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder) {
        return Mono.zip(authenticationTokenMono, payloadMono)
                .flatMap(tuple ->
                        this.favoriteProductsService.addProductToFavorites(tuple.getT2().productId(), tuple.getT1().getToken().getSubject()))
                .map(favoriteProduct -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("feedback-api/favorite-products/{id}")
                                .build(favoriteProduct.getId()))
                .body(favoriteProduct));
    }

    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavorites(@PathVariable("productId") int productId, Mono<JwtAuthenticationToken> authenticationTokenMono) {
        return authenticationTokenMono.flatMap(token ->
                        this.favoriteProductsService.removeProductFromFavorites(productId, token.getToken().getSubject()))
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
