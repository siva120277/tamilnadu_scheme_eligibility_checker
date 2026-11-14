package com.tn.schemes.eligibility_finder.model;

import java.util.ArrayList;
import java.util.List;

public class CitizenProfile {

    private int age;
    private String gender;
    private double annualIncome;
    private String occupation;
    private String community;
    private String educationLevel;
    private String maritalStatus;

    private List<String> eligibleSchemeCodes = new ArrayList<>();

    public CitizenProfile() {
    }

    public void addEligibleScheme(String schemeCode) {
        if (this.eligibleSchemeCodes == null) {
            this.eligibleSchemeCodes = new ArrayList<>();
        }
        if (!this.eligibleSchemeCodes.contains(schemeCode)) {
            this.eligibleSchemeCodes.add(schemeCode);
        }
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(double annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public List<String> getEligibleSchemeCodes() {
        if (this.eligibleSchemeCodes == null) {
            this.eligibleSchemeCodes = new ArrayList<>();
        }
        return eligibleSchemeCodes;
    }

    public void setEligibleSchemeCodes(List<String> eligibleSchemeCodes) {
        this.eligibleSchemeCodes = eligibleSchemeCodes;
    }
}
