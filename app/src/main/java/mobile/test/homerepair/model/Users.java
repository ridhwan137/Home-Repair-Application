package mobile.test.homerepair.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Users {

    String name;
    String companyName;
    String serviceType;
    String userID;
    String serviceID;
    String email;
    String phone;
    String address1;
    String address2;
    String postcode;
    String state;
    String city;
    String password;
    String companyNo;
    String pictureURL;
    String userType;
    String clientID;
    String providerID;
    String dateRegistration = null;
    String hasServiceOffer;
    String userServiceRating;
    String totalUserRate;

    public String getTotalUserRate() {
        return totalUserRate;
    }

    public void setTotalUserRate(String totalUserRate) {
        this.totalUserRate = totalUserRate;
    }



    public String getUserServiceRating() {
        return userServiceRating;
    }

    public void setUserServiceRating(String userServiceRating) {
        this.userServiceRating = userServiceRating;
    }



    public String getHasServiceOffer() {
        return hasServiceOffer;
    }

    public void setHasServiceOffer(String hasServiceOffer) {
        this.hasServiceOffer = hasServiceOffer;
    }




    public String getDateRegistration() {

        /*
        This method is required because the original date is store in format "dd-MM-yyyy hh:mm a",
        when it is sorted the date will be sort by dd/day only. Thus, the date is not in align
        with each other. Therefore, the date need to change the format to "MM-dd-yyyy hh:mm a" so that it can be
        sort by month then by day.
         */

        try{

            //create SimpleDateFormat object with source string date format
            SimpleDateFormat sdfSource = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

            //parse the string into Date object
            Date parseDate = sdfSource.parse(dateRegistration);

            //create SimpleDateFormat object with desired date format
            SimpleDateFormat sdfDestination = new SimpleDateFormat("MM-dd-yyyy hh:mm a");

            //parse the date into another format
            dateRegistration = sdfDestination.format(parseDate);

        }catch (ParseException e){
            e.printStackTrace();
        }


        return dateRegistration;
    }

    public void setDateRegistration(String dateRegistration) {
        this.dateRegistration = dateRegistration;
    }




    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompanyNo() {
        return companyNo;
    }

    public void setCompanyNo(String companyNo) {
        this.companyNo = companyNo;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }




    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }


}
