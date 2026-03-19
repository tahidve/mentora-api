package vn.mentora.mentora_api.controller;

import vn.mentora.mentora_api.domain.response.ResSlotDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        ResSlotDTO slot = this.slotService.createSlot(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(slot);
    }

}
