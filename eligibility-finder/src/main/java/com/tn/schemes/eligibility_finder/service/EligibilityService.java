package com.tn.schemes.eligibility_finder.service;

import com.tn.schemes.eligibility_finder.model.CitizenProfile;
import com.tn.schemes.eligibility_finder.model.Scheme;
import com.tn.schemes.eligibility_finder.repository.SchemeRepository;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EligibilityService {

    private final KieContainer kieContainer;
    private final SchemeRepository schemeRepository;

    public List<Scheme> findEligibleSchemes(CitizenProfile profile) {
        KieSession kieSession = kieContainer.newKieSession();
        try {
            kieSession.insert(profile);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }

        if (profile.getEligibleSchemeCodes().isEmpty()) {
            return Collections.emptyList();
        }

        return schemeRepository.findBySchemeCodeIn(profile.getEligibleSchemeCodes());
    }

    public List<Scheme> getAllSchemes() {
        return schemeRepository.findAll();
    }
}