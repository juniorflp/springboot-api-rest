package com.example.curso_api_rest_java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class ItemOrderDTO {
    private Long id;
    private Long productId;
    private int quantity;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal subTotal;

    public ItemOrderDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemOrderDTO that = (ItemOrderDTO) o;
        return quantity == that.quantity && Objects.equals(id, that.id) && Objects.equals(productId, that.productId) && Objects.equals(subTotal, that.subTotal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productId, quantity, subTotal);
    }
}