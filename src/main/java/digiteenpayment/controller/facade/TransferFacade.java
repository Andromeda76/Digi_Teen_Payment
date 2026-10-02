package digiteenpayment.controller.facade;


import digiteenpayment.model.Wallet;
import digiteenpayment.service.WalletService;
import digiteenpayment.service.security.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TransferFacade {


    private final PersonService personService;
    private final WalletService walletService;


    public long getWalletId(){
        String email = personService.getEmail();
        Wallet wallet = walletService.findByEmail(email);
        return wallet.getId();
    }

}
