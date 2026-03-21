package vn.mentora.mentora_api.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import com.turkraft.springfilter.boot.Filter;

import vn.mentora.mentora_api.domain.Mentor;
import vn.mentora.mentora_api.domain.WorkFor;
import vn.mentora.mentora_api.domain.request.ReqUpdateMentorDTO;
import vn.mentora.mentora_api.domain.request.ReqWorkExperienceDTO;
import vn.mentora.mentora_api.domain.response.ResMentorProfileDTO;
import vn.mentora.mentora_api.domain.response.ResWorkExperienceDTO;
import vn.mentora.mentora_api.domain.response.ResultPaginationDTO;
import vn.mentora.mentora_api.service.MentorService;
import vn.mentora.mentora_api.util.SecurityUtil;
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

    // Mentor update profile
    @PutMapping("/mentors/me")
    @PreAuthorize("hasAuthority('MENTOR')")
    @ApiMessage("Mentor update profile success")
    public ResponseEntity<ResMentorProfileDTO> updateMentorProfile(@RequestBody ReqUpdateMentorDTO request)
            throws IdInvalidException {
        Optional<String> emailOptional = SecurityUtil.getCurrentUserLogin();
        if (emailOptional.isEmpty()) {
            throw new IdInvalidException("Invalid login");
        }

        ResMentorProfileDTO response = this.mentorService.updateProfile(emailOptional.get(), request);
        return ResponseEntity.ok().body(response);
    }

    // Mentors get their profile
    @GetMapping("/mentors/me")
    @PreAuthorize("hasAuthority('MENTOR')")
    @ApiMessage("Mentor get profile success")
    public ResponseEntity<ResMentorProfileDTO> getMentorProfile() throws IdInvalidException {
        Optional<String> emailOptional = SecurityUtil.getCurrentUserLogin();
        if (emailOptional.isEmpty()) {
            throw new IdInvalidException("Invalid login");
        }

        Mentor mentor = this.mentorService.getMentorByEmail(emailOptional.get());
        Long mentorId = mentor.getMentorId();
        ResMentorProfileDTO response = this.mentorService.getPublicProfile(mentorId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/mentors/search")
    @ApiMessage("Search mentors by keyword success")
    public ResponseEntity<ResultPaginationDTO> searchMentors(
            @RequestParam(name = "q", defaultValue = "") String keyword, Pageable pageable) {

        ResultPaginationDTO response = this.mentorService.searchMentors(keyword, pageable);
        return ResponseEntity.ok().body(response);
    }

    // All users see mentor profile
    @GetMapping("/mentors/{id}")
    @ApiMessage("Get mentor profile success")
    public ResponseEntity<ResMentorProfileDTO> getPublicMentorProfile(
            @PathVariable("id") Long mentorId) throws IdInvalidException {
        return ResponseEntity.ok(this.mentorService.getPublicProfile(mentorId));
    }

    // Mentor update avatar
    @PostMapping("/mentors/me/avatar")
    @PreAuthorize("hasAuthority('MENTOR')")
    @ApiMessage("Mentor upload avatar success")
    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file)
            throws IdInvalidException, IOException {
        Optional<String> emailOptional = SecurityUtil.getCurrentUserLogin();
        if (emailOptional.isEmpty()) {
            throw new IdInvalidException("Invalid login");
        }
        String email = emailOptional.get();
        String filename = this.mentorService.uploadAvatar(email, file);
        return ResponseEntity.ok(filename);
    }

    // Mentor update skills
    @PutMapping("/mentors/me/skills")
    @PreAuthorize("hasAuthority('MENTOR')")
    @ApiMessage("Mentor update skills success")
    public ResponseEntity<ResMentorProfileDTO> updateSkills(@RequestBody List<Long> skillIds)
            throws IdInvalidException {
        Optional<String> emailOptional = SecurityUtil.getCurrentUserLogin();
        if (emailOptional.isEmpty()) {
            throw new IdInvalidException("Invalid login");
        }
        String email = emailOptional.get();

        ResMentorProfileDTO result = this.mentorService.updateSkills(email, skillIds);
        return ResponseEntity.ok(result);
    }

    // Mentor update language
    @PutMapping("/mentors/me/languages")
    @PreAuthorize("hasAuthority('MENTOR')")
    @ApiMessage("Mentor update language success")
    public ResponseEntity<ResMentorProfileDTO> updateLanguages(@RequestBody List<String> languages)
            throws IdInvalidException {
        Optional<String> emailOptional = SecurityUtil.getCurrentUserLogin();
        if (emailOptional.isEmpty()) {
            throw new IdInvalidException("Invalid login");
        }
        String email = emailOptional.get();

        ResMentorProfileDTO result = this.mentorService.updateLanguages(email, languages);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/mentors")
    @ApiMessage("Mentors fetched successfully")
    public ResponseEntity<ResultPaginationDTO> getMentors(@Filter Specification<Mentor> spec, Pageable pageable) {
        ResultPaginationDTO response = this.mentorService.fetchMentorsPaginated(spec, pageable);
        return ResponseEntity.ok(response);
    }

}
