package com.realestate.ai.dto;

public class AiLeadRequest {

    private String name;
    private String phone;
    private String requirement; // user's last AI query
    private Long projectId;     // optional
    private Long unitId;        // optional
    private String tempPhone;

    public AiLeadRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRequirement() { return requirement; }
    public void setRequirement(String requirement) { this.requirement = requirement; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }

	public String getTempPhone() {
		return tempPhone;
	}

	public void setTempPhone(String tempPhone) {
		this.tempPhone = tempPhone;
	}
    
    
}
