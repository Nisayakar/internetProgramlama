package entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "product")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 140)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "currentprice", nullable = false)
    private Double currentPrice;

    @Column(name = "stockquantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "imageurl", length = 255)
    private String imageUrl;


    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageResourcePath() {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        if (imageUrl.startsWith("/resources/")) {
            return imageUrl.substring("/resources/".length());
        }

        if (imageUrl.startsWith("resources/")) {
            return imageUrl.substring("resources/".length());
        }

        if (imageUrl.startsWith("/")) {
            return imageUrl.substring(1);
        }

        return imageUrl;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}



