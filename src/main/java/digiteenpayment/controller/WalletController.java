package digiteenpayment.controller;


import digiteenpayment.model.Wallet;
import digiteenpayment.model.pvm.WalletPVM;
import digiteenpayment.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/wallet")
public class WalletController {


    private final WalletService walletService;

    @PostMapping("/create")
    public Wallet create(@Valid @RequestBody WalletPVM walletPVM) {
        return walletService.save(WalletPVM.walletPVMToEntity(walletPVM));
    }

}
