package mobile.test.homerepair.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Appointment {

    String appointmentID;

    String clientID;
    String clientName;
    String clientEmail;
    String clientPhone;
    String clientPictureURL;
    String clientAddress1;
    String clientAddress2;
    String clientPostcode;
    String clientCity;
    String clientState;

    String providerID;
    String companyName;
    String companyEmail;
    String companyPhone;
    String companyServiceType;
    String providerPictureURL;
    String companyAddress1;
    String companyAddress2;
    String companyPostcode;
    String companyCity;
    String companyState;


    String message;
    String appointmentStatus;
    String requestStatus;
    String time;
    String receiptPictureURL;
    String totalPrice;

    String date = null;
    String dateCompleteAppointment = null;
    String dateServiceRate = null;


    public String getDateServiceRate() {

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
            Date parseDate = sdfSource.parse(dateServiceRate);

            //create SimpleDateFormat object with desired date format
            SimpleDateFormat sdfDestination = new SimpleDateFormat("MM-dd-yyyy hh:mm a");

            //parse the date into another format
            dateServiceRate = sdfDestination.format(parseDate);

        }catch (ParseException e){
            e.printStackTrace();
        }


        return dateServiceRate;
    }

    public void setDateServiceRate(String dateServiceRate) {
        this.dateServiceRate = dateServiceRate;
    }



    public String getDateCompleteAppointment() {

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
            Date parseDate = sdfSource.parse(dateCompleteAppointment);

            //create SimpleDateFormat object with desired date format
            SimpleDateFormat sdfDestination = new SimpleDateFormat("MM-dd-yyyy hh:mm a");

            //parse the date into another format
            dateCompleteAppointment = sdfDestination.format(parseDate);

        }catch (ParseException e){
            e.printStackTrace();
        }


        return dateCompleteAppointment;
    }

    public void setDateCompleteAppointment(String dateCompleteAppointment) {
        this.dateCompleteAppointment = dateCompleteAppointment;
    }



    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }


    public String getReceiptPictureURL() {
        return receiptPictureURL;
    }

    public void setReceiptPictureURL(String receiptPictureURL) {
        this.receiptPictureURL = receiptPictureURL;
    }




    public String getClientAddress1() {
        return clientAddress1;
    }

    public void setClientAddress1(String clientAddress1) {
        this.clientAddress1 = clientAddress1;
    }

    public String getClientAddress2() {
        return clientAddress2;
    }

    public void setClientAddress2(String clientAddress2) {
        this.clientAddress2 = clientAddress2;
    }

    public String getClientPostcode() {
        return clientPostcode;
    }

    public void setClientPostcode(String clientPostcode) {
        this.clientPostcode = clientPostcode;
    }

    public String getClientCity() {
        return clientCity;
    }

    public void setClientCity(String clientCity) {
        this.clientCity = clientCity;
    }

    public String getClientState() {
        return clientState;
    }

    public void setClientState(String clientState) {
        this.clientState = clientState;
    }

    public String getCompanyAddress1() {
        return companyAddress1;
    }

    public void setCompanyAddress1(String companyAddress1) {
        this.companyAddress1 = companyAddress1;
    }

    public String getCompanyAddress2() {
        return companyAddress2;
    }

    public void setCompanyAddress2(String companyAddress2) {
        this.companyAddress2 = companyAddress2;
    }

    public String getCompanyPostcode() {
        return companyPostcode;
    }

    public void setCompanyPostcode(String companyPostcode) {
        this.companyPostcode = companyPostcode;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public String getCompanyState() {
        return companyState;
    }

    public void setCompanyState(String companyState) {
        this.companyState = companyState;
    }

    public String getClientPictureURL() {
        return clientPictureURL;
    }

    public void setClientPictureURL(String clientPictureURL) {
        this.clientPictureURL = clientPictureURL;
    }

    public String getProviderPictureURL() {
        return providerPictureURL;
    }

    public void setProviderPictureURL(String providerPictureURL) {
        this.providerPictureURL = providerPictureURL;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }



    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }




    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyServiceType() {
        return companyServiceType;
    }

    public void setCompanyServiceType(String companyServiceType) {
        this.companyServiceType = companyServiceType;
    }

    public String getDate() {

        /*
        This method is required because the original date is store in format dd/MM/yyyy,
        when it is sorted the date will be sort by dd/day only. Thus, the date is not in align
        with each other. Therefore, the date need to change the format to MM-dd-yyyy so that it can be
        sort by month then by day.
         */

        try{

            //create SimpleDateFormat object with source string date format
            SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");

            //parse the string into Date object
            Date parseDate = sdfSource.parse(date);

            //create SimpleDateFormat object with desired date format
            SimpleDateFormat sdfDestination = new SimpleDateFormat("MM-dd-yyyy");

            //parse the date into another format
            date = sdfDestination.format(parseDate);

        }catch (ParseException e){
            e.printStackTrace();
        }


        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
