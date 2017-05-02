package com.example;

import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zoltan Altfatter
 */
@RestController
public class BankTransferController {

    private final BankTransferService service;

    public BankTransferController(BankTransferService service) {
        this.service = service;
    }

    @PostMapping("/transfers")
    public ResponseEntity<CreateBankTransferResponse> transfer(@RequestBody CreateBankTransferRequest request) {
        String bankTransferId = service.completeTransfer(new BankTransfer(request.from, request.to, request.amountInCents));
        return new ResponseEntity(new CreateBankTransferResponse(bankTransferId), HttpStatus.CREATED);
    }

    @GetMapping("/transfers/{id}")
    public ResponseEntity<BankTransferView> findById(@PathVariable String id) {
        BankTransfer bankTransfer = service.findById(id);
        if (bankTransfer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(createView(bankTransfer));
    }

    @GetMapping("/transfers")
    public ResponseEntity<List<BankTransferView>> findAll() {
        List<BankTransfer> bankTransfers = service.findAll();
        List<BankTransferView> view = bankTransfers.stream().map(bankTransfer -> createView(bankTransfer)).collect(Collectors.toList());
        return ResponseEntity.ok(view);
    }

    private BankTransferView createView(BankTransfer bankTransfer) {
        return new BankTransferView(bankTransfer.getId(), bankTransfer.getSourceBankAccountId(),
                bankTransfer.getDestinationBankAccountId(), bankTransfer.getAmountInCents(), bankTransfer.getStatus().toString());
    }

    @Value
    static class CreateBankTransferRequest {
        String from;
        String to;
        long amountInCents;
    }

    @Value
    static class CreateBankTransferResponse {
        String bankTransferId;
    }

    @Value
    static class BankTransferView {
        String bankTransferId;
        String from;
        String to;
        long amountInCents;
        String status;
    }
}
