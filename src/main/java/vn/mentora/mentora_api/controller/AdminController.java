package vn.mentora.mentora_api.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;

import vn.mentora.mentora_api.domain.Invoice;
import vn.mentora.mentora_api.domain.response.ResAdminStatsDTO;
import vn.mentora.mentora_api.domain.response.ResAdminInvoiceDTO;
import vn.mentora.mentora_api.domain.response.ResAdminMenteeDTO;
import vn.mentora.mentora_api.domain.response.ResAdminMentorDTO;
import vn.mentora.mentora_api.domain.response.ResultPaginationDTO;
import vn.mentora.mentora_api.util.error.IdInvalidException;
import vn.mentora.mentora_api.service.AdminService;
import vn.mentora.mentora_api.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/mentees")
    @ApiMessage("fetch all mentees success")
    public ResponseEntity<ResultPaginationDTO> getAllMentees(Pageable pageable) {
        return ResponseEntity.ok(this.adminService.getAllMentees(pageable));
    }

    @GetMapping("/mentees/{id}")
    @ApiMessage("fetch mentee detail success")
    public ResponseEntity<ResAdminMenteeDTO> getMenteeById(@PathVariable("id") Long id)
            throws IdInvalidException {
        return ResponseEntity.ok(this.adminService.getMenteeDetail(id));
    }

    @GetMapping("/mentors")
    @ApiMessage("fetch all mentors success")
    public ResponseEntity<ResultPaginationDTO> getAllMentors(Pageable pageable) {
        return ResponseEntity.ok(this.adminService.getAllMentors(pageable));
    }

    @GetMapping("/mentors/pending")
    @ApiMessage("fetch mentor pending success")
    public ResponseEntity<ResultPaginationDTO> getPendingMentors(Pageable pageable) {
        return ResponseEntity.ok(this.adminService.getPendingMentors(pageable));
    }

    @GetMapping("/mentors/{id}")
    @ApiMessage("fetch mentor detail success")
    public ResponseEntity<ResAdminMentorDTO> getMentorById(@PathVariable("id") Long id)
            throws IdInvalidException {
        return ResponseEntity.ok(this.adminService.getMentorDetail(id));
    }

    @PutMapping("/mentees/{id}/ban")
    @ApiMessage("toggle ban/unban mentee success")
    public ResponseEntity<ResAdminMenteeDTO> toggleBanMentee(@PathVariable("id") Long id)
            throws IdInvalidException {
        return ResponseEntity.ok(this.adminService.toggleBanMentee(id));
    }

    @PutMapping("/mentors/{id}/ban")
    @ApiMessage("toggle ban/unban mentor sucsess")
    public ResponseEntity<ResAdminMentorDTO> toggleBanMentor(@PathVariable("id") Long id)
            throws IdInvalidException {
        return ResponseEntity.ok(this.adminService.toggleBanMentor(id));
    }

    @PutMapping("/mentors/{id}/approve")
    @ApiMessage("approve mentor success")
    public ResponseEntity<ResAdminMentorDTO> approveMentor(@PathVariable("id") Long id)
            throws IdInvalidException {
        return ResponseEntity.ok(this.adminService.approveMentor(id));
    }

    @PutMapping("/mentors/{id}/reject")
    @ApiMessage("reject mentor success")
    public ResponseEntity<ResAdminMentorDTO> rejectMentor(
            @PathVariable("id") Long id,
            @Valid @RequestBody vn.mentora.mentora_api.domain.request.ReqAdminReviewMentorDTO dto)
            throws IdInvalidException {
        return ResponseEntity.ok(this.adminService.rejectMentor(id, dto));
    }
    @GetMapping("/invoices")
    @ApiMessage("fetch all invoices success")
    public ResponseEntity<ResultPaginationDTO> getAllInvoices(
            @Filter Specification<Invoice> spec, Pageable pageable) {
        return ResponseEntity.ok(this.adminService.getAllInvoices(spec, pageable));
    }

    @GetMapping("/stats")
    @ApiMessage("fetch admin stats success")
    public ResponseEntity<ResAdminStatsDTO> getOverallStats() {
        return ResponseEntity.ok(this.adminService.getOverallStats());
    }

}
