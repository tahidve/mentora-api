package vn.mentora.mentora_api.controller;

import java.util.Map;

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
import vn.mentora.mentora_api.domain.request.ReqCreateMentorshipDTO;
import vn.mentora.mentora_api.domain.request.ReqCreateSessionDTO;
import vn.mentora.mentora_api.domain.response.ResPlanMentorshipDTO;
import vn.mentora.mentora_api.domain.response.ResPlanSessionDTO;
import vn.mentora.mentora_api.service.PlanService;
import vn.mentora.mentora_api.util.SecurityUtil;
import vn.mentora.mentora_api.util.annotation.ApiMessage;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping("/mentors/me/plans/session")
    @PreAuthorize("hasAuthority('MENTOR')")
    @ApiMessage("Create plan session success")
    public ResponseEntity<ResPlanSessionDTO> createSession(@Valid @RequestBody ReqCreateSessionDTO request)
            throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IdInvalidException("Invalid login"));
        ResPlanSessionDTO response = this.planService.createSession(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/mentors/me/plans/mentorship")
    @PreAuthorize("hasAuthority('MENTOR')")
    @ApiMessage("Create plan mentorship success")
    public ResponseEntity<ResPlanMentorshipDTO> createMentorship(@Valid @RequestBody ReqCreateMentorshipDTO request)
            throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IdInvalidException("Invalid login"));
        ResPlanMentorshipDTO response = this.planService.createMentorship(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("mentors/{id}/plans")
    @ApiMessage("Get mentor plans success")
    public ResponseEntity<Map<String, Object>> getPlansByMentor(@PathVariable("id") Long mentorId) {
        return ResponseEntity.ok().body(this.planService.getPlansByMentor(mentorId));
    }

}
