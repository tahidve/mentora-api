package vn.mentora.mentora_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.mentora.mentora_api.domain.Discount;
import vn.mentora.mentora_api.service.DiscountService;
import vn.mentora.mentora_api.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping("/discounts")
    @ApiMessage("fetch active discounts success")
    public ResponseEntity<List<Discount>> getActiveDiscounts() {
        List<Discount> response = this.discountService.findAllActive();
        return ResponseEntity.ok().body(response);
    }

}
