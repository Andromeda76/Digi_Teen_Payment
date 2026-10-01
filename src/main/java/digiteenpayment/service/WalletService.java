package digiteenpayment.service;


import digiteenpayment.model.Wallet;
import digiteenpayment.repository.WalletIRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletIRepository walletRepository;


    public Wallet save(Wallet wallet) {
        return walletRepository.save(wallet);
    }

}
