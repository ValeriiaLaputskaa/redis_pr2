package org.example.redis_pr2.service;

import lombok.RequiredArgsConstructor;
import org.example.redis_pr2.entity.Product;
import org.example.redis_pr2.payload.user.CreateProductRequest;
import org.example.redis_pr2.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final RedisCacheService redisCacheService;
    private final ExternalProductService externalProductService;
    private final ProductRepository productRepository;

    public Product getProductById(Long id) {
        String cacheKey = "product:" + id;
        Product cachedUser = (Product) redisCacheService.get(cacheKey);

        if (cachedUser != null) {
            return cachedUser;
        }

        Product externalProduct = externalProductService.fetchProductById(id);
        if (externalProduct != null) {
            redisCacheService.save(cacheKey, externalProduct);
        }

        return externalProduct;
    }

    public Product createProduct(CreateProductRequest createProductRequest) {
        Product product = Product.builder()
                .title(createProductRequest.title())
                .description(createProductRequest.description())
                .price(createProductRequest.price())
                .build();
        Product savedProduct = productRepository.save(product);
        redisCacheService.save("product:" + savedProduct.getId(), savedProduct);
        return savedProduct;
    }

    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
        redisCacheService.delete("product:" + id);
    }
}

