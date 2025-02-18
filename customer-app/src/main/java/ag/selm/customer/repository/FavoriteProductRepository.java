package ag.selm.customer.repository;

import ag.selm.customer.entity.FavoriteProduct;
import reactor.core.publisher.Mono;

public interface FavoriteProductRepository {

    Mono<FavoriteProduct> save(FavoriteProduct favoriteProduct);

    Mono<Void> deleteByProductId(int productId);
}
