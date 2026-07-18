package com.logan.branchapi.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "branches", schema = "dbo")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "branch_code",
        nullable = false,
        unique = true,
        length = 50
    )
    private String branchCode;

    @Column(
        name = "branch_name",
        nullable = false,
        length = 255
    )
    private String branchName;

    @Column(
        name = "address",
        nullable = false,
        length = 1000
    )
    private String address;

    @Column(
        name = "city",
        nullable = false,
        length = 150
    )
    private String city;

    @Column(
        name = "country",
        nullable = false,
        length = 100
    )
    private String country;

    @Column(
        name = "latitude",
        nullable = false,
        precision = 10,
        scale = 7
    )
    private BigDecimal latitude;

    @Column(
        name = "longitude",
        nullable = false,
        precision = 10,
        scale = 7
    )
    private BigDecimal longitude;

    @Column(
        name = "phone_number",
        length = 30
    )
    private String phoneNumber;

    @Column(
        name = "active",
        nullable = false
    )
    private boolean active = true;

    @Column(
        name = "created_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
        name = "updated_at",
        nullable = false
    )
    private LocalDateTime updatedAt;

    public Branch() {
        // Required by JPA.
    }

    public Long getId() {
        return id;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}