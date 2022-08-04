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
import android.util.Patterns;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mobile.test.homerepair.R;
import mobile.test.homerepair.client.ProfileClient;

public class UpdateUserClientInfoAdmin extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;
//    FirebaseUser user;

    Map<String, Object> user = new HashMap<>();

    EditText et_clientEditName,
            et_clientEditPhone,
            et_clientEditEmail,
            et_clientEditAddress1,
            et_clientEditAddress2,
            et_clientEditPostcode,
            et_clientEditState,
            et_clientEditCity,
            et_clientEditOldPassword,
            et_clientEditNewPassword,
            et_clientEditConfirmPassword;


    Button btn_clientEditUserUpdate,
            btn_clientEditAddressUpdate,
            btn_clientEditPasswordUpdate,
            btn_backToClientProfile;

    String TAG = "UserEditProfile";

    String oldPasswordFromDB;

    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String clientID;


    ImageView img_pictureClient;
    String url;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_client_info_admin);

        Intent intent = getIntent();
        clientID = intent.getStringExtra("userID");
        Log.e("testUserID",clientID);

        btn_backToClientProfile = findViewById(R.id.btn_backToClientProfile);

        img_pictureClient = findViewById(R.id.img_pictureClient);

        et_clientEditName = findViewById(R.id.et_clientEditName);
        et_clientEditPhone = findViewById(R.id.et_clientEditPhone);
        et_clientEditEmail = findViewById(R.id.et_clientEditEmail);

        et_clientEditAddress1 = findViewById(R.id.et_clientEditAddress1);
        et_clientEditAddress2 = findViewById(R.id.et_clientEditAddress2);
        et_clientEditPostcode = findViewById(R.id.et_clientEditPostcode);
        et_clientEditState = findViewById(R.id.et_clientEditState);
        et_clientEditCity = findViewById(R.id.et_clientEditCity);

        et_clientEditOldPassword = findViewById(R.id.et_clientEditOldPassword);
        et_clientEditNewPassword = findViewById(R.id.et_clientEditNewPassword);
        et_clientEditConfirmPassword = findViewById(R.id.et_clientEditConfirmPassword);

        btn_clientEditUserUpdate = findViewById(R.id.btn_clientEditUserUpdate);
        btn_clientEditAddressUpdate = findViewById(R.id.btn_clientEditAddressUpdate);
        btn_clientEditPasswordUpdate = findViewById(R.id.btn_clientEditPasswordUpdate);


        displayUserProfileInformation();


        btn_clientEditUserUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileUserInformation();
            }
        });


        btn_clientEditAddressUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileUserAddress();
            }
        });


        btn_clientEditPasswordUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileUserPassword();
            }
        });

        ////////////
    }

    public void displayUserProfileInformation(){
        DocumentReference docRef = db.collection("users").document(clientID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()){

                        try {
                            url = document.getData().get("pictureURL").toString();
                            Picasso.with(getApplicationContext()).load(url).into(img_pictureClient);
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("testPicture","No Picture");
                        }

                        et_clientEditName.setText(document.getData().get("name").toString());
                        et_clientEditPhone.setText(document.getData().get("phone").toString());
                        et_clientEditEmail.setText(document.getData().get("email").toString());

                        et_clientEditAddress1.setText(document.getData().get("address1").toString());
                        et_clientEditAddress2.setText(document.getData().get("address2").toString());
                        et_clientEditPostcode.setText(document.getData().get("postcode").toString());
                        et_clientEditState.setText(document.getData().get("state").toString());
                        et_clientEditCity.setText(document.getData().get("city").toString());

                        oldPasswordFromDB = document.getData().get("password").toString();

                        Log.e("getOldPasswordDB",oldPasswordFromDB);

                    }else{
                        // No document
                        Log.d(TAG,"no document");
                    }
                }else{
                    Log.d(TAG,"get failed with",task.getException());
                }

            }
        });



        btn_backToClientProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileClient.class);
                startActivity(intent);
            }
        });


        img_pictureClient.setOnClickListener(new View.OnClickListener() {
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


    }


    public void updateProfileUserInformation(){


        // Initialize EditText to variable
        String name = et_clientEditName.getText().toString();
        String phone = et_clientEditPhone.getText().toString();
        String email = et_clientEditEmail.getText().toString();


        //// <---- Validation ---- ////

        // <---- EditText Validation

        // If All Empty
        if (name.isEmpty() &&
                phone.isEmpty() &&
                email.isEmpty()){

            et_clientEditName.setError("Require to fill");
            et_clientEditPhone.setError("Require to fill");
            et_clientEditEmail.setError("Require to fill");

            return;

        };

        // validation Name
        if (name.isEmpty()){
            et_clientEditName.setError("Require to fill");
            return;
        }
        if (!name.matches("^[a-zA-Z\\s]+$")){
            et_clientEditName.setError("Invalid character, input A~Z only");
            return;
        }

        if (name.length()>100){
            et_clientEditName.setError("Name should be less than 100 characters");
            return;
        }



        // validation phone
        if (phone.isEmpty()){
            et_clientEditPhone.setError("Require to fill");
            return;
        }
        if (!phone.matches("^[0-9]+$")){
            et_clientEditPhone.setError("Invalid character, input 0~9 only");
            return;
        }

        if (phone.length() < 10 || phone.length() > 11 ){
            et_clientEditPhone.setError("Phone number should be at least 10 and at most 11 characters");
            return;
        }

        //validation email
        if (email.isEmpty()){
            et_clientEditEmail.setError("Require to fill");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_clientEditEmail.setError("Invalid email format");
            return;
        }

        // ----> EditText Validation


        //// ---- Validation ----> ////


        user.put("updateName",name);
        user.put("updatePhone",phone);
        user.put("updateEmail",email);

        DocumentReference docRef = db.collection("users").document(clientID);
        docRef.update("name",user.get("updateName"),
                "phone",user.get("updatePhone"),
                "email",user.get("updateEmail"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Update Field On Other Collection For User Information
                        updateFieldOnOtherCollectionUserInformation(name,phone);

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
        String address1 = et_clientEditAddress1.getText().toString();
        String address2 = et_clientEditAddress2.getText().toString();
        String postcode = et_clientEditPostcode.getText().toString();
        String state = et_clientEditState.getText().toString();
        String city = et_clientEditCity.getText().toString();


        //// <---- Validation ---- ////

        // <---- EditText Validation

        // If All Empty
        if (    address1.isEmpty() &&
                address2.isEmpty() &&
                postcode.isEmpty() &&
                state.isEmpty() &&
                city.isEmpty()){

            et_clientEditAddress1.setError("Require to fill");
            et_clientEditAddress2.setError("Require to fill");
            et_clientEditPostcode.setError("Require to fill");
            et_clientEditState.setError("Require to fill");
            et_clientEditCity.setError("Require to fill");

            return;

        };


        //validation address1
        if (address1.isEmpty()){
            et_clientEditAddress1.setError("Require to fill");
            return;
        }

        //validation address2
        if (address2.isEmpty()){
            et_clientEditAddress2.setError("Require to fill");
            return;
        }

        // validate postcode
        if (postcode.isEmpty()){
            et_clientEditPostcode.setError("Require to fill");
            return;
        }
        if (!postcode.matches("^[0-9]+$")){
            et_clientEditPostcode.setError("Invalid character, input 0~9 only");
            return;
        }


        // validate city
        if (city.isEmpty()){
            et_clientEditCity.setError("Require to fill");
            return;
        }


        if (!city.matches("^[a-zA-Z\\s]+$")){
            et_clientEditCity.setError("Invalid character, input A~Z only");
            return;
        }

        if (city.length()>30){
            et_clientEditCity.setError("Name should be less than 30 characters");
            return;
        }

        // validation state
        if (state.isEmpty()){
            et_clientEditState.setError("Require to fill");
            return;
        }

        if (!state.matches("^[a-zA-Z\\s]+$")){
            et_clientEditState.setError("Invalid character, input A~Z only");
            return;
        }

        if (state.length()>30){
            et_clientEditState.setError("Name should be less than 30 characters");
            return;
        }

        // ----> EditText Validation


        //// ---- Validation ----> ////

        user.put("updateAddress1",address1);
        user.put("updateAddress2",address2);
        user.put("updatePostcode",postcode);
        user.put("updateState",state);
        user.put("updateCity",city);

        DocumentReference docRef = db.collection("users").document(clientID);
        docRef.update("address1",user.get("updateAddress1"),
                "address2",user.get("updateAddress2"),
                "postcode",user.get("updatePostcode"),
                "state",user.get("updateState"),
                "city",user.get("updateCity"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Update Field On Other Collection For User Address
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
        String oldPassword = et_clientEditOldPassword.getText().toString();
        String newPassword = et_clientEditNewPassword.getText().toString();
        String confirmPassword = et_clientEditConfirmPassword.getText().toString();


        //// <---- Validation ---- ////

        // <---- EditText Validation

        // If All Empty
        if (    oldPassword.isEmpty() &&
                newPassword.isEmpty() &&
                confirmPassword.isEmpty() ){

            et_clientEditOldPassword.setError("Require to fill");
            et_clientEditNewPassword.setError("Require to fill");
            et_clientEditConfirmPassword.setError("Require to fill");

            return;

        };

        //validation old password
        if (oldPassword.isEmpty()){
            et_clientEditOldPassword.setError("Require to fill");
            return;
        }

        if (!oldPassword.equals(oldPasswordFromDB)){
            et_clientEditOldPassword.setError("Password not same");
            return;
        }



        //validation password
        if (newPassword.isEmpty()){
            et_clientEditNewPassword.setError("Require to fill");
            return;
        }

        if (!newPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,25}$")){
            et_clientEditNewPassword.setError("Password should contain 0~9, a~z, symbol, more than 8");
            return;
        }

        //validation confirm password
        if (confirmPassword.isEmpty()){
            et_clientEditConfirmPassword.setError("Require to fill");
            return;
        }

        Log.e("getNewPassword",newPassword);

        if (!confirmPassword.equals(newPassword)){
            et_clientEditConfirmPassword.setError("Password not same");
            return;
        }
        // ----> EditText Validation


        //// ---- Validation ----> ////




        user.put("updateNewPassword",newPassword);

        if (newPassword.equals(confirmPassword)){
            DocumentReference docRef = db.collection("users").document(clientID);
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
                    img_pictureClient.setImageBitmap(bitmap);



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

                                    img_pictureClient.setImageURI(null);
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

        DocumentReference nameRef = db.collection("users").document(clientID);
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
                .whereEqualTo("clientID",clientID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("updateFieldOnOtherCollection->", document.getId() + " => " + document.getData());

                                // Put update code in here
                                document.getReference().update("clientPictureURL", url);
                            }
                        } else {
                            Log.e("updateFieldOnOtherCollection->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public void updateFieldOnOtherCollectionUserInformation(String name, String phone){

        db.collection("appointment")
                .whereEqualTo("clientID",clientID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("updateFieldOnOtherCollection->", document.getId() + " => " + document.getData());

                                // Put update code in here
                                document.getReference().update("clientName", name);
                                document.getReference().update("clientPhone", phone);
                            }
                        } else {
                            Log.e("updateFieldOnOtherCollection->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public void updateFieldOnOtherCollectionUserAddress(String address,String address2,String postcode, String state, String city){

        db.collection("appointment")
                .whereEqualTo("clientID",clientID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("updateFieldOnOtherCollection->", document.getId() + " => " + document.getData());

                                // Put update code in here
                                document.getReference().update("clientAddress1", address);
                                document.getReference().update("clientAddress2", address2);
                                document.getReference().update("clientPostcode", postcode);
                                document.getReference().update("clientState", state);
                                document.getReference().update("clientCity", city);
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



    //////////////
}