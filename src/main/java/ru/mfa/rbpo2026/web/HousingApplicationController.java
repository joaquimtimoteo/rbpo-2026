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
import ru.mfa.rbpo2026.domain.HousingApplication;
import ru.mfa.rbpo2026.service.HousingApplicationService;
import ru.mfa.rbpo2026.web.dto.HousingApplicationCreateRequest;
import ru.mfa.rbpo2026.web.dto.HousingApplicationResponse;
import ru.mfa.rbpo2026.web.dto.HousingApplicationUpdateRequest;

@RestController
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
@RequestMapping("/api/housing-applications")
public class HousingApplicationController {

    private final HousingApplicationService applicationService;

    public HousingApplicationController(HousingApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public List<HousingApplicationResponse> findAll() {
        return applicationService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public HousingApplicationResponse findById(@PathVariable Long id) {
        return toResponse(applicationService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HousingApplicationResponse create(@Valid @RequestBody HousingApplicationCreateRequest request) {
        return toResponse(applicationService.create(request.applicantId()));
    }

    @PatchMapping("/{id}")
    public HousingApplicationResponse update(@PathVariable Long id,
                                             @Valid @RequestBody HousingApplicationUpdateRequest request) {
        return toResponse(applicationService.update(id, request.status()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        applicationService.delete(id);
    }

    private HousingApplicationResponse toResponse(HousingApplication application) {
        return new HousingApplicationResponse(
                application.getId(),
                application.getApplicantId(),
                application.getSubmittedAt(),
                application.getStatus());
    }
}
