package digiteenpayment.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class Wallet extends Payment {

    @Column(name = "email", nullable = false, updatable = false, unique = true)
    private String email;

    @Column(name = "naturalCode", nullable = false, unique = true, updatable = false)
    private String naturalCode;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private int version;
}
