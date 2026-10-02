package digiteenpayment.controller;


import digiteenpayment.model.Wallet;
import digiteenpayment.model.pvm.WalletPVM;
import digiteenpayment.service.WalletService;
import digiteenpayment.service.security.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/wallet")
public class WalletController {


    private final WalletService walletService;
    private final PersonService personService;

    @PostMapping("/create")
    public Wallet create(@Valid @RequestBody WalletPVM walletPVM) {
        String email = personService.getEmail();
        walletPVM.setEmail(email);
        return walletService.save(WalletPVM.walletPVMToEntity(walletPVM));
    }

}
