package digiteenpayment.model;


import digiteenpayment.model.enumerated.TransferStatus;
import digiteenpayment.model.enumerated.TransferType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class Transfer extends Payment {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(updatable = false)
    private Wallet origin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false)
    private Wallet destination;

    @Column(nullable = false, precision = 19, scale = 2, updatable = false)
    private BigDecimal amount;

    @Column(nullable = false, unique = true, updatable = false)
    private String requestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TransferType transferType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus transferStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;
}