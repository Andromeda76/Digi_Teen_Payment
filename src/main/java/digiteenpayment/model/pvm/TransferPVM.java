package digiteenpayment.model.pvm;

import digiteenpayment.model.Transfer;
import digiteenpayment.model.Wallet;
import digiteenpayment.model.enumerated.TransferStatus;
import digiteenpayment.model.enumerated.TransferType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;



@Getter
@Setter
public class TransferPVM {

    @Positive
    private Long walletDestinationId;

    @NotNull
    @Positive
    private Long originWalletId;

    @NotNull
    @PositiveOrZero
    private BigDecimal amount;

    private String requestId;

    @NotNull
    private TransferType transferType;

    @NotNull
    private TransferStatus transferStatus;

    @Positive
    private Long LedgerId;


    public static Transfer transferPVMToEntity(TransferPVM transferPVM) {
        Transfer transfer = new Transfer();
        Wallet originWallet = new Wallet();

        originWallet.setId(transferPVM.getOriginWalletId());
        transfer.setOrigin(originWallet);

        if (transferPVM.getWalletDestinationId() != null &&
                transferPVM.getTransferType().equals(TransferType.TRANSFER)) {
            Wallet destinationWallet = new Wallet();
            destinationWallet.setId(transferPVM.getWalletDestinationId());
            transfer.setDestination(destinationWallet);
        }

        transfer.setAmount(transferPVM.getAmount());
        transfer.setTransferType(transferPVM.getTransferType());
        transfer.setTransferStatus(transferPVM.getTransferStatus());
        return transfer;
    }
}
