package ru.mfa.rbpo2026.web;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.mfa.rbpo2026.domain.ResidenceContract;
import ru.mfa.rbpo2026.service.ResidenceContractService;
import ru.mfa.rbpo2026.web.dto.ResidenceContractCreateRequest;
import ru.mfa.rbpo2026.web.dto.ResidenceContractResponse;
import ru.mfa.rbpo2026.web.dto.ResidenceContractUpdateRequest;

@RestController
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
@RequestMapping("/api/residence-contracts")
public class ResidenceContractController {

    private final ResidenceContractService contractService;

    public ResidenceContractController(ResidenceContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping
    public List<ResidenceContractResponse> findAll() {
        return contractService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ResidenceContractResponse findById(@PathVariable Long id) {
        return toResponse(contractService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResidenceContractResponse create(@Valid @RequestBody ResidenceContractCreateRequest request) {
        ResidenceContract contract = contractService.create(
                request.applicationId(),
                request.roomId(),
                request.moveInDate(),
                request.moveOutDate());
        return toResponse(contract);
    }

    @PatchMapping("/{id}")
    public ResidenceContractResponse update(@PathVariable Long id,
                                            @Valid @RequestBody ResidenceContractUpdateRequest request) {
        ResidenceContract contract = contractService.update(
                id,
                request.roomId(),
                request.moveOutDate(),
                request.status());
        return toResponse(contract);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        contractService.delete(id);
    }

    private ResidenceContractResponse toResponse(ResidenceContract contract) {
        return new ResidenceContractResponse(
                contract.getId(),
                contract.getApplicationId(),
                contract.getRoomId(),
                contract.getMoveInDate(),
                contract.getMoveOutDate(),
                contract.getStatus());
    }
}
