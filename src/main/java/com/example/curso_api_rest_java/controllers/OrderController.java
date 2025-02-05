package com.example.curso_api_rest_java.controllers;

import com.example.curso_api_rest_java.dto.OrderDTO;
import com.example.curso_api_rest_java.services.OrderServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/order/v1")
@Tag(name = "Order", description = "Endpoints for Managing Order")
public class OrderController {

    @Autowired
    private OrderServices service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderDTO findById(@PathVariable(value = "id") Long id) {
        return service.findById(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrderDTO create(@RequestBody OrderDTO order) {
        return service.create(order);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrderDTO update(@PathVariable(value = "id") Long id, @RequestBody OrderDTO order) {
        return service.update(id, order);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Void> deleteItemFromOrder(
            @PathVariable("orderId") Long orderId,
            @PathVariable("itemId") Long itemId) {
        service.deleteItemFromOrder(orderId, itemId);
        return ResponseEntity.noContent().build();
    }
}
