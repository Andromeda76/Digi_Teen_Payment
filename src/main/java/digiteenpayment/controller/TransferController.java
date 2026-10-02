package digiteenpayment.controller;


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

    private final TransferService transferService;


    @PostMapping("/create_transaction")
    public Transfer createTransaction(@Valid @RequestBody TransferPVM transferPVM) {
        return transferService.save(TransferPVM.transferPVMToEntity(transferPVM));
    }


    @GetMapping("/ledger_values")
    public Map<String, Enum[]> ledgerValues() {
        return transferService.getLedgerEnums();
    }

}
