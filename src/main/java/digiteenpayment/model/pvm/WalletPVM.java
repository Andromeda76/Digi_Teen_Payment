package digiteenpayment.model.pvm;


import com.fasterxml.jackson.annotation.JsonProperty;
import digiteenpayment.model.Wallet;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
public class WalletPVM {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String naturalCode;

    @NotNull
    @PositiveOrZero
    private BigDecimal balance = BigDecimal.ZERO;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;


    public static Wallet walletPVMToEntity(WalletPVM walletPVM) {
        Wallet wallet = new Wallet();
        wallet.setEmail(walletPVM.getEmail());
        wallet.setBalance(walletPVM.getBalance());
        wallet.setUpdatedAt(walletPVM.getUpdatedAt());
        wallet.setCreatedAt(walletPVM.getCreatedAt());
        wallet.setNaturalCode(UUID.randomUUID().toString());
        return wallet;
    }

}