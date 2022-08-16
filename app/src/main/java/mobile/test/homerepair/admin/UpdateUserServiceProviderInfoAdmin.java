package mobile.test.homerepair.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import mobile.test.homerepair.R;
import mobile.test.homerepair.provider.ProfileServiceProvider;

public class UpdateUserServiceProviderInfoAdmin extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    StorageReference storageReference;

    Map<String, Object> user = new HashMap<>();

    EditText et_providerEditCompanyName,et_providerEditCompanyNo,et_providerEditPhone,
            et_providerEditAddress1,et_providerEditAddress2,et_providerEditPostcode,et_providerEditState,et_providerEditCity,
            et_providerEditNewPassword,et_providerEditConfirmPassword;

    Button btn_providerEditUserUpdate,btn_providerEditAddressUpdate,btn_resetPassword,
            btn_back;

    String TAG = "UserEditProfile";

    String providerID;

    ImageView img_pictureCompany;
    String url;
    ProgressDialog progressDialog;

    String emailToResetPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_infomation_admin);

        Intent intent = getIntent();
        providerID = intent.getStringExtra("userID");
        Log.e("testUserID", providerID);

        btn_back = findViewById(R.id.btn_back);

        img_pictureCompany = findViewById(R.id.img_pictureCompany);
        et_providerEditCompanyName = findViewById(R.id.et_providerEditCompanyName);
        et_providerEditCompanyNo = findViewById(R.id.et_providerEditCompanyNo);
        et_providerEditPhone = findViewById(R.id.et_providerEditPhone);

        et_providerEditAddress1 = findViewById(R.id.et_providerEditAddress1);
        et_providerEditAddress2 = findViewById(R.id.et_providerEditAddress2);
        et_providerEditPostcode = findViewById(R.id.et_providerEditPostcode);
        et_providerEditState = findViewById(R.id.et_providerEditState);
        et_providerEditCity = findViewById(R.id.et_providerEditCity);


        btn_providerEditUserUpdate = findViewById(R.id.btn_providerEditUserUpdate);
        btn_providerEditAddressUpdate = findViewById(R.id.btn_providerEditAddressUpdate);
        btn_resetPassword = findViewById(R.id.btn_resetPassword);

        displayUserProfileInformation();


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListRegisteredUserDetailServiceProviderAdmin.class);
                intent.putExtra("userID",providerID);
                startActivity(intent);

//                UpdateUserServiceProviderInfoAdmin.this.finish();
            }
        });


        img_pictureCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean pick = true;
                if(pick == true){

                    if(!checkCameraPermission()){
                        requestCameraPermissions();
                    }
                    else
                        pickImage();

                }
                else {

                    if (!checkStoragePermission()) {
                        requestStoragePermissions();
                    } else
                        pickImage();
                }
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


        btn_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordReset(emailToResetPassword);
            }
        });

        // End Bracket
    }


    public void sendPasswordReset(String userEmail) {

        mAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e("adminResetPassword->", "Email sent to : " + userEmail);
                            Toast.makeText(getApplicationContext(), "Reset Password has sent to user email: " + userEmail, Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }


    public void displayUserProfileInformation(){
        DocumentReference docRef = db.collection("users").document(providerID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()){

                        try {
                            url = document.getData().get("pictureURL").toString();
                            Picasso.with(getApplicationContext()).load(url).into(img_pictureCompany);
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("testPicture","No Picture");
                        }

                        et_providerEditCompanyName.setText(document.getData().get("companyName").toString());
                        et_providerEditCompanyNo.setText(document.getData().get("companyNo").toString());
                        et_providerEditPhone.setText(document.getData().get("phone").toString());

                        et_providerEditAddress1.setText(document.getData().get("address1").toString());
                        et_providerEditAddress2.setText(document.getData().get("address2").toString());
                        et_providerEditPostcode.setText(document.getData().get("postcode").toString());
                        et_providerEditState.setText(document.getData().get("state").toString());
                        et_providerEditCity.setText(document.getData().get("city").toString());

                        emailToResetPassword = document.getData().get("email").toString();


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
        String phone = et_providerEditPhone.getText().toString();


        //// <---- Validation ---- ////

        // <---- EditText Validation

        // If All Empty
        if (companyName.isEmpty() &&
                companyNo.isEmpty() &&
                phone.isEmpty()){

            et_providerEditCompanyName.setError("Require to fill");
            et_providerEditCompanyNo.setError("Require to fill");
            et_providerEditPhone.setError("Require to fill");

            return;

        };

        // validation companyName
        if (companyName.isEmpty()){
            et_providerEditCompanyName.setError("Require to fill");
            return;
        }
        if (!companyName.matches("^[a-zA-Z\\s]+$")){
            et_providerEditCompanyName.setError("Invalid character, input A~Z only");
            return;
        }

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



        // ----> EditText Validation


        //// ---- Validation ----> ////

        user.put("updateCompanyName",companyName);
        user.put("updateCompanyNo",companyNo);
        user.put("updatePhone",phone);

        DocumentReference docRef = db.collection("users").document(providerID);

        docRef.update("companyName",user.get("updateCompanyName"),
                "companyNo",user.get("updateCompanyNo"),
                "phone",user.get("updatePhone"),
                "email",user.get("updateEmail"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        updateFieldOnOtherCollectionUserInformation(companyName,phone,companyNo);

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

        DocumentReference docRef = db.collection("users").document(providerID);
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


        // Initialize EditText to variable
        String newPassword = et_providerEditNewPassword.getText().toString();
        String confirmPassword = et_providerEditConfirmPassword.getText().toString();


        //// <---- Validation ---- ////

        // <---- EditText Validation

        // If All Empty
        if (
                newPassword.isEmpty() &&
                confirmPassword.isEmpty() ){

            et_providerEditNewPassword.setError("Require to fill");
            et_providerEditConfirmPassword.setError("Require to fill");
            return;

        };


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
            DocumentReference docRef = db.collection("users").document(providerID);
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



    private void pickImage() {
        CropImage.activity().start(this);
    }

    private void requestStoragePermissions() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
    }

    private void requestCameraPermissions() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
    }

    private boolean checkStoragePermission() {
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res2;
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return  res1 && res2;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(resultUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    img_pictureCompany.setImageBitmap(bitmap);



                    ////////
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
                    Date now = new Date();
                    String fileName = formatter.format(now);
                    storageReference = FirebaseStorage.getInstance().getReference("images/"+ " profilepic " +fileName);

                    //upload the photo uploaded from camera to storage
                    storageReference.putFile(getImageUri(getApplicationContext(),bitmap))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    img_pictureCompany.setImageURI(null);
                                    Toast.makeText(getApplicationContext(),"Successfully Uploaded" + storageReference.getDownloadUrl(),Toast.LENGTH_SHORT).show();

                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            Log.e("URL ", "onSuccess: " + uri);
                                            url = uri.toString();
                                            updatePictureUrl();
                                        }
                                    });

                                    //Log.e("URL ", "onSuccess: " + storageReference.getDownloadUrl());
//                            if (progressDialog.isShowing())
//                                progressDialog.dismiss();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Failed to Upload",Toast.LENGTH_SHORT).show();


                        }
                    });


                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    //method to convert bitmap to Uri
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }



    public void updatePictureUrl(){

        DocumentReference nameRef = db.collection("users").document(providerID);
        nameRef
                .update("pictureURL", url)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        updateFieldOnOtherCollectionUserPicture(url);

                        Log.d("EditName", "DocumentSnapshot successfully updated!");

                        Toast.makeText(getApplicationContext(), "Successfully Update.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("EditName", "Error updating document", e);
                    }
                });
    }


    public void updateFieldOnOtherCollectionUserPicture(String url){

        db.collection("appointment")
                .whereEqualTo("providerID",providerID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("updateFieldOnOtherCollection->", document.getId() + " => " + document.getData());

                                // Put update code in here
                                document.getReference().update("providerPictureURL", url);
                            }
                        } else {
                            Log.e("updateFieldOnOtherCollection->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



    public void updateFieldOnOtherCollectionUserInformation(String name, String phone, String companyNo){

        db.collection("appointment")
                .whereEqualTo("providerID",providerID)
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
//                                document.getReference().update("companyServiceType", serviceType);
                                document.getReference().update("companyNo", companyNo);

                            }
                        } else {
                            Log.e("updateFieldOnOtherCollection->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void updateFieldOnOtherCollectionUserAddress(String address1,String address2,String postcode, String state, String city){

        db.collection("appointment")
                .whereEqualTo("providerID",providerID)
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



    // End Bracket
}