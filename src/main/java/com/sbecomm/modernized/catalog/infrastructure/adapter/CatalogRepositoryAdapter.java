package com.sbecomm.modernized.catalog.infrastructure.adapter;

import com.sbecomm.modernized.catalog.domain.model.Category;
import com.sbecomm.modernized.catalog.domain.model.Product;
import com.sbecomm.modernized.catalog.domain.model.ProductId;
import com.sbecomm.modernized.catalog.domain.repository.CatalogRepository;
import com.sbecomm.modernized.catalog.infrastructure.entity.CategoryEntity;
import com.sbecomm.modernized.catalog.infrastructure.entity.ProductEntity;
import com.sbecomm.modernized.common.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CatalogRepositoryAdapter implements CatalogRepository {

    private final CategoryJpaRepository categoryJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    public CatalogRepositoryAdapter(CategoryJpaRepository categoryJpaRepository, ProductJpaRepository productJpaRepository) {
        this.categoryJpaRepository = categoryJpaRepository;
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Optional<Category> findCategoryById(String id) {
        return categoryJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Category> findAllCategories() {
        return categoryJpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Category saveCategory(Category category) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(category.getId());
        entity.setName(category.getName());
        entity.setDescription(category.getDescription());
        return toDomain(categoryJpaRepository.save(entity));
    }

    @Override
    public Optional<Product> findProductById(String id) {
        return productJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public PagedResponse<Product> findProductsByCategory(String categoryId, int page, int size) {
        Page<ProductEntity> productPage = productJpaRepository.findByCategoryId(categoryId, PageRequest.of(page, size));
        return toPagedResponse(productPage);
    }

    @Override
    public PagedResponse<Product> findAllProducts(int page, int size) {
        Page<ProductEntity> productPage = productJpaRepository.findAll(PageRequest.of(page, size));
        return toPagedResponse(productPage);
    }

    @Override
    public Product saveProduct(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId().value());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setStockQuantity(product.getStockQuantity());
        
        CategoryEntity catEntity = new CategoryEntity();
        catEntity.setId(product.getCategory().getId());
        catEntity.setName(product.getCategory().getName());
        catEntity.setDescription(product.getCategory().getDescription());
        
        entity.setCategory(catEntity);
        
        return toDomain(productJpaRepository.save(entity));
    }

    @Override
    public void deleteProduct(String id) {
        productJpaRepository.deleteById(id);
    }

    private Category toDomain(CategoryEntity entity) {
        return new Category(entity.getId(), entity.getName(), entity.getDescription());
    }

    private Product toDomain(ProductEntity entity) {
        Category category = toDomain(entity.getCategory());
        return new Product(new ProductId(entity.getId()), entity.getName(), entity.getDescription(), 
                           entity.getPrice(), entity.getStockQuantity(), category);
    }

    private PagedResponse<Product> toPagedResponse(Page<ProductEntity> page) {
        List<Product> content = page.getContent().stream()
                .map(this::toDomain)
                .toList();
        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
