package entity;

import enums.OrderStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "orderdate", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "totalamount", nullable = false)
    private Double totalAmount;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> details = new ArrayList<>();

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        if (status == null) {
            return null;
        }

        switch (status) {
            case "ONAY_BEKLIYOR":
                return OrderStatus.PENDING_APPROVAL;
            case "ONAYLANDI":
                return OrderStatus.APPROVED;
            case "HAZIRLANIYOR":
                return OrderStatus.PREPARING;
            case "TESLIM_EDILDI":
                return OrderStatus.DELIVERED;
            case "IPTAL_EDILDI":
                return OrderStatus.CANCELLED;
            default:
                return OrderStatus.valueOf(status);
        }
    }

    public void setStatus(OrderStatus status) {
        this.status = status == null ? null : status.name();
    }

    public List<OrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }
}

