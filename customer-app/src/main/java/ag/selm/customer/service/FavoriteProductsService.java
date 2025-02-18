package ag.selm.customer.service;

import ag.selm.customer.entity.FavoriteProduct;
import reactor.core.publisher.Mono;

public interface FavoriteProductsService {

    Mono<FavoriteProduct> addProductToFavorites(int productId);

    Mono<Void> removeProductFromFavorites(int productId);
}
