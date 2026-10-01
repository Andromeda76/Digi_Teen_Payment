package digiteenpayment.model;


import digiteenpayment.model.enumerated.WalletState;
import digiteenpayment.model.enumerated.WalletType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;



@Entity
@Getter
@Setter
public class Wallet extends Payment {

    @Column(name = "person_Id", nullable = false, updatable = false)
    private Long personId;

    @Column(name = "naturalCode", nullable = false, unique = true, updatable = false)
    private String naturalCode;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private WalletType walletType;

    @Enumerated(EnumType.STRING)
    private WalletState  walletState;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private int version;
}
