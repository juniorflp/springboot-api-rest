package com.example.curso_api_rest_java.controllers;

import com.example.curso_api_rest_java.dto.ProductDTO;
import com.example.curso_api_rest_java.exceptions.ResourceNotFoundException;
import com.example.curso_api_rest_java.services.ProductServices;
import com.example.curso_api_rest_java.services.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/product/v1")
@Tag(name = "Product", description = "Endpoints for Managing Products")
public class ProductController {

    @Autowired
    private ProductServices service;

    @Autowired
    private ImageService imageService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ProductDTO>>findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required=false) String search
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> productsPage = service.findAll(search, pageable);

        return ResponseEntity.ok(productsPage);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductDTO findById(@PathVariable(value = "id") Long id) {
        return service.findById(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProductDTO create(@RequestBody ProductDTO product) {
        return service.create(product);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProductDTO update(@PathVariable(value = "id") Long id, @RequestBody ProductDTO product) {
        return service.update(id, product);
    }

    @PatchMapping(value="/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDTO> patchProduct(@PathVariable Long id, @RequestBody Map<String, Object> updates ){
        try{
            ProductDTO updatedProduct = service.updatePartial(id, updates);
            return ResponseEntity.ok(updatedProduct);
        }catch(ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        }catch(Exception e){
            return ResponseEntity.status(500).build();
        }
    }




    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDTO> uploadOrUpdateImage(@PathVariable Long id, @RequestParam("image") MultipartFile image){
        try{
            String imagePath = imageService.saveImage(image);
            ProductDTO updatedProduct = service.updateImageUrl(id, imagePath);

            return  ResponseEntity.ok(updatedProduct);

        }catch(IOException e){
            return ResponseEntity.status(500).build();

        }
    }

}
