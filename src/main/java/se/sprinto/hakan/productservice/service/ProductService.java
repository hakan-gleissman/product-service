package se.sprinto.hakan.productservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se.sprinto.hakan.productservice.dto.ProductRequestDto;
import se.sprinto.hakan.productservice.dto.ProductResponseDto;
import se.sprinto.hakan.productservice.dto.StockDecreaseItemRequest;
import se.sprinto.hakan.productservice.model.Product;
import se.sprinto.hakan.productservice.repository.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDto create(ProductRequestDto request) {
        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock()
        );

        return toResponse(productRepository.save(product));
    }

    public List<ProductResponseDto> findAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponseDto findById(Long id) {
        return toResponse(findProductById(id));
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public List<ProductResponseDto> decreaseStock(List<StockDecreaseItemRequest> requestedProducts) {
        List<Long> productIds = requestedProducts.stream()
                .map(StockDecreaseItemRequest::getProductId)
                .toList();

        Map<Long, Product> productsById = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (StockDecreaseItemRequest requestedProduct : requestedProducts) {
            Product product = productsById.get(requestedProduct.getProductId());
            if (product == null) {
                throw new ResponseStatusException(NOT_FOUND, "Product not found: " + requestedProduct.getProductId());
            }
            if (product.getStock() < requestedProduct.getQuantity()) {
                throw new ResponseStatusException(BAD_REQUEST, "Not enough stock for product: " + product.getId());
            }
        }

        for (StockDecreaseItemRequest requestedProduct : requestedProducts) {
            Product product = productsById.get(requestedProduct.getProductId());
            product.setStock(product.getStock() - requestedProduct.getQuantity());
        }

        return requestedProducts.stream()
                .map(requestedProduct -> toResponse(productsById.get(requestedProduct.getProductId())))
                .toList();
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found: " + id));
    }

    private ProductResponseDto toResponse(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
    }
}
