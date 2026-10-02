package digiteenpayment.service;


import digiteenpayment.model.Transfer;
import digiteenpayment.model.Wallet;
import digiteenpayment.model.enumerated.TransferType;
import digiteenpayment.model.enumerated.TransferStatus;
import digiteenpayment.repository.TransferIRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TransferService {

    private final WalletService walletService;
    private final TransferIRepository transferIRepository;


    public Transfer save(Transfer transfer) {
        transfer.setRequestId(UUID.randomUUID().toString());
        return transferIRepository.save(transfer);
    }


    @Transactional(rollbackFor = Throwable.class)
    public Transfer update(String requestId, long walletOwnerId) {
        Transfer transfer = findByRequestId(requestId);

        if (transfer.getTransferStatus() == TransferStatus.COMPLETED) {
            return transfer;
        }

        BigDecimal amount = transfer.getAmount();
        Wallet owner = walletService.findById(transfer.getOrigin().getId());

        if (owner.getId() == walletOwnerId) {
            if (transfer.getTransferType() == TransferType.DEPOSIT) {
                owner.setBalance(owner.getBalance().add(amount));

            } else if (transfer.getTransferType() == TransferType.WITHDRAW
                && checkBalance(owner, amount)) {
                owner.setBalance(owner.getBalance().subtract(amount));
            }
            else if (transfer.getTransferType() == TransferType.TRANSFER
                    && checkBalance(owner, amount)) {
                Wallet destination = walletService.findById(transfer.getDestination().getId());
                owner.setBalance(owner.getBalance().subtract(amount));
                destination.setBalance(destination.getBalance().add(amount));
            }

            transfer.setTransferStatus(TransferStatus.COMPLETED);
        } else {
            throw new RuntimeException("Inconsistent data found");
        }
        return transfer;
    }


    public Transfer findByRequestId(String requestId) {
        return transferIRepository
                .findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("requestId not found"));
    }


    public Map<String, Enum[]> getLedgerEnums() {
        Map<String, Enum[]> ledgerEnums = new HashMap<>();
        ledgerEnums.put("LedgerTypes", TransferType.values());
        ledgerEnums.put("LedgerStatus", TransferStatus.values());
         return ledgerEnums;
    }

    private boolean checkBalance(Wallet owner, BigDecimal amount) {
        if (owner.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        return true;
    }
}
