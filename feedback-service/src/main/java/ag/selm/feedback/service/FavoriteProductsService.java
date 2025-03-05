package ag.selm.feedback.service;

import ag.selm.feedback.entity.FavoriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavoriteProductsService {

    Mono<FavoriteProduct> addProductToFavorites(int productId, String userId);

    Mono<Void> removeProductFromFavorites(int productId, String userId);

    Mono<FavoriteProduct> findFavoriteProductByProduct(int productId, String userId);

    Flux<FavoriteProduct> findFavoriteProducts(String userId);
}
