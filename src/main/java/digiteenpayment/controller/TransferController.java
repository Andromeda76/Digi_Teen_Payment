package digiteenpayment.controller;


import digiteenpayment.controller.facade.TransferFacade;
import digiteenpayment.model.Transfer;
import digiteenpayment.model.pvm.TransferPVM;
import digiteenpayment.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/transfer")
public class TransferController {

    private final TransferFacade transferFacade;
    private final TransferService transferService;


    @PostMapping("/create_transaction")
    public Transfer createTransaction(@Valid @RequestBody TransferPVM transferPVM) {
        transferPVM.setOriginWalletId(transferFacade.getWalletId());
        return transferService.save(TransferPVM.transferPVMToEntity(transferPVM));
    }


    @PutMapping("/{request_Id}")
    public Transfer runTransaction(@PathVariable("request_Id") String requestId) {
        long walletId = transferFacade.getWalletId();
        return transferService.update(requestId, walletId);
    }


    @GetMapping("/ledger_values")
    public Map<String, Enum[]> ledgerValues() {
        return transferService.getLedgerEnums();
    }

}
