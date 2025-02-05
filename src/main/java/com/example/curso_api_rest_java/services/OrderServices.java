package com.example.curso_api_rest_java.services;

import com.example.curso_api_rest_java.controllers.OrderController;
import com.example.curso_api_rest_java.dto.ItemOrderDTO;
import com.example.curso_api_rest_java.dto.OrderDTO;
import com.example.curso_api_rest_java.exceptions.ResourceNotFoundException;
import com.example.curso_api_rest_java.model.ItemOrder;
import com.example.curso_api_rest_java.model.Order;
import com.example.curso_api_rest_java.model.Product;
import com.example.curso_api_rest_java.repositories.OrderRepository;
import com.example.curso_api_rest_java.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class OrderServices {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private ProductRepository productRepository; // Repositório para validação dos produtos

    // Retorna todos os pedidos com os itens (evitando LazyInitializationException)
    public List<OrderDTO> findAll() {
        return repository.findAllOrdersWithItems().stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO findById(Long id) {
        Order order = repository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Records found for this id"));
        return convertToOrderDTO(order);
    }

    public OrderDTO create(OrderDTO orderDTO) {
        Order order = convertToOrderEntity(orderDTO);
        order.setDatetime(LocalDateTime.now());
        order.setStatus(Order.StatusOrder.ABERTO);
        order.setTotal((order.getItens() != null && !order.getItens().isEmpty())
                ? calculateTotal(order.getItens())
                : BigDecimal.ZERO);

        order = repository.save(order);
        return convertToOrderDTO(order);
    }

    @Transactional
    public OrderDTO update(Long id, OrderDTO orderDTO) {
        Order order = repository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Records found for this id"));

        // Atualiza campos simples do pedido
        if (orderDTO.getStatus() != null) {
            order.setStatus(orderDTO.getStatus());
        }

        // Realiza o merge dos itens enviados com os já existentes
        if (orderDTO.getItems() != null && !orderDTO.getItems().isEmpty()) {
            // Realiza o merge dos itens se eles foram enviados
            mergeItemOrders(order, orderDTO.getItems());
        }

        // Recalcula o total (soma dos subtotais dos itens)
        order.setTotal((order.getItens() != null && !order.getItens().isEmpty())
                ? calculateTotal(order.getItens())
                : BigDecimal.ZERO);

        order = repository.save(order);
        return convertToOrderDTO(order);
    }

    public void delete(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Records found for this id"));
        repository.delete(order);
    }

    @Transactional
    public void deleteItemFromOrder(Long orderId, Long itemId) {
        Order order = repository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));

        boolean removed = order.getItens().removeIf(item -> item.getId().equals(itemId));

        if (!removed) {
            throw new ResourceNotFoundException("Item not found with id " + itemId + " in order " + orderId);
        }

        order.setTotal(calculateTotal(order.getItens()));

        repository.save(order);
    }

    // Calcula o total da order somando os subtotais de cada item
    private BigDecimal calculateTotal(List<ItemOrder> items) {
        return items.stream()
                .map(ItemOrder::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Converte Order para OrderDTO
    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setTotal(order.getTotal());
        dto.setDatetime(order.getDatetime());

        // Converte a lista de ItemOrder para ItemOrderDTO
        List<ItemOrderDTO> itemDTOList = order.getItens().stream()
                .map(this::convertToItemOrderDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOList);

        // Adiciona link HATEOAS para o recurso
        dto.addLink(linkTo(methodOn(OrderController.class).findById(order.getId())).withSelfRel());
        return dto;
    }

    // Converte OrderDTO para Order
    private Order convertToOrderEntity(OrderDTO orderDTO) {
        Order order = new Order();
        if (orderDTO.getId() != null) {
            order.setId(orderDTO.getId());
        }
        order.setStatus(orderDTO.getStatus());
        order.setTotal(orderDTO.getTotal());
        order.setDatetime(orderDTO.getDatetime());

        // Converte os itens e já associa cada ItemOrder à Order
        List<ItemOrder> items = convertToItemOrderEntityList(orderDTO.getItems(), order);
        order.setItens(items);

        return order;
    }

    // Converte ItemOrder para ItemOrderDTO
    private ItemOrderDTO convertToItemOrderDTO(ItemOrder itemOrder) {
        ItemOrderDTO dto = new ItemOrderDTO();
        dto.setId(itemOrder.getId());
        dto.setProductId(itemOrder.getProduct().getId());
        dto.setQuantity(itemOrder.getQuantity());
        dto.setSubTotal(itemOrder.getSubTotal());
        return dto;
    }

    // Converte uma lista de ItemOrderDTO para ItemOrder, associando cada item à Order
    private List<ItemOrder> convertToItemOrderEntityList(List<ItemOrderDTO> itemDTOs, Order order) {
        if (itemDTOs == null) {
            return new ArrayList<>();
        }
        return itemDTOs.stream()
                .map(dto -> convertToItemOrderEntity(dto, order))
                .collect(Collectors.toList());
    }

    // Converte ItemOrderDTO para ItemOrder, associando o item à Order
    // Realiza a validação do produto e recalcula o subtotal automaticamente
    private ItemOrder convertToItemOrderEntity(ItemOrderDTO dto, Order order) {
        ItemOrder itemOrder = new ItemOrder();
        itemOrder.setId(dto.getId());
        itemOrder.setQuantity(dto.getQuantity());

        // Valida se o produto existe e o busca (lança exceção se não existir)
        Product product = findProductById(dto.getProductId());
        itemOrder.setProduct(product);

        // Calcula o subtotal automaticamente: preço do produto * quantidade
        itemOrder.setSubTotal(product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        itemOrder.setOrder(order);
        return itemOrder;
    }

    // Método auxiliar para buscar e validar a existência do produto
    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for id " + productId));
    }

    // Realiza o merge entre a lista atual de itens do pedido e a nova lista vinda do DTO
    private void mergeItemOrders(Order order, List<ItemOrderDTO> newItemsDTO) {
        List<ItemOrder> mergedItems = new ArrayList<>();
        List<ItemOrder> existingItems = order.getItens() != null ? order.getItens() : new ArrayList<>();

        // Cria um mapa para acesso rápido aos itens existentes (por id)
        Map<Long, ItemOrder> existingItemsMap = existingItems.stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(ItemOrder::getId, item -> item));

        for (ItemOrderDTO dto : newItemsDTO) {
            if (dto.getId() != null && existingItemsMap.containsKey(dto.getId())) {
                // Atualiza o item existente
                ItemOrder existingItem = existingItemsMap.get(dto.getId());
                existingItem.setQuantity(dto.getQuantity());
                // Atualiza o produto (valida se existe)
                Product product = findProductById(dto.getProductId());
                existingItem.setProduct(product);
                // Recalcula o subtotal
                existingItem.setSubTotal(product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
                mergedItems.add(existingItem);
            } else {
                // Cria um novo item
                ItemOrder newItem = convertToItemOrderEntity(dto, order);
                mergedItems.add(newItem);
            }
        }
        // Atualiza a lista de itens do pedido
        order.setItens(mergedItems);
    }
}
