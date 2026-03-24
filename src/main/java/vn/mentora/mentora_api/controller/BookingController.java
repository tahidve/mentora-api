package vn.mentora.mentora_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.mentora.mentora_api.domain.request.ReqCreateBookingDTO;
import vn.mentora.mentora_api.domain.response.ResBookingDTO;
import vn.mentora.mentora_api.service.BookingService;
import vn.mentora.mentora_api.util.SecurityUtil;
import vn.mentora.mentora_api.util.annotation.ApiMessage;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    @PreAuthorize("hasAuthority('MENTEE')")
    @ApiMessage("Tạo booking thành công")
    public ResponseEntity<ResBookingDTO> createBooking(@Valid @RequestBody ReqCreateBookingDTO request)
            throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new IdInvalidException("Invalid login"));
        ResBookingDTO response = new ResBookingDTO();
        response = this.bookingService.createBooking(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("bookings/{id}/cancel")
    @PreAuthorize("hasAuthority('MENTEE')")
    @ApiMessage("Hủy booking thành công")
    public ResponseEntity<ResBookingDTO> cancelBooking(@PathVariable("id") Long bookingId) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new IdInvalidException("Invalid login"));
        ResBookingDTO response = this.bookingService.cancelBooking(email, bookingId);
        return ResponseEntity.ok().body(response);
    }

}
