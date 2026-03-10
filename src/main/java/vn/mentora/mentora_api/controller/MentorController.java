package vn.mentora.mentora_api.controller;

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
import vn.mentora.mentora_api.domain.WorkFor;
import vn.mentora.mentora_api.domain.request.ReqWorkExperienceDTO;
import vn.mentora.mentora_api.domain.response.ResWorkExperienceDTO;
import vn.mentora.mentora_api.service.MentorService;
import vn.mentora.mentora_api.util.annotation.ApiMessage;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class MentorController {

    private final MentorService mentorService;

    public MentorController(MentorService mentorService) {
        this.mentorService = mentorService;
    }

    @GetMapping("/mentors/{id}/work-experience")
    @ApiMessage("Get mentor's work experience success")
    public ResponseEntity<List<ResWorkExperienceDTO>> getWorkExperiences(@PathVariable("id") Long mentorId) {
        return ResponseEntity.ok().body(this.mentorService.getWorkExperiences(mentorId));
    }

    @PostMapping("/mentors/me/work-experience")
    @PreAuthorize("hasAuthority('MENTOR')")
    @ApiMessage("Add work experience success")
    public ResponseEntity<ResWorkExperienceDTO> addWorkExperience(
            @Valid @RequestBody ReqWorkExperienceDTO request) throws IdInvalidException {
        WorkFor newWork = this.mentorService.addWorkExperience(request);
        ResWorkExperienceDTO responseDTO = new ResWorkExperienceDTO(newWork);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

}
