package mobile.test.homerepair.provider;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import mobile.test.homerepair.R;

public class EditProfileServiceProvider extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    FirebaseUser user;

    Map<String, Object> user = new HashMap<>();

    EditText et_providerEditCompanyName,et_providerEditCompanyNo,et_providerEditServiceType,et_providerEditPhone,et_providerEditEmail,
            et_providerEditAddress1,et_providerEditAddress2,et_providerEditPostcode,et_providerEditState,et_providerEditCity,
            et_providerEditOldPassword,et_providerEditNewPassword,et_providerEditConfirmPassword;

    Button btn_providerEditUserUpdate,btn_providerEditAddressUpdate,btn_providerEditPasswordUpdate,
            btn_backToProviderProfile;

    String TAG = "UserEditProfile";

    String oldPasswordFromDB;

    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_service_provider);

        btn_backToProviderProfile = findViewById(R.id.btn_backToProviderProfile);

        et_providerEditCompanyName = findViewById(R.id.et_providerEditCompanyName);
        et_providerEditCompanyNo = findViewById(R.id.et_providerEditCompanyNo);
        et_providerEditServiceType = findViewById(R.id.et_providerEditServiceType);
        et_providerEditPhone = findViewById(R.id.et_providerEditPhone);
        et_providerEditEmail = findViewById(R.id.et_providerEditEmail);

        et_providerEditAddress1 = findViewById(R.id.et_providerEditAddress1);
        et_providerEditAddress2 = findViewById(R.id.et_providerEditAddress2);
        et_providerEditPostcode = findViewById(R.id.et_providerEditPostcode);
        et_providerEditState = findViewById(R.id.et_providerEditState);
        et_providerEditCity = findViewById(R.id.et_providerEditCity);

        et_providerEditOldPassword = findViewById(R.id.et_providerEditOldPassword);
        et_providerEditNewPassword = findViewById(R.id.et_providerEditNewPassword);
        et_providerEditConfirmPassword = findViewById(R.id.et_providerEditConfirmPassword);

        btn_providerEditUserUpdate = findViewById(R.id.btn_providerEditUserUpdate);
        btn_providerEditAddressUpdate = findViewById(R.id.btn_providerEditAddressUpdate);
        btn_providerEditPasswordUpdate = findViewById(R.id.btn_providerEditPasswordUpdate);


        displayUserProfileInformation();


        btn_backToProviderProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileServiceProvider.class);
                startActivity(intent);
                EditProfileServiceProvider.this.finish();
            }
        });


        btn_providerEditUserUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileUserInformation();
            }
        });


        btn_providerEditAddressUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileUserAddress();
            }
        });


        btn_providerEditPasswordUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileUserPassword();
            }
        });





    }


    public void displayUserProfileInformation(){
        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()){

                        et_providerEditCompanyName.setText(document.getData().get("companyName").toString());
                        et_providerEditCompanyNo.setText(document.getData().get("companyNo").toString());
                        et_providerEditPhone.setText(document.getData().get("phone").toString());
                        et_providerEditServiceType.setText(document.getData().get("serviceType").toString());
                        et_providerEditEmail.setText(document.getData().get("email").toString());

                        et_providerEditAddress1.setText(document.getData().get("address1").toString());
                        et_providerEditAddress2.setText(document.getData().get("address2").toString());
                        et_providerEditPostcode.setText(document.getData().get("postcode").toString());
                        et_providerEditState.setText(document.getData().get("state").toString());
                        et_providerEditCity.setText(document.getData().get("city").toString());

                        oldPasswordFromDB = document.getData().get("password").toString();


                    }else{
                        // No document
                        Log.d(TAG,"no document");
                    }
                }else{
                    Log.d(TAG,"get failed with",task.getException());
                }

            }
        });
    }


    public void updateProfileUserInformation(){


        // Initialize EditText to variable
        String companyName = et_providerEditCompanyName.getText().toString();
        String companyNo = et_providerEditCompanyNo.getText().toString();
        String serviceType = et_providerEditServiceType.getText().toString();
        String phone = et_providerEditPhone.getText().toString();
        String email = et_providerEditEmail.getText().toString();


        //// <---- Validation ---- ////

        // <---- EditText Validation

        // If All Empty
        if (companyName.isEmpty() &&
                companyNo.isEmpty() &&
                phone.isEmpty() &&
                email.isEmpty()){

            et_providerEditCompanyName.setError("Require to fill");
            et_providerEditCompanyNo.setError("Require to fill");
            et_providerEditPhone.setError("Require to fill");
            et_providerEditEmail.setError("Require to fill");

            return;

        };

        // validation companyName
        if (companyName.isEmpty()){
            et_providerEditCompanyName.setError("Require to fill");
            return;
        }
//        if (!companyName.matches("^[a-zA-Z\\s]+$")){
//            et_providerEditCompanyName.setError("Invalid character, input A~Z only");
//            return;
//        }

        if (companyName.length()>100){
            et_providerEditCompanyName.setError("Name should be less than 100 characters");
            return;
        }

        // validation companyNo
        if (companyNo.isEmpty()){
            et_providerEditCompanyNo.setError("Require to fill");
            return;
        }
        if (!companyNo.matches("^[0-9]+$")){
            et_providerEditCompanyNo.setError("Invalid character, input 0~9 only");
            return;
        }

        if (companyNo.length() < 12){
            Log.d("identity",companyNo);
            et_providerEditCompanyNo.setError("Company No should be 12");
            return;
        }

        if (companyNo.length() > 12){
            Log.d("identity",companyNo);
            et_providerEditCompanyNo.setError("Company No should be 12");
            return;
        }


        // validation phone
        if (phone.isEmpty()){
            et_providerEditPhone.setError("Require to fill");
            return;
        }
        if (!phone.matches("^[0-9]+$")){
            et_providerEditPhone.setError("Invalid character, input 0~9 only");
            return;
        }

        if (phone.length() < 10 || phone.length() > 11 ){
            et_providerEditPhone.setError("Phone number should be at least 10 and at most 11 characters");
            return;
        }


        //validation email
        if (email.isEmpty()){
            et_providerEditEmail.setError("Require to fill");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_providerEditEmail.setError("Invalid email format");
            return;
        }

        // ----> EditText Validation


        //// ---- Validation ----> ////

        user.put("updateCompanyName",companyName);
        user.put("updateCompanyNo",companyNo);
        user.put("updateServiceType",serviceType);
        user.put("updatePhone",phone);
        user.put("updateEmail",email);

        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        docRef.update("companyName",user.get("updateCompanyName"),
                "companyNo",user.get("updateCompanyNo"),
                "serviceType",user.get("updateServiceType"),
                "phone",user.get("updatePhone"),
                "email",user.get("updateEmail"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        updateFieldOnOtherCollectionUserInformation(companyName,phone,serviceType);

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
        String address1 = et_providerEditAddress1.getText().toString();
        String address2 = et_providerEditAddress2.getText().toString();
        String postcode = et_providerEditPostcode.getText().toString();
        String city = et_providerEditCity.getText().toString();
        String state = et_providerEditState.getText().toString();


        //// <---- Validation ---- ////

        // <---- EditText Validation

        // If All Empty
        if (    address1.isEmpty() &&
                address2.isEmpty() &&
                postcode.isEmpty() &&
                city.isEmpty() &&
                state.isEmpty()){

            et_providerEditAddress1.setError("Require to fill");
            et_providerEditAddress2.setError("Require to fill");
            et_providerEditPostcode.setError("Require to fill");
            et_providerEditCity.setError("Require to fill");
            et_providerEditState.setError("Require to fill");

            return;
        };



        //validation address1
        if (address1.isEmpty()){
            et_providerEditAddress1.setError("Require to fill");
            return;
        }

        //validation address2
        if (address2.isEmpty()){
            et_providerEditAddress2.setError("Require to fill");
            return;
        }

        // validate postcode
        if (postcode.isEmpty()){
            et_providerEditPostcode.setError("Require to fill");
            return;
        }
        if (!postcode.matches("^[0-9]+$")){
            et_providerEditPostcode.setError("Invalid character, input 0~9 only");
            return;
        }


        // validate city
        if (city.isEmpty()){
            et_providerEditCity.setError("Require to fill");
            return;
        }


        if (!city.matches("^[a-zA-Z\\s]+$")){
            et_providerEditCity.setError("Invalid character, input A~Z only");
            return;
        }

        if (city.length()>30){
            et_providerEditCity.setError("Name should be less than 30 characters");
            return;
        }

        // validation state
        if (state.isEmpty()){
            et_providerEditState.setError("Require to fill");
            return;
        }

        if (!state.matches("^[a-zA-Z\\s]+$")){
            et_providerEditState.setError("Invalid character, input A~Z only");
            return;
        }

        if (state.length()>30){
            et_providerEditState.setError("Name should be less than 30 characters");
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

                        updateFieldOnOtherCollectionUserAddress(address1,address2,postcode,state,city);

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

        Log.e("getOldPasswordFromUpdatePasswordFC",oldPasswordFromDB);

        // Initialize EditText to variable
        String oldPassword = et_providerEditOldPassword.getText().toString();
        String newPassword = et_providerEditNewPassword.getText().toString();
        String confirmPassword = et_providerEditConfirmPassword.getText().toString();


        //// <---- Validation ---- ////

        // <---- EditText Validation

        // If All Empty
        if (    oldPassword.isEmpty() &&
                newPassword.isEmpty() &&
                confirmPassword.isEmpty() ){

            et_providerEditOldPassword.setError("Require to fill");
            et_providerEditNewPassword.setError("Require to fill");
            et_providerEditConfirmPassword.setError("Require to fill");
            return;

        };

        //validation old password
        if (oldPassword.isEmpty()){
            et_providerEditOldPassword.setError("Require to fill");
            return;
        }

        if (!oldPassword.equals(oldPasswordFromDB)){
            et_providerEditOldPassword.setError("Password not same");
            return;
        }



        //validation password
        if (newPassword.isEmpty()){
            et_providerEditNewPassword.setError("Require to fill");
            return;
        }

        if (!newPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,25}$")){
            et_providerEditNewPassword.setError("Password should contain 0~9, a~z, symbol, more than 8");
            return;
        }

        //validation confirm password
        if (confirmPassword.isEmpty()){
            et_providerEditConfirmPassword.setError("Require to fill");
            return;
        }

        Log.e("getNewPassword",newPassword);

        if (!confirmPassword.equals(newPassword)){
            et_providerEditConfirmPassword.setError("Password not same");
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

                            Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot successfully updated!");




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

    public void updateFieldOnOtherCollectionUserInformation(String name, String phone, String serviceType){

        db.collection("appointment")
                .whereEqualTo("providerID",currentUserID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("updateFieldOnOtherCollection->", document.getId() + " => " + document.getData());

                                // Put update code in here
                                document.getReference().update("companyName", name);
                                document.getReference().update("companyPhone", phone);
                                document.getReference().update("companyServiceType", serviceType);
//                                document.getReference().update("companyNo", companyNo);

                            }
                        } else {
                            Log.e("updateFieldOnOtherCollection->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void updateFieldOnOtherCollectionUserAddress(String address1,String address2,String postcode, String state, String city){

        db.collection("appointment")
                .whereEqualTo("providerID",currentUserID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("updateFieldOnOtherCollection->", document.getId() + " => " + document.getData());

                                // Put update code in here
                                document.getReference().update("companyAddress1", address1);
                                document.getReference().update("companyAddress2", address2);
                                document.getReference().update("companyPostcode", postcode);
                                document.getReference().update("companyState", state);
                                document.getReference().update("companyCity", city);
                            }
                        } else {
                            Log.e("updateFieldOnOtherCollection->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }




    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}