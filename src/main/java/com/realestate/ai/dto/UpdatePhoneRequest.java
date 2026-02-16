package com.realestate.ai.dto;

public class UpdatePhoneRequest {

    private String tempPhone;
    private String realPhone;
    private String name;

    public String getTempPhone() {
        return tempPhone;
    }

    public void setTempPhone(String tempPhone) {
        this.tempPhone = tempPhone;
    }

    public String getRealPhone() {
        return realPhone;
    }

    public void setRealPhone(String realPhone) {
        this.realPhone = realPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
