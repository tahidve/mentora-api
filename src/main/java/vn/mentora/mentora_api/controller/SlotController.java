package vn.mentora.mentora_api.controller;

import vn.mentora.mentora_api.domain.response.ResSlotDTO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.mentora.mentora_api.domain.request.ReqCreateSlotDTO;
import vn.mentora.mentora_api.service.SlotService;
import vn.mentora.mentora_api.util.SecurityUtil;
import vn.mentora.mentora_api.util.annotation.ApiMessage;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping("/mentors/me/slots")
    @PreAuthorize("hasAuthority('MENTOR')")
    @ApiMessage("Tạo slot thành công")
    public ResponseEntity<ResSlotDTO> createSlot(@Valid @RequestBody ReqCreateSlotDTO request)
            throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IdInvalidException("Invalid login"));
        ResSlotDTO response = this.slotService.createSlot(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/mentors/me/slots/bulk")
    @PreAuthorize("hasAuthority('MENTOR')")
    @ApiMessage("Tạo nhiều slot thành công")
    public ResponseEntity<List<ResSlotDTO>> createBulkSlots(@Valid @RequestBody List<ReqCreateSlotDTO> request)
            throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IdInvalidException("Invalid login"));
        List<ResSlotDTO> response = this.slotService.createBulkSlots(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mentors/{id}/slots")
    @ApiMessage("Lấy danh sách slot available của mentor thành công")
    public ResponseEntity<List<ResSlotDTO>> getAvailableSlots(@PathVariable("id") Long mentorId) {
        List<ResSlotDTO> response = this.slotService.getAvailableSlot(mentorId);
        return ResponseEntity.ok().body(response);
    }

}
