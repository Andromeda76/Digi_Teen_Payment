package digiteenpayment.controller;


import digiteenpayment.model.Wallet;
import digiteenpayment.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/wallet")
public class WalletController {


    private final WalletService walletService;


    @PostMapping("/create")
    public Wallet create(@RequestBody Wallet wallet) {
        return walletService.save(wallet);
    }

}
