package mobile.test.homerepair.provider;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import mobile.test.homerepair.R;
import mobile.test.homerepair.main.Login;
import mobile.test.homerepair.model.Order;
import mobile.test.homerepair.provider.EditProfileServiceProvider;

public class ProfileServiceProvider extends AppCompatActivity {


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    StorageReference storageReference;

    Button btLogout;

    CircleImageView ivProfile;
    String url;

    //    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;

    EditText et_providerProfileCompanyName,
            et_providerProfileCompanyNo,
            et_providerProfileServiceType,
            et_providerProfileAddress,et_providerProfilePhone,et_providerProfileEmail;

    TextView txt_providerAccountType;

    Button btn_providerEditProfile,btn_providerTotalIncome ;

    String TAG = "UserProfile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_service_provider);



        //Iniatialize and Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Menu Selected
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_addService:
                        startActivity(new Intent(getApplicationContext(),AddServices.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_appointmentSchedule:
                        startActivity(new Intent(getApplicationContext(),AppointmentScheduleServiceProviderTabLayout.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_historyAppointmentSchedule:
                        startActivity(new Intent(getApplicationContext(),HistoryAppointmentServiceProviderTabLayout.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_profile:
//                        startActivity(new Intent(getApplicationContext(),ProfileServiceProvider.class));
//                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });


        ivProfile = findViewById(R.id.ivProfile);
        txt_providerAccountType = findViewById(R.id.txt_providerAccountType);

        et_providerProfileCompanyName = findViewById(R.id.et_providerProfileCompanyName);
        et_providerProfileCompanyNo = findViewById(R.id.et_providerProfileCompanyNo);
        et_providerProfileServiceType = findViewById(R.id.et_providerProfileServiceType);
        et_providerProfileAddress = findViewById(R.id.et_providerProfileAddress);
        et_providerProfilePhone = findViewById(R.id.et_providerProfilePhone);
        et_providerProfileEmail = findViewById(R.id.et_providerProfileEmail);
        btn_providerTotalIncome = findViewById(R.id.btn_providerTotalIncome);

        btn_providerEditProfile = findViewById(R.id.btn_providerEditProfile);
        btLogout = findViewById(R.id.btLogout);

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });


        displayUserProfileInformation();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        totalIncome(userID);

        ivProfile.setOnClickListener(new View.OnClickListener() {
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



        btn_providerEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfileServiceProvider.class);
                startActivity(intent);
            }
        });

        btn_providerTotalIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryAppointmentServiceProviderTabLayout.class);
                startActivity(intent);
            }
        });


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
                    ivProfile.setImageBitmap(bitmap);



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

                                    ivProfile.setImageURI(null);
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

        DocumentReference nameRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        nameRef
                .update("pictureURL", url)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        updateFieldOnOtherCollectionUserPicture(url);

                        Log.d("EditName", "DocumentSnapshot successfully updated!");

//                        Toast.makeText(getApplicationContext(), "Name updated successfully.", Toast.LENGTH_SHORT).show();
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
                .whereEqualTo("providerID",FirebaseAuth.getInstance().getCurrentUser().getUid())
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




    public void displayUserProfileInformation(){
        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()){

//                        txt_providerAccountType.setText(document.getData().get("userType").toString());

                        if(document.getData().get("userType").toString().equals("serviceProvider")){
                            txt_providerAccountType.setText("Service Provider");
                        }

                        et_providerProfileCompanyName.setText(document.getData().get("companyName").toString());
                        et_providerProfileCompanyNo.setText(document.getData().get("companyNo").toString());
                        et_providerProfileServiceType.setText(document.getData().get("serviceType").toString());
                        et_providerProfilePhone.setText(document.getData().get("phone").toString());
                        et_providerProfileEmail.setText(document.getData().get("email").toString());


                        // Get Full Address

                        String fullAddress;

                        fullAddress = document.getData().get("address1").toString() + ", ";
                        fullAddress += document.getData().get("address2").toString() + ", \n";
                        fullAddress += document.getData().get("postcode").toString() + " ";
                        fullAddress += document.getData().get("city").toString() + ", \n";
                        fullAddress += document.getData().get("state").toString() ;

                        Log.e("testAddress",fullAddress);

                        et_providerProfileAddress.setText(fullAddress);


                        try {
                            url = document.getData().get("pictureURL").toString();
                            Picasso.with(getApplicationContext()).load(url).into(ivProfile);
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("testPicture","No Picture");
                        }

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

    public void totalIncome(String userID) {

        db.collection("appointment")
                .whereEqualTo("providerID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        double totalPrice = 0.00;
                        String servicePrice = null;

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getUserInfoFromAppointmentDB->", document.getId() + " => " + document.getData());


                                try {
                                    servicePrice = document.getData().get("totalPrice").toString();
                                    totalPrice += Double.parseDouble(servicePrice);

                                    Log.e("totalIncome->", String.valueOf(totalPrice));
                                }catch (Exception e){
                                    Log.e("NullServicePrice->", "servicePrice = null");
                                }


                            }

                            Log.e("totalIncome->", String.valueOf(totalPrice));
                            btn_providerTotalIncome.setText("Total Income: "+"RM " + String.format("%.2f",totalPrice));


                        } else {
                            Log.e("updateFieldOnOtherCollection->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



    public void signOut() {
        // [START auth_sign_out]
        FirebaseAuth.getInstance().signOut();
        // [END auth_sign_out]
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}