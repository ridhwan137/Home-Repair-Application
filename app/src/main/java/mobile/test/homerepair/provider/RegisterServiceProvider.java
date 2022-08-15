package mobile.test.homerepair.provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mobile.test.homerepair.R;
import mobile.test.homerepair.main.Login;

public class RegisterServiceProvider extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;



    String companyName, companyNo,address1,address2,postcode,state,city,phone,email,password,confirmPassword;

    EditText et_providerName,
            et_providerNo,
            et_providerAddress1,
            et_providerAddress2,
            et_providerPostcode,
            et_providerState,
            et_providerCity,
            et_providerPhone,
            et_providerEmail,
            et_providerPassword,
            et_providerConfirmPassword;

//    EditText et_providerServiceType;
//    String serviceType;

    // Spinner Service Type
    String getSelectedServiceType;
    Spinner sp_serviceType;
    ArrayList<String> arrayServices;
    ArrayAdapter<String> adapterServices;
    QuerySnapshot services;


    Button btn_providerRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_service_provider);

        et_providerName = findViewById(R.id.et_providerName);
        et_providerNo = findViewById(R.id.et_providerNo);
//        et_providerServiceType = findViewById(R.id.et_providerServiceType);
        et_providerAddress1 = findViewById(R.id.et_providerAddress1);
        et_providerAddress2 = findViewById(R.id.et_providerAddress2);
        et_providerPostcode = findViewById(R.id.et_providerPostcode);
        et_providerState = findViewById(R.id.et_providerState);
        et_providerCity = findViewById(R.id.et_providerCity);
        et_providerPhone = findViewById(R.id.et_providerPhone);
        et_providerEmail = findViewById(R.id.et_providerEmail);
        et_providerPassword = findViewById(R.id.et_providerPassword);
        et_providerConfirmPassword = findViewById(R.id.et_providerConfirmPassword);

        btn_providerRegister = findViewById(R.id.btn_providerRegister);

        // authenthication db
        mAuth = FirebaseAuth.getInstance();


        //<-- Service Type
        sp_serviceType = findViewById(R.id.sp_serviceType);

        String [] arrayServiceList = {"Select Your Service Type"};

        arrayServices = new ArrayList<>(Arrays.asList(arrayServiceList));
        adapterServices = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, arrayServices){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }

        };


        adapterServices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_serviceType.setAdapter(adapterServices);
        sp_serviceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), adapterServices.getItem(position), Toast.LENGTH_SHORT).show();

                try {
                    Log.e("services->",services.getDocuments().get(position).getId());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        db.collection("service").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                services = queryDocumentSnapshots;
                if (queryDocumentSnapshots.size()>0){


                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                        arrayServices.add(documentSnapshot.getString("serviceType"));
                    }

                    adapterServices.notifyDataSetChanged();

                }else{
                    Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });
        /////


        // --> Service Type




        btn_providerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Initialize EditText to variable
                companyName = et_providerName.getText().toString();
                companyNo = et_providerNo.getText().toString();
//                serviceType = et_providerServiceType.getText().toString();
                address1 = et_providerAddress1.getText().toString();
                address2 = et_providerAddress2.getText().toString();
                postcode = et_providerPostcode.getText().toString();
                city = et_providerCity.getText().toString();
                state = et_providerState.getText().toString();
                phone = et_providerPhone.getText().toString();
                email = et_providerEmail.getText().toString();
                password = et_providerPassword.getText().toString();
                confirmPassword = et_providerConfirmPassword.getText().toString();


                //// <---- Validation ---- ////

                // <---- EditText Validation

                // If All Empty
                if (companyName.isEmpty() &&
                        companyNo.isEmpty() &&
//                        serviceType.isEmpty() &&
                        address1.isEmpty() &&
                        address2.isEmpty() &&
                        postcode.isEmpty() &&
                        city.isEmpty() &&
                        state.isEmpty() &&
                        phone.isEmpty() &&
                        email.isEmpty() &&
                        password.isEmpty() &&
                        confirmPassword.isEmpty() ){

                    et_providerName.setError("Require to fill");
                    et_providerNo.setError("Require to fill");
//                    et_providerServiceType.setError("Require to fill");
                    et_providerAddress1.setError("Require to fill");
                    et_providerAddress2.setError("Require to fill");
                    et_providerPostcode.setError("Require to fill");
                    et_providerCity.setError("Require to fill");
                    et_providerState.setError("Require to fill");
                    et_providerPhone.setError("Require to fill");
                    et_providerEmail.setError("Require to fill");
                    et_providerPassword.setError("Require to fill");
                    et_providerConfirmPassword.setError("Require to fill");

                    return;

                };

                // validation companyName
                if (companyName.isEmpty()){
                    et_providerName.setError("Require to fill");
                    return;
                }
//                if (!companyName.matches("^[a-zA-Z\\s]+$")){
//                    et_providerName.setError("Invalid character, input A~Z only");
//                    return;
//                }

                if (companyName.length()>100){
                    et_providerName.setError("Name should be less than 100 characters");
                    return;
                }

                // validation companyNo
                if (companyNo.isEmpty()){
                    et_providerNo.setError("Require to fill");
                    return;
                }
                if (!companyNo.matches("^[0-9]+$")){
                    et_providerNo.setError("Invalid character, input 0~9 only");
                    return;
                }

                if (companyNo.length() < 12){
                    Log.d("identity",companyNo);
                    et_providerNo.setError("Company No should be 12");
                    return;
                }
                if (companyNo.length() > 12){
                    Log.d("identity",companyNo);
                    et_providerNo.setError("Company No should be 12");
                    return;
                }


                // <---- Validation Spinner serviceType

                getSelectedServiceType = sp_serviceType.getSelectedItem().toString();

                Log.d("getServicesSpinner", getSelectedServiceType);

                if(getSelectedServiceType.equals("Select Your Service Type")){
                    Toast.makeText(getApplicationContext(), "Please Select Your Service Type", Toast.LENGTH_SHORT).show();
                    return;
                }
                // ----> Validation Spinner serviceType


/*

                // validation serviceType
                if (serviceType.isEmpty()){
                    et_providerServiceType.setError("Require to fill");
                    return;
                }

                if (!serviceType.matches("^[a-zA-Z\\s]+$")){
                    et_providerServiceType.setError("Invalid character, input A~Z only");
                    return;
                }

                if (serviceType.length()>20){
                    et_providerServiceType.setError("Should be less than 20 characters");
                    return;
                }
*/


/*
        // Check identityNo in database
        Map checkIdentityNo = databaseController.getADataFromTable("user","userIdentityNo LIKE '"+editTextDonorIC.getText().toString()+"' ");

        // If there is identityNo on database
        if (!checkIdentityNo.isEmpty()){
            editTextDonorIC.setError("This identity number is unavailable");
            return;
        }
*/


                //validation address1
                if (address1.isEmpty()){
                    et_providerAddress1.setError("Require to fill");
                    return;
                }

                //validation address2
                if (address2.isEmpty()){
                    et_providerAddress2.setError("Require to fill");
                    return;
                }

                // validate postcode
                if (postcode.isEmpty()){
                    et_providerPostcode.setError("Require to fill");
                    return;
                }
                if (!postcode.matches("^[0-9]+$")){
                    et_providerPostcode.setError("Invalid character, input 0~9 only");
                    return;
                }


                // validate city
                if (city.isEmpty()){
                    et_providerCity.setError("Require to fill");
                    return;
                }


                if (!city.matches("^[a-zA-Z\\s]+$")){
                    et_providerCity.setError("Invalid character, input A~Z only");
                    return;
                }

                if (city.length()>30){
                    et_providerCity.setError("Name should be less than 30 characters");
                    return;
                }

                // validation state
                if (state.isEmpty()){
                    et_providerState.setError("Require to fill");
                    return;
                }

                if (!state.matches("^[a-zA-Z\\s]+$")){
                    et_providerState.setError("Invalid character, input A~Z only");
                    return;
                }

                if (state.length()>30){
                    et_providerState.setError("Name should be less than 30 characters");
                    return;
                }




                // validation phone
                if (phone.isEmpty()){
                    et_providerPhone.setError("Require to fill");
                    return;
                }
                if (!phone.matches("^[0-9]+$")){
                    et_providerPhone.setError("Invalid character, input 0~9 only");
                    return;
                }

                if (phone.length() < 10 || phone.length() > 11 ){
                    et_providerPhone.setError("Phone number should be at least 10 and at most 11 characters");
                    return;
                }
/*
        // Check phone number in database
        Map checkPhoneNo = databaseController.getADataFromTable("user","contactNo LIKE '"+editTextDonorPhone.getText().toString()+"' ");

        // If there is phone on database
        if (!checkPhoneNo.isEmpty()){
            editTextDonorPhone.setError("This phone number is unavailable");
            return;
        }
*/

                //validation email
                if (email.isEmpty()){
                    et_providerEmail.setError("Require to fill");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    et_providerEmail.setError("Invalid email format");
                    return;
                }

/*
        // Check email in database
        Map checkEmailDatabase = databaseController.getADataFromTable("user","email LIKE '"+et_clientEmail.getText().toString()+"' ");

        // If there is email on database
        if (!checkEmailDatabase.isEmpty()){
            et_clientEmail.setError("This email is unavailable");
            return;
        }
*/


                //validation password
                if (password.isEmpty()){
                    et_providerPassword.setError("Require to fill");
                    return;
                }
                if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,25}$")){
                    et_providerPassword.setError("Password should contain 0~9, a~z, symbol, more than 8");
                    return;
                }


                //validation confirm password
                if (confirmPassword.isEmpty()){
                    et_providerConfirmPassword.setError("Require to fill");
                    return;
                }
                if (!confirmPassword.equals(password)){
                    et_providerConfirmPassword.setError("Password not same");
                    return;
                }
                // ----> EditText Validation


                //// ---- Validation ----> ////


                registerUser();
            }
        });

    }

    // Add to Authentication DB
    public void registerUser(){

/*
        email = et_providerEmail.getText().toString();
        password = et_providerPassword.getText().toString();
*/


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            user = mAuth.getCurrentUser();
                            //updateUI(user);

                            Toast.makeText(getApplicationContext(), "Authentication Success.",
                                    Toast.LENGTH_SHORT).show();

                            addUserInfo();

                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(getApplicationContext(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();

                            Toast.makeText(getApplicationContext(), "Registration Failed, Email Already Exist",
                                    Toast.LENGTH_SHORT).show();

                            //updateUI(null);
                        }
                    }
                });
    }

    // Add to FireStore DB
    public void addUserInfo(){

/*
        companyName = et_providerName.getText().toString();
        identityNo = et_providerNo.getText().toString();
        serviceType = et_providerServiceType.getText().toString();
        address1 = et_providerAddress1.getText().toString();
        address2 = et_providerAddress2.getText().toString();
        postcode = et_providerPostcode.getText().toString();
        state = et_providerState.getText().toString();
        city = et_providerCity.getText().toString();
        phone = et_providerPhone.getText().toString();
        email = et_providerEmail.getText().toString();
        password = et_providerPassword.getText().toString();
*/

        // format Date
        Date currentDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        String formatCurrentDate = simpleDateFormat.format(currentDate);
        Log.e("formatCurrentDate->",formatCurrentDate);



        Map<String, Object> user = new HashMap<>();
        user.put("companyName", companyName);
        user.put("companyNo", companyNo);
        user.put("serviceType", getSelectedServiceType);
        user.put("address1",address1);
        user.put("address2",address2);
        user.put("postcode",postcode);
        user.put("state",state);
        user.put("city", city);
        user.put("phone", phone);
        user.put("email", email);
        user.put("password", password);
        user.put("userType","serviceProvider");
        user.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        user.put("dateRegistration",formatCurrentDate);
        user.put("registrationStatus","pending");


        db.collection("users").document(this.user.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Successfully Register", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        //Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Unsuccessfully Register", Toast.LENGTH_SHORT).show();
                        //Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    public void backButton(View view) {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
    }
}