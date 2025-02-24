package ag.selm.customer.client;

import ag.selm.customer.entity.FavoriteProduct;
import ag.selm.customer.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.channels.FileChannel;

public interface FavoritesProductsClient {

    Flux<FavoriteProduct> findFavoriteProducts();

    Mono<FavoriteProduct> findFavoriteProductByProduct(int productId);


    Mono<FavoriteProduct> addProductToFavorites(int productId);


    Mono<Void> removeProductFromFavorites(int productId);





}
