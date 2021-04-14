package com.lk.userapp.Model;

public class Address {
    String userDocId;
    String houseNo;
    String streetName;
    String cityName;
    String landmark;
    String personFirstName;
    String personLastName;
    String personMobile;
    String status;

    public Address(String userDocId, String houseNo, String streetName, String cityName, String landmark, String personFirstName, String personLastName, String personMobile, String status) {
        this.userDocId = userDocId;
        this.houseNo = houseNo;
        this.streetName = streetName;
        this.cityName = cityName;
        this.landmark = landmark;
        this.personFirstName = personFirstName;
        this.personLastName = personLastName;
        this.personMobile = personMobile;
        this.status = status;
    }

    public String getUserDocId() {
        return userDocId;
    }

    public void setUserDocId(String userDocId) {
        this.userDocId = userDocId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Address() {

    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPersonFirstName() {
        return personFirstName;
    }

    public void setPersonFirstName(String personFirstName) {
        this.personFirstName = personFirstName;
    }

    public String getPersonLastName() {
        return personLastName;
    }

    public void setPersonLastName(String personLastName) {
        this.personLastName = personLastName;
    }

    public String getPersonMobile() {
        return personMobile;
    }

    public void setPersonMobile(String personMobile) {
        this.personMobile = personMobile;
    }
}
