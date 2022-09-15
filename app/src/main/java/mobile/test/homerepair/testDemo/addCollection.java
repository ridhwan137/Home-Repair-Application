package mobile.test.homerepair.testDemo;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mobile.test.homerepair.admin.AdminAddService;

public class addCollection {


    public void addService(String serviceTypeID, String serviceType) {

        String TAG = "TAG";

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Random rand = new Random();
        int randomID = rand.nextInt(99999999) + 1;
        serviceTypeID = "serviceType" + randomID;

        Map<String, Object> serviceData = new HashMap<>();
        serviceData.put("serviceType", serviceType);
        serviceData.put("serviceTypeID", serviceTypeID);

        db.collection("service").document(serviceTypeID)
                .set(serviceData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    public void addServiceOffer(String serviceID, String serviceTypeID, String serviceName, String servicePrice) {

        String TAG = "TAG";

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Random rand = new Random();
        int randomID = rand.nextInt(99999999) + 1;
        serviceID = "service" + randomID;

        Map<String, Object> serviceOfferData = new HashMap<>();
        serviceOfferData.put("serviceID", serviceID);
        serviceOfferData.put("serviceTypeID", serviceTypeID);
        serviceOfferData.put("serviceName", serviceName);
        serviceOfferData.put("servicePrice", servicePrice);

        db.collection("serviceOffer").document(serviceID)
                .set(serviceOfferData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    public void addOrder(String orderID, String appointmentID, String clientID, String providerID,
                         String serviceName, String servicePrice) {

        String TAG = "TAG";

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Random rand = new Random();
        int randomID = rand.nextInt(99999999) + 1;
        orderID = "order" + randomID;

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderID", orderID);
        orderData.put("appointmentID", appointmentID);
        orderData.put("clientID", clientID);
        orderData.put("providerID", providerID);
        orderData.put("serviceName", serviceName);
        orderData.put("servicePrice", servicePrice);

        db.collection("order").document(orderID)
                .set(orderData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    public void addAppointment(String appointmentID, String clientID, String providerID, String date,
                               String time, String message, String appointmentStatus, String receiptURL,
                               String totalPrice, String dateComplete, String serviceRate, String dateServiceRate,
                               String dateCompleteAppointment) {

        String TAG = "TAG";

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Random rand = new Random();
        int randomID = rand.nextInt(99999999) + 1;
        appointmentID = "appointment" + randomID;

        Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("appointmentID", appointmentID);
        appointmentData.put("clientID", clientID);
        appointmentData.put("providerID", providerID);
        appointmentData.put("date", date);
        appointmentData.put("time", time);
        appointmentData.put("message", message);
        appointmentData.put("appointmentStatus", appointmentStatus);
        appointmentData.put("receiptURL", receiptURL);
        appointmentData.put("totalPrice", totalPrice);
        appointmentData.put("dateComplete", dateComplete);
        appointmentData.put("serviceRate", serviceRate);
        appointmentData.put("dateServiceRate", dateServiceRate);
        appointmentData.put("dateCompleteAppointment", dateCompleteAppointment);


        db.collection("appointment").document(appointmentID)
                .set(appointmentData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    public void addUser(String userID, String name, String email, String phone, String address1,
                        String address2, String postcode, String city, String state, String userType,
                        String pictureURL, String companyName, String companyNo, String serviceType,
                        String userServiceRating, String totalUserRate, String dateRegistration) {

        String TAG = "TAG";

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> userData = new HashMap<>();
        userData.put("userID", userID);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("address1", address1);
        userData.put("address2", address2);
        userData.put("postcode", postcode);
        userData.put("city", city);
        userData.put("state", state);
        userData.put("userType", userType);
        userData.put("pictureURL", pictureURL);
        userData.put("companyName", companyName);
        userData.put("companyNo", companyNo);
        userData.put("serviceType", serviceType);
        userData.put("userServiceRating", userServiceRating);
        userData.put("totalUserRate", totalUserRate);
        userData.put("dateRegistration", dateRegistration);


        db.collection("users").document(userID)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


}


