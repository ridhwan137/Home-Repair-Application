package mobile.test.homerepair.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import mobile.test.homerepair.R;
import mobile.test.homerepair.main.Login;
import mobile.test.homerepair.provider.ProfileServiceProvider;

public class RegisterClient extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;

    String name,address1,address2,postcode,state,city,phone,email,password,confirmPassword;


    EditText et_clientName,
            et_clientAddress1,
            et_clientAddress2,
            et_clientPostcode,
            et_clientState,
            et_clientCity,
            et_clientPhone,
            et_clientEmail,
            et_clientPassword,
            et_clientConfirmPassword;

    Button btn_clientRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client);

        et_clientName = findViewById(R.id.et_clientName);
        et_clientAddress1 = findViewById(R.id.et_clientAddress1);
        et_clientAddress2 = findViewById(R.id.et_clientAddress2);
        et_clientPostcode = findViewById(R.id.et_clientPostcode);
        et_clientState = findViewById(R.id.et_clientState);
        et_clientCity = findViewById(R.id.et_clientCity);
        et_clientPhone = findViewById(R.id.et_clientPhone);
        et_clientEmail = findViewById(R.id.et_clientEmail);
        et_clientPassword = findViewById(R.id.et_clientPassword);
        et_clientConfirmPassword = findViewById(R.id.et_clientConfirmPassword);

        btn_clientRegister = findViewById(R.id.btn_clientRegister);

        // authenthication db
        mAuth = FirebaseAuth.getInstance();



        btn_clientRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initialize EditText to variable
                name = et_clientName.getText().toString();
                address1 = et_clientAddress1.getText().toString();
                address2 = et_clientAddress2.getText().toString();
                postcode = et_clientPostcode.getText().toString();
                state = et_clientState.getText().toString();
                city = et_clientCity.getText().toString();
                phone = et_clientPhone.getText().toString();
                email = et_clientEmail.getText().toString();
                password = et_clientPassword.getText().toString();
                confirmPassword = et_clientConfirmPassword.getText().toString();


                //// <---- Validation ---- ////

                // <---- EditText Validation

                // If All Empty
                if (name.isEmpty() &&
                        address1.isEmpty() &&
                        address2.isEmpty() &&
                        postcode.isEmpty() &&
                        state.isEmpty() &&
                        city.isEmpty() &&
                        phone.isEmpty() &&
                        email.isEmpty() &&
                        password.isEmpty() &&
                        confirmPassword.isEmpty() ){

                    et_clientName.setError("Require to fill");
                    et_clientAddress1.setError("Require to fill");
                    et_clientAddress2.setError("Require to fill");
                    et_clientPostcode.setError("Require to fill");
                    et_clientState.setError("Require to fill");
                    et_clientCity.setError("Require to fill");
                    et_clientPhone.setError("Require to fill");
                    et_clientEmail.setError("Require to fill");
                    et_clientPassword.setError("Require to fill");
                    et_clientConfirmPassword.setError("Require to fill");

                    return;

                };

                // validation Name
                if (name.isEmpty()){
                    et_clientName.setError("Require to fill");
                    return;
                }
                if (!name.matches("^[a-zA-Z\\s]+$")){
                    et_clientName.setError("Invalid character, input A~Z only");
                    return;
                }

                if (name.length()>100){
                    et_clientName.setError("Name should be less than 100 characters");
                    return;
                }



//        // Check identityNo in database
//        Map checkIdentityNo = databaseController.getADataFromTable("user","userIdentityNo LIKE '"+editTextDonorIC.getText().toString()+"' ");
//
//        // If there is identityNo on database
//        if (!checkIdentityNo.isEmpty()){
//            editTextDonorIC.setError("This identity number is unavailable");
//            return;
//        }



                //validation address1
                if (address1.isEmpty()){
                    et_clientAddress1.setError("Require to fill");
                    return;
                }

                //validation address2
                if (address2.isEmpty()){
                    et_clientAddress2.setError("Require to fill");
                    return;
                }

                // validate postcode
                if (postcode.isEmpty()){
                    et_clientPostcode.setError("Require to fill");
                    return;
                }
                if (!postcode.matches("^[0-9]+$")){
                    et_clientPostcode.setError("Invalid character, input 0~9 only");
                    return;
                }


                // validate city
                if (city.isEmpty()){
                    et_clientCity.setError("Require to fill");
                    return;
                }


                if (!city.matches("^[a-zA-Z\\s]+$")){
                    et_clientCity.setError("Invalid character, input A~Z only");
                    return;
                }

                if (city.length()>30){
                    et_clientCity.setError("Name should be less than 30 characters");
                    return;
                }

                // validation state
                if (state.isEmpty()){
                    et_clientState.setError("Require to fill");
                    return;
                }

                if (!state.matches("^[a-zA-Z\\s]+$")){
                    et_clientState.setError("Invalid character, input A~Z only");
                    return;
                }

                if (state.length()>30){
                    et_clientState.setError("Name should be less than 30 characters");
                    return;
                }



                // validation phone
                if (phone.isEmpty()){
                    et_clientPhone.setError("Require to fill");
                    return;
                }
                if (!phone.matches("^[0-9]+$")){
                    et_clientPhone.setError("Invalid character, input 0~9 only");
                    return;
                }

                if (phone.length() < 10 || phone.length() > 11 ){
                    et_clientPhone.setError("Phone number should be at least 10 and at most 11 characters");
                    return;
                }

//        // Check phone number in database
//        Map checkPhoneNo = databaseController.getADataFromTable("user","contactNo LIKE '"+editTextDonorPhone.getText().toString()+"' ");
//
//        // If there is phone on database
//        if (!checkPhoneNo.isEmpty()){
//            editTextDonorPhone.setError("This phone number is unavailable");
//            return;
//        }


                //validation email
                if (email.isEmpty()){
                    et_clientEmail.setError("Require to fill");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    et_clientEmail.setError("Invalid email format");
                    return;
                }


//        // Check email in database
//        Map checkEmailDatabase = databaseController.getADataFromTable("user","email LIKE '"+et_clientEmail.getText().toString()+"' ");
//
//        // If there is email on database
//        if (!checkEmailDatabase.isEmpty()){
//            et_clientEmail.setError("This email is unavailable");
//            return;
//        }



                //validation password
                if (password.isEmpty()){
                    et_clientPassword.setError("Require to fill");
                    return;
                }
                if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,25}$")){
                    et_clientPassword.setError("Password should contain 0~9, a~z, symbol, more than 8");
                    return;
                }


                //validation confirm password
                if (confirmPassword.isEmpty()){
                    et_clientConfirmPassword.setError("Require to fill");
                    return;
                }
                if (!confirmPassword.equals(password)){
                    et_clientConfirmPassword.setError("Password not same");
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

/*        email = et_clientEmail.getText().toString();
        password = et_clientPassword.getText().toString();*/

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            user = mAuth.getCurrentUser();
                            //updateUI(user);

//                            Toast.makeText(getApplicationContext(), "Authentication Success.",
//                                    Toast.LENGTH_SHORT).show();

                            addUserInfo();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
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

       /* name = et_clientName.getText().toString();
        address1 = et_clientAddress1.getText().toString();
        address2 = et_clientAddress2.getText().toString();
        postcode = et_clientPostcode.getText().toString();
        state = et_clientState.getText().toString();
        city = et_clientCity.getText().toString();
        phone = et_clientPhone.getText().toString();
        email = et_clientEmail.getText().toString();
        password = et_clientPassword.getText().toString();
        confirmPassword = et_clientConfirmPassword.getText().toString();*/




        Map<String, Object> user = new HashMap<>();
        user.put("name",name);
        user.put("address1",address1);
        user.put("address2",address2);
        user.put("postcode",postcode);
        user.put("state",state);
        user.put("city", city);
        user.put("phone", phone);
        user.put("email", email);
        user.put("password", password);
        user.put("userType","client");
        user.put("pictureURL","https://firebasestorage.googleapis.com/v0/b/homerepair-88bbb.appspot.com/o/profilepicturenoimage.png?alt=media&token=https://firebasestorage.googleapis.com/v0/b/homerepair-88bbb.appspot.com/o/profilepicturenoimage.png?alt=media&token=fe7e401f-422e-4c95-9308-2e5a441e0bcb");
//        user.put("userType","admin");
        user.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());

        db.collection("users").document(this.user.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Successfully Register", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, "DocumentSnapshot successfully written!");
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
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