package com.tn.schemes.eligibility_finder.repository;

import com.tn.schemes.eligibility_finder.model.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchemeRepository extends JpaRepository<Scheme, Long> {
    List<Scheme> findBySchemeCodeIn(List<String> codes);
}