package vn.mentora.mentora_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.mentora.mentora_api.domain.Company;
import vn.mentora.mentora_api.service.CompanyService;
import vn.mentora.mentora_api.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/companies")
    @ApiMessage("Get companies success")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok().body(this.companyService.getAllCompanies());
    }

}
