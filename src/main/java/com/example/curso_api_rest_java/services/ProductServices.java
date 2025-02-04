package com.example.curso_api_rest_java.services;

import com.example.curso_api_rest_java.controllers.ProductController;
import com.example.curso_api_rest_java.dto.ProductDTO;
import com.example.curso_api_rest_java.exceptions.ResourceNotFoundException;
import com.example.curso_api_rest_java.model.Product;
import com.example.curso_api_rest_java.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ProductServices {

    @Autowired
    ProductRepository repository;

    public Page<ProductDTO> findAll(String search, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            return repository.findByNameContainingIgnoreCase(search, pageable)
                    .map(this::toProductDTO);
        } else {
            return repository.findAll(pageable)
                    .map(this::toProductDTO);
        }
    }

    public ProductDTO findById(Long id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Records found for this id"));

        return toProductDTO(entity);
    }

    public ProductDTO create(ProductDTO productDTO) {
        var entity = toProductEntity(productDTO);
        entity = repository.save(entity);

        return toProductDTO(entity);
    }

    public ProductDTO update(Long id, ProductDTO productDTO) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Records found for this id"));

        entity.setName(productDTO.getName());
        entity.setDescription(productDTO.getDescription());
        entity.setAvailable(productDTO.getAvailable());
        entity.setPrice(productDTO.getPrice());

        entity = repository.save(entity);
        return toProductDTO(entity);
    }

    public void delete(Long id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Records found for this id"));

        repository.delete(entity);
    }


    public ProductDTO updateImageUrl(Long id, String imagePath) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Records found for this id"));

        entity.setImageUrl(imagePath);
        entity = repository.save(entity);

        return toProductDTO(entity);
    }

    public ProductDTO updatePartial(Long id, Map<String, Object> updates) {
        Product existingProduct = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Product.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, existingProduct, value);
            } else {
                throw new IllegalArgumentException("Update not allowed for field: " + key);
            }
        });

        Product updatedProduct = repository.save(existingProduct);

        return toProductDTO(updatedProduct);

    }


    // Método auxiliar para converter Product em ProductDTO e adicionar links
    private ProductDTO toProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setAvailable(product.isAvailable());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setCategory(product.getCategory());

        // Adiciona links HATEOAS
        dto.addLink(linkTo(methodOn(ProductController.class).findById(product.getId())).withSelfRel());


        return dto;
    }

    // Método auxiliar para converter ProductDTO em Product
    private Product toProductEntity(ProductDTO productDTO) {
        Product product = new Product();
        // Não definir o ID ao criar um novo produto, pois ele é gerado automaticamente
        if (productDTO.getId() != null) {
            product.setId(productDTO.getId());
        }
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setAvailable(productDTO.getAvailable());
        product.setPrice(productDTO.getPrice());
        product.setImageUrl(productDTO.getImageUrl());
        product.setCategory(productDTO.getCategory());
        return product;
    }
}
