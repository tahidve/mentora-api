package vn.mentora.mentora_api.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.mentora.mentora_api.domain.response.ResultPaginationDTO;
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
}
