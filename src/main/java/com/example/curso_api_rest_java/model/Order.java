package com.example.curso_api_rest_java.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<ItemOrder> items = new ArrayList<>();
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime datetime;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOrder status;
    @Column(nullable = false)
    private BigDecimal total;

    public enum StatusOrder {
        ABERTO, EM_PREPARO, CONCLUIDO, CANCELADO
    }

    public Order() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemOrder> getItens() {
        return items;
    }

    public void setItens(List<ItemOrder> items) {
        this.items = items;
        if (items != null) {
            for (ItemOrder item : items) {
                item.setOrder(this);
            }
        }
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public StatusOrder getStatus() {
        return status;
    }

    public void setStatus(StatusOrder status) {
        this.status = status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(items, order.items) && Objects.equals(datetime, order.datetime) && status == order.status && Objects.equals(total, order.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, items, datetime, status, total);
    }
}
