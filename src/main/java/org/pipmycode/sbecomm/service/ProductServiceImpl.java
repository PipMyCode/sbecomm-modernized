package org.pipmycode.sbecomm.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pipmycode.sbecomm.exceptions.ResourceNotFoundException;
import org.pipmycode.sbecomm.model.Category;
import org.pipmycode.sbecomm.model.Product;
import org.pipmycode.sbecomm.payload.ProductDTO;
import org.pipmycode.sbecomm.payload.ProductResponse;
import org.pipmycode.sbecomm.repository.CategoryRepository;
import org.pipmycode.sbecomm.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    @Value( "${project.image.path}")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Product product = new Product();
        product.setProductName(productDTO.productName());
        product.setProductDescription(productDTO.productDescription());
        product.setQuantity(productDTO.quantity());
        product.setPrice(productDTO.price());
        product.setDiscountedPrice(productDTO.discountedPrice());
        product.setImageUrl("default.png");
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return mapToDTO(savedProduct);
    }

    @Override
    public ProductResponse getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);

        return convertToProductResponse(productPage);
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);
        }

        Page<Product> productPage = productRepository.findByCategory_CategoryId(categoryId, pageable);
        return convertToProductResponse(productPage);
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword, Pageable pageable) {
        Page<Product> productPage = productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
        return convertToProductResponse(productPage);
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        existingProduct.setProductName(productDTO.productName());
        existingProduct.setProductDescription(productDTO.productDescription());
        existingProduct.setQuantity(productDTO.quantity());
        existingProduct.setPrice(productDTO.price());
        existingProduct.setDiscountedPrice(productDTO.discountedPrice());

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToDTO(updatedProduct);
    }


    @Override
    @Transactional
    public ProductDTO updateProductImage(Long productId, MultipartFile image) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

      try {
          String fileName = fileService.uploadImage(path, image);

          product.setImageUrl(fileName);
      } catch (IOException e) {
          throw new RuntimeException("Error occurred while saving the image to the local drive.");
      }
        return mapToDTO(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        productRepository.delete(product);
    }

    @Override
    public Long getProductCount() {
        return productRepository.count();
    }




    private ProductDTO mapToDTO(Product product) {
        return new ProductDTO(
                product.getProductId(),
                product.getProductName(),
                product.getProductDescription(),
                product.getQuantity(),
                product.getPrice(),
                product.getDiscountedPrice(),
                product.getImageUrl(),
                product.getCategory().getCategoryId()
        );
    }

    private ProductResponse convertToProductResponse(Page<Product> productPage) {
        List<ProductDTO> productDTOs = productPage.getContent().stream()
                .map(this::mapToDTO)
                .toList();

        return new ProductResponse(
                productDTOs,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }
}