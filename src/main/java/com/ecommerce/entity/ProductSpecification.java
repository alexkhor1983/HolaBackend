package com.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "ProductSpecification")
public class ProductSpecification {
    @Id
    @Column(name = "product_spec_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_quantity", nullable = false)
    private Integer productQuantity;

    @Column(name = "specification", nullable = false, length = 20)
    private String specification;

    public ProductSpecification(Product product, Integer productQuantity, String specification) {
        this.product = product;
        this.productQuantity = productQuantity;
        this.specification = specification;
    }

    public ProductSpecification(Integer productQuantity, String specification) {
        this.productQuantity = productQuantity;
        this.specification = specification;
    }
}