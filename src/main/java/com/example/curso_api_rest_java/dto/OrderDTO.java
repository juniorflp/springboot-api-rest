package com.example.curso_api_rest_java.dto;

import com.example.curso_api_rest_java.model.ItemOrder;
import com.example.curso_api_rest_java.model.Order;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.hateoas.Link;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderDTO {

    private Long id;
    private List<ItemOrderDTO> items = new ArrayList<>();
    private Order.StatusOrder status;
    private BigDecimal total;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime datetime;

    @JsonProperty("_links")
    private List<Link> links = new ArrayList<>();

    public OrderDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemOrderDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemOrderDTO> items) {
        this.items = items;
    }

    public Order.StatusOrder getStatus() {
        return status;
    }

    public void setStatus(Order.StatusOrder status) {
        this.status = status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void addLink(Link link) {
        this.links.add(link);
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDTO orderDTO = (OrderDTO) o;
        return Objects.equals(id, orderDTO.id) && Objects.equals(items, orderDTO.items) && status == orderDTO.status && Objects.equals(total, orderDTO.total) && Objects.equals(links, orderDTO.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, items, status, total, links);
    }
}
