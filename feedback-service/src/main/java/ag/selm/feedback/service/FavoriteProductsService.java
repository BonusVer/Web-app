package ag.selm.feedback.service;

import ag.selm.feedback.entity.FavoriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavoriteProductsService {

    Mono<FavoriteProduct> addProductToFavorites(int productId);

    Mono<Void> removeProductFromFavorites(int productId);

    Mono<FavoriteProduct> findFavoriteProductByProduct(int productId);

    Flux<FavoriteProduct> findFavoriteProducts();
}
