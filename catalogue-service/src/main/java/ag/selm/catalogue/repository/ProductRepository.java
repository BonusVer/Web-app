package ag.selm.catalogue.repository;

import ag.selm.catalogue.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    @Query(name = "Product.findAllByTitleLikeIgnoringCase", nativeQuery = true)

    Iterable<Product> findAllByTitleLikeIgnoreCase(@Param("filter") String filter);

}
