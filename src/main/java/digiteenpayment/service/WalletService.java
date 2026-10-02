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


    public Wallet findByEmail(String email) {
        return walletRepository
                .findByEmail(email)
                .orElseThrow(()-> new EntityNotFoundException("Wallet not found"));
    }


    public Wallet findById(Long id) {
        return walletRepository
                .findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Wallet not found"));
    }

}
