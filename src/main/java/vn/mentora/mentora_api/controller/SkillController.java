package vn.mentora.mentora_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.mentora.mentora_api.domain.Skill;
import vn.mentora.mentora_api.domain.request.ReqCreateSkillDTO;
import vn.mentora.mentora_api.service.SkillService;
import vn.mentora.mentora_api.util.annotation.ApiMessage;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiMessage("Create skill success")
    public ResponseEntity<Skill> create(@Valid @RequestBody ReqCreateSkillDTO request) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.createSkill(request.getSkillName()));
    }

    @GetMapping("/skills")
    @ApiMessage("Get all skills success")
    public ResponseEntity<List<Skill>> getAllSkills() {
        return ResponseEntity.ok().body(this.skillService.getAllSkills());
    }

}
