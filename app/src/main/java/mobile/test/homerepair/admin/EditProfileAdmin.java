package mobile.test.homerepair.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import mobile.test.homerepair.R;
import mobile.test.homerepair.client.ProfileClient;

public class EditProfileAdmin extends AppCompatActivity {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    FirebaseUser user;

    Map<String, Object> user = new HashMap<>();

    EditText et_adminEditName,
            et_adminEditPhone,
            et_adminEditEmail,
            et_adminEditAddress1,
            et_adminEditAddress2,
            et_adminEditPostcode,
            et_adminEditState,
            et_adminEditCity,
            et_adminEditOldPassword,
            et_adminEditNewPassword,
            et_adminEditConfirmPassword;


    Button btn_adminEditUserUpdate,
            btn_adminEditAddressUpdate,
            btn_adminEditPasswordUpdate,
            btn_backToAdminProfile;

    String TAG = "UserEditProfile";

//    String oldPasswordFromDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_admin);


        btn_backToAdminProfile = findViewById(R.id.btn_backToAdminProfile);

        et_adminEditName = findViewById(R.id.et_adminEditName);
        et_adminEditPhone = findViewById(R.id.et_adminEditPhone);
//        et_adminEditEmail = findViewById(R.id.et_adminEditEmail);

        et_adminEditAddress1 = findViewById(R.id.et_adminEditAddress1);
        et_adminEditAddress2 = findViewById(R.id.et_adminEditAddress2);
        et_adminEditPostcode = findViewById(R.id.et_adminEditPostcode);
        et_adminEditState = findViewById(R.id.et_adminEditState);
        et_adminEditCity = findViewById(R.id.et_adminEditCity);

//        et_adminEditOldPassword = findViewById(R.id.et_adminEditOldPassword);
        et_adminEditNewPassword = findViewById(R.id.et_adminEditNewPassword);
        et_adminEditConfirmPassword = findViewById(R.id.et_adminEditConfirmPassword);

        btn_adminEditUserUpdate = findViewById(R.id.btn_adminEditUserUpdate);
        btn_adminEditAddressUpdate = findViewById(R.id.btn_adminEditAddressUpdate);
        btn_adminEditPasswordUpdate = findViewById(R.id.btn_adminEditPasswordUpdate);


        displayUserProfileInformation();


        btn_adminEditUserUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileUserInformation();
            }
        });


        btn_adminEditAddressUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileUserAddress();
            }
        });


        btn_adminEditPasswordUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileUserPassword();
            }
        });

        // End Bracket
    }

    public void displayUserProfileInformation(){
        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()){

                        et_adminEditName.setText(document.getData().get("name").toString());
                        et_adminEditPhone.setText(document.getData().get("phone").toString());
//                        et_adminEditEmail.setText(document.getData().get("email").toString());

                        et_adminEditAddress1.setText(document.getData().get("address1").toString());
                        et_adminEditAddress2.setText(document.getData().get("address2").toString());
                        et_adminEditPostcode.setText(document.getData().get("postcode").toString());
                        et_adminEditState.setText(document.getData().get("state").toString());
                        et_adminEditCity.setText(document.getData().get("city").toString());

//                        oldPasswordFromDB = document.getData().get("password").toString();
//                        Log.e("getOldPasswordDB",oldPasswordFromDB);

                    }else{
                        // No document
                        Log.d(TAG,"no document");
                    }
                }else{
                    Log.d(TAG,"get failed with",task.getException());
                }

            }
        });



        btn_backToAdminProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileAdmin.class);
                startActivity(intent);
            }
        });


    }


    public void updateProfileUserInformation(){


        // Initialize EditText to variable
        String name = et_adminEditName.getText().toString();
        String phone = et_adminEditPhone.getText().toString();
//        String email = et_adminEditEmail.getText().toString();


        //// <---- Validation ---- ////

        // <---- EditText Validation

        // If All Empty
        if (name.isEmpty() &&
                phone.isEmpty()
//                && email.isEmpty()
        ){

            et_adminEditName.setError("Require to fill");
            et_adminEditPhone.setError("Require to fill");
//            et_adminEditEmail.setError("Require to fill");

            return;

        };

        // validation Name
        if (name.isEmpty()){
            et_adminEditName.setError("Require to fill");
            return;
        }
        if (!name.matches("^[a-zA-Z\\s]+$")){
            et_adminEditName.setError("Invalid character, input A~Z only");
            return;
        }

        if (name.length()>100){
            et_adminEditName.setError("Name should be less than 100 characters");
            return;
        }



        // validation phone
        if (phone.isEmpty()){
            et_adminEditPhone.setError("Require to fill");
            return;
        }
        if (!phone.matches("^[0-9]+$")){
            et_adminEditPhone.setError("Invalid character, input 0~9 only");
            return;
        }

        if (phone.length() < 10 || phone.length() > 11 ){
            et_adminEditPhone.setError("Phone number should be at least 10 and at most 11 characters");
            return;
        }

/*        //validation email
        if (email.isEmpty()){
            et_adminEditEmail.setError("Require to fill");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_adminEditEmail.setError("Invalid email format");
            return;
        }*/

        // ----> EditText Validation


        //// ---- Validation ----> ////


        user.put("updateName",name);
        user.put("updatePhone",phone);
//        user.put("updateEmail",email);

        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        docRef.update("name",user.get("updateName")
                ,"phone",user.get("updatePhone")
//                ,"email",user.get("updateEmail")
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });

    }



    public void updateProfileUserAddress(){


        // Initialize EditText to variable
        String address1 = et_adminEditAddress1.getText().toString();
        String address2 = et_adminEditAddress2.getText().toString();
        String postcode = et_adminEditPostcode.getText().toString();
        String state = et_adminEditState.getText().toString();
        String city = et_adminEditCity.getText().toString();


        //// <---- Validation ---- ////

        // <---- EditText Validation

        // If All Empty
        if (    address1.isEmpty() &&
                address2.isEmpty() &&
                postcode.isEmpty() &&
                state.isEmpty() &&
                city.isEmpty()){

            et_adminEditAddress1.setError("Require to fill");
            et_adminEditAddress2.setError("Require to fill");
            et_adminEditPostcode.setError("Require to fill");
            et_adminEditState.setError("Require to fill");
            et_adminEditCity.setError("Require to fill");

            return;

        };


        //validation address1
        if (address1.isEmpty()){
            et_adminEditAddress1.setError("Require to fill");
            return;
        }

        //validation address2
        if (address2.isEmpty()){
            et_adminEditAddress2.setError("Require to fill");
            return;
        }

        // validate postcode
        if (postcode.isEmpty()){
            et_adminEditPostcode.setError("Require to fill");
            return;
        }
        if (!postcode.matches("^[0-9]+$")){
            et_adminEditPostcode.setError("Invalid character, input 0~9 only");
            return;
        }


        // validate city
        if (city.isEmpty()){
            et_adminEditCity.setError("Require to fill");
            return;
        }


        if (!city.matches("^[a-zA-Z\\s]+$")){
            et_adminEditCity.setError("Invalid character, input A~Z only");
            return;
        }

        if (city.length()>30){
            et_adminEditCity.setError("Name should be less than 30 characters");
            return;
        }

        // validation state
        if (state.isEmpty()){
            et_adminEditState.setError("Require to fill");
            return;
        }

        if (!state.matches("^[a-zA-Z\\s]+$")){
            et_adminEditState.setError("Invalid character, input A~Z only");
            return;
        }

        if (state.length()>30){
            et_adminEditState.setError("Name should be less than 30 characters");
            return;
        }

        // ----> EditText Validation


        //// ---- Validation ----> ////

        user.put("updateAddress1",address1);
        user.put("updateAddress2",address2);
        user.put("updatePostcode",postcode);
        user.put("updateState",state);
        user.put("updateCity",city);

        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.update("address1",user.get("updateAddress1"),
                "address2",user.get("updateAddress2"),
                "postcode",user.get("updatePostcode"),
                "state",user.get("updateState"),
                "city",user.get("updateCity"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });

    }



    public void updateProfileUserPassword(){

//        Log.e("getOldPasswordFromUpdatePasswordFC",oldPasswordFromDB);

        // Initialize EditText to variable
//        String oldPassword = et_adminEditOldPassword.getText().toString();
        String newPassword = et_adminEditNewPassword.getText().toString();
        String confirmPassword = et_adminEditConfirmPassword.getText().toString();

        //// <---- Validation ---- ////

        // <---- EditText Validation

        // If All Empty
        if (    newPassword.isEmpty() &&
                confirmPassword.isEmpty() ){

            et_adminEditNewPassword.setError("Require to fill");
            et_adminEditConfirmPassword.setError("Require to fill");

            return;

        };

/*        if (    oldPassword.isEmpty() &&
                newPassword.isEmpty() &&
                confirmPassword.isEmpty() ){

            et_adminEditOldPassword.setError("Require to fill");
            et_adminEditNewPassword.setError("Require to fill");
            et_adminEditConfirmPassword.setError("Require to fill");

            return;

        };*/


        //validation old password
/*        if (oldPassword.isEmpty()){
            et_adminEditOldPassword.setError("Require to fill");
            return;
        }

        if (!oldPassword.equals(oldPasswordFromDB)){
            et_adminEditOldPassword.setError("Password not same");
            return;
        }
        */



        //validation password
        if (newPassword.isEmpty()){
            et_adminEditNewPassword.setError("Require to fill");
            return;
        }

        if (!newPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,25}$")){
            et_adminEditNewPassword.setError("Password should contain 0~9, a~z, symbol, more than 8");
            return;
        }

        //validation confirm password
        if (confirmPassword.isEmpty()){
            et_adminEditConfirmPassword.setError("Require to fill");
            return;
        }

        Log.e("getNewPassword",newPassword);

        if (!confirmPassword.equals(newPassword)){
            et_adminEditConfirmPassword.setError("Password not same");
            return;
        }
        // ----> EditText Validation


        //// ---- Validation ----> ////




        user.put("updateNewPassword",newPassword);

        if (newPassword.equals(confirmPassword)){
            DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            docRef.update("password",user.get("updateNewPassword"))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot successfully updated!");

                            //Update Password in Authentication
                            FirebaseUser updateUserPassword = FirebaseAuth.getInstance().getCurrentUser();
                            updateUserPassword.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Log.e("updatePassword", "User password updated.");
                                    }
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error updating document", e);
                }
            });


        }else{
            Toast.makeText(getApplicationContext(), "Password not same", Toast.LENGTH_SHORT).show();
        }


    }



    // End Bracket
}