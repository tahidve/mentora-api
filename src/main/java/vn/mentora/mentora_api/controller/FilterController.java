package vn.mentora.mentora_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.mentora.mentora_api.domain.response.ResAvailableFilterDTO;
import vn.mentora.mentora_api.service.FilterService;
import vn.mentora.mentora_api.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class FilterController {

    private final FilterService filterService;

    public FilterController(FilterService filterService) {
        this.filterService = filterService;
    }

    @GetMapping("/filters/available")
    @ApiMessage("Lấy filter options thành công")
    public ResponseEntity<ResAvailableFilterDTO> getAvailableFilters() {
        return ResponseEntity.ok(this.filterService.getAvailableFilters());
    }

}
