package vn.mentora.mentora_api.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.mentora.mentora_api.domain.request.ReqSubmitFeedbackDTO;
import vn.mentora.mentora_api.domain.response.ResFeedbackDTO;
import vn.mentora.mentora_api.domain.response.ResultPaginationDTO;
import vn.mentora.mentora_api.service.FeedbackService;
import vn.mentora.mentora_api.util.SecurityUtil;
import vn.mentora.mentora_api.util.annotation.ApiMessage;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/feedbacks")
    @PreAuthorize("hasAuthority('MENTEE')")
    @ApiMessage("submit feedback success")
    public ResponseEntity<ResFeedbackDTO> submitFeedback(@Valid @RequestBody ReqSubmitFeedbackDTO request)
            throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new IdInvalidException("Invalid login"));
        ResFeedbackDTO response = this.feedbackService.submitFeedback(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/feedbacks/mentor/{mentorId}")
    @ApiMessage("get mentor's feedback success")
    public ResponseEntity<ResultPaginationDTO> fetchMentorFeedback(@PathVariable("mentorId") Long mentorId,
            Pageable pageable) {
        ResultPaginationDTO response = this.feedbackService.getFeedbacksByMentorId(mentorId, pageable);
        return ResponseEntity.ok().body(response);
    }

}
