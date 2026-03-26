package vn.mentora.mentora_api.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.mentora.mentora_api.domain.Meeting;
import vn.mentora.mentora_api.domain.request.ReqSetLocationDTO;
import vn.mentora.mentora_api.domain.response.ResMeetingDTO;
import vn.mentora.mentora_api.domain.response.ResultPaginationDTO;
import vn.mentora.mentora_api.service.MeetingService;
import vn.mentora.mentora_api.util.SecurityUtil;
import vn.mentora.mentora_api.util.annotation.ApiMessage;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @GetMapping("/meetings/mentee")
    @PreAuthorize("hasAuthority('MENTEE')")
    @ApiMessage("fetch mentee's meeting success")
    public ResponseEntity<ResultPaginationDTO> menteeGetMeetings(Pageable pageable) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new IdInvalidException("Invalid login"));
        ResultPaginationDTO response = this.meetingService.findByMenteeEmail(email, pageable);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/meetings/mentor")
    @PreAuthorize("hasAuthority('MENTOR')")
    @ApiMessage("fetch mentor's meeting success")
    public ResponseEntity<ResultPaginationDTO> mentorGetMeetings(Pageable pageable) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new IdInvalidException("Invalid login"));
        ResultPaginationDTO response = this.meetingService.findByMentorEmail(email, pageable);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/meetings/{id}")
    @ApiMessage("fetch meeting by id success")
    public ResponseEntity<ResMeetingDTO> fetchById(@PathVariable("id") Long meetingId) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new IdInvalidException("Invalid login"));
        return ResponseEntity.ok().body(this.meetingService.getMeetingDetail(meetingId, email));
    }

    @PutMapping("/meetings/{id}/location")
    @PreAuthorize("hasAuthority('MENTOR')")
    @ApiMessage("mentor set location success")
    public ResponseEntity<ResMeetingDTO> setMeetingLocation(@RequestBody ReqSetLocationDTO request,
            @PathVariable("id") Long meetingId) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new IdInvalidException("Invalid login"));
        Meeting meeting = this.meetingService.setMeetingLocation(request.getLocation(), meetingId, email);
        ResMeetingDTO response = this.meetingService.convertToMeetingDTO(meeting);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/meetings/{id}/complete")
    @PreAuthorize("hasAuthority('MENTOR')")
    @ApiMessage("mentor set meeting complete success")
    public ResponseEntity<ResMeetingDTO> setMeetingComplete(@PathVariable("id") Long meetingId)
            throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new IdInvalidException("Invalid login"));
        Meeting meeting = this.meetingService.completeMeeting(meetingId, email);
        ResMeetingDTO response = this.meetingService.convertToMeetingDTO(meeting);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/meetings/{id}/cancel")
    @PreAuthorize("hasAuthority('MENTOR')")
    @ApiMessage("mentor cancel meeting success")
    public ResponseEntity<ResMeetingDTO> cancelMeeting(@PathVariable("id") Long meetingId)
            throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new IdInvalidException("Invalid login"));
        Meeting meeting = this.meetingService.cancelMeeting(meetingId, email);
        ResMeetingDTO response = this.meetingService.convertToMeetingDTO(meeting);
        return ResponseEntity.ok().body(response);
    }

}
