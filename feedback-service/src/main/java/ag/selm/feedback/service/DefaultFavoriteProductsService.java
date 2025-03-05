package ag.selm.feedback.service;

import ag.selm.feedback.entity.FavoriteProduct;
import ag.selm.feedback.repository.FavoriteProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultFavoriteProductsService implements FavoriteProductsService {

    private final FavoriteProductRepository favoriteProductRepository;

    @Override
    public Mono<FavoriteProduct> addProductToFavorites(int productId, String userId) {
        return this.favoriteProductRepository.save(new FavoriteProduct(UUID.randomUUID(), productId, userId));
    }

    @Override
    public Mono<Void> removeProductFromFavorites(int productId, String userId) {
        return this.favoriteProductRepository.deleteByProductIdAndUserId(productId, userId);
    }

    @Override
    public Mono<FavoriteProduct> findFavoriteProductByProduct(int productId, String userId) {
        return this.favoriteProductRepository.findByProductIdAndUserId(productId, userId);
    }

    @Override
    public Flux<FavoriteProduct> findFavoriteProducts(String userId) {
        return this.favoriteProductRepository.findAllByUserId(userId);
    }
}
