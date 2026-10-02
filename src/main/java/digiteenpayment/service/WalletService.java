package digiteenpayment.service;


import digiteenpayment.model.Wallet;
import digiteenpayment.repository.WalletIRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletIRepository walletRepository;


    public Wallet save(Wallet wallet) {
        return walletRepository.save(wallet);
    }


    public Wallet findById(Long aLong) {
        return walletRepository
                .findById(aLong)
                .orElseThrow(()-> new EntityNotFoundException("Wallet not found"));
    }

}
