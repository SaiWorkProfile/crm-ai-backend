package com.realestate.ai.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name="projects")
public class Project {

@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
private Long id;

private String name;
private String type;
private String city;
private String location;
private String bhk;
private Boolean gatedCommunity;
private String priceStart;
private String amenities;
private Integer availableUnits;
private String status;
private String reraNumber;
private Boolean published=false;

private LocalDateTime createdAt;
private LocalDateTime updatedAt;

@PrePersist
void onCreate(){
createdAt=LocalDateTime.now();
}

@PreUpdate
void onUpdate(){
updatedAt=LocalDateTime.now();
}

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}

public String getCity() {
	return city;
}

public void setCity(String city) {
	this.city = city;
}

public String getLocation() {
	return location;
}

public void setLocation(String location) {
	this.location = location;
}

public String getBhk() {
	return bhk;
}

public void setBhk(String bhk) {
	this.bhk = bhk;
}

public Boolean getGatedCommunity() {
	return gatedCommunity;
}

public void setGatedCommunity(Boolean gatedCommunity) {
	this.gatedCommunity = gatedCommunity;
}

public String getPriceStart() {
	return priceStart;
}

public void setPriceStart(String priceStart) {
	this.priceStart = priceStart;
}

public String getAmenities() {
	return amenities;
}

public void setAmenities(String amenities) {
	this.amenities = amenities;
}

public Integer getAvailableUnits() {
	return availableUnits;
}

public void setAvailableUnits(Integer availableUnits) {
	this.availableUnits = availableUnits;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}

public String getReraNumber() {
	return reraNumber;
}

public void setReraNumber(String reraNumber) {
	this.reraNumber = reraNumber;
}

public Boolean getPublished() {
	return published;
}

public void setPublished(Boolean published) {
	this.published = published;
}

public LocalDateTime getCreatedAt() {
	return createdAt;
}

public void setCreatedAt(LocalDateTime createdAt) {
	this.createdAt = createdAt;
}

public LocalDateTime getUpdatedAt() {
	return updatedAt;
}

public void setUpdatedAt(LocalDateTime updatedAt) {
	this.updatedAt = updatedAt;
}


}
