package digiteenpayment.model.pvm;


import com.fasterxml.jackson.annotation.JsonProperty;
import digiteenpayment.model.enumerated.WalletState;
import digiteenpayment.model.enumerated.WalletType;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
public class WalletPVM {

    @NotNull
    private Long personId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String naturalCode;

    @NotNull
    @PositiveOrZero
    private BigDecimal balance = BigDecimal.ZERO;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private WalletType walletType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private WalletState walletState;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}