package vn.mentora.mentora_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.mentora.mentora_api.domain.Company;
import vn.mentora.mentora_api.domain.request.ReqCompanyDTO;
import vn.mentora.mentora_api.repository.CompanyRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company createCompany(ReqCompanyDTO request) {
        Company company = new Company();

        company.setCompanyName(request.getCompanyName());

        return companyRepository.save(company);
    }

    public List<Company> getAllCompanies() {
        return this.companyRepository.findAll();
    }

}
