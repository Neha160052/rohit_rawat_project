/*
package org.project.ttnecommerce.entity;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CartId implements Serializable {

    private UUID customerUserId;
    private UUID productVariationId;

    public CartId() {}

    public CartId(UUID customerUserId, UUID productVariationId) {
        this.customerUserId = customerUserId;
        this.productVariationId = productVariationId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if ( !(o instanceof CartId that) ) return false;
        return Objects.equals(customerUserId, that.customerUserId) &&
                Objects.equals(productVariationId, that.productVariationId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(customerUserId, productVariationId);
    }
}*/
