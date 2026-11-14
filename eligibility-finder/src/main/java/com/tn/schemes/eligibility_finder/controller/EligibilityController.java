package com.tn.schemes.eligibility_finder.controller;

import com.tn.schemes.eligibility_finder.model.CitizenProfile;
import com.tn.schemes.eligibility_finder.model.Scheme;
import com.tn.schemes.eligibility_finder.service.EligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schemes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EligibilityController {

    private final EligibilityService eligibilityService;

    @PostMapping("/check")
    public ResponseEntity<List<Scheme>> checkEligibility(@RequestBody CitizenProfile profile) {
        return ResponseEntity.ok(eligibilityService.findEligibleSchemes(profile));
    }

    @GetMapping
    public ResponseEntity<List<Scheme>> getAllSchemes() {
        return ResponseEntity.ok(eligibilityService.getAllSchemes());
    }
}