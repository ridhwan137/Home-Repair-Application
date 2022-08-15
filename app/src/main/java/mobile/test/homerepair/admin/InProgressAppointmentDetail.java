package mobile.test.homerepair.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Order;
import mobile.test.homerepair.model.Services;
import mobile.test.homerepair.provider.AppointmentScheduleServiceProviderTabLayout;
import mobile.test.homerepair.provider.InProgressAppointmentServiceProvider;
import mobile.test.homerepair.provider.InProgressAppointmentServiceProviderRVAdapter;

public class InProgressAppointmentDetail extends AppCompatActivity implements ServiceOfferRVAdapter.ItemClickListener,ServiceOrderRVAdapter.ItemClickListener {

    // 1st RecyclerView to show service offer
    private RecyclerView rvServiceDetail;
    private ArrayList<Services> servicesArrayList;
    private ServiceOfferRVAdapter serviceOfferRVAdapter;

    // 2nd RecyclerView to show service charges
    private RecyclerView rvServiceItem;
    private ArrayList<Order> orderArrayList;
    private ServiceOrderRVAdapter serviceOrderRVAdapter;

    ProgressBar loadingPB,loadingPB2;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;

    EditText et_appointmentID,et_clientName,et_companyName,et_serviceType,et_appointmentDate,et_appointmentTime;

    MaterialIconView btn_clientDetail,btn_companyDetail;

    String TAG = "TAG";
    String appointmentID;
    String providerID,clientID;

    // 2nd RecyclerView declaration

    EditText et_addServicePrice,et_addServiceOffer;
    Button btn_completeAppointment;
    MaterialIconView btn_addItem;
    TextView tv_totalPrice;
    ImageView img_receiptPicture;

    String orderID;
    String serviceName,servicePrice;

    String urlReceiptPicture;
    String url;
    ProgressDialog progressDialog;

    String getReceiptURLFromDB;

    Button btn_back;

    double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress_appointment_detail);

        Intent intent = getIntent();
        appointmentID = intent.getStringExtra("appointmentID");
        Log.e("appointmentID->",appointmentID);

        btn_back = findViewById(R.id.btn_back);

        et_appointmentID = findViewById(R.id.et_appointmentID);
        et_clientName = findViewById(R.id.et_clientName);
        et_companyName = findViewById(R.id.et_companyName);
        et_serviceType = findViewById(R.id.et_serviceType);
        et_appointmentDate = findViewById(R.id.et_appointmentDate);
        et_appointmentTime = findViewById(R.id.et_appointmentTime);

        btn_clientDetail = findViewById(R.id.btn_clientDetail);
        btn_companyDetail = findViewById(R.id.btn_companyDetail);

        getAppointmentInfoFromDB();

        loadingPB = findViewById(R.id.idProgressBar);
        rvServiceDetail = findViewById(R.id.rvServiceDetail);

        servicesArrayList = new ArrayList<>();
        rvServiceDetail.setHasFixedSize(true);
        rvServiceDetail.setLayoutManager(new LinearLayoutManager(this));

        serviceOfferRVAdapter = new ServiceOfferRVAdapter(servicesArrayList,this);
        serviceOfferRVAdapter.setClickListener(this);

        rvServiceDetail.setAdapter(serviceOfferRVAdapter);


        //<-- 2nd RecyclerView
        et_addServiceOffer = findViewById(R.id.et_addServiceOffer);
        et_addServicePrice = findViewById(R.id.et_addServicePrice);
        btn_addItem = findViewById(R.id.btn_addItem);

        tv_totalPrice = findViewById(R.id.tv_totalPrice);

        img_receiptPicture = findViewById(R.id.img_receiptPicture);

        btn_completeAppointment = findViewById(R.id.btn_completeAppointment);

        loadingPB2 = findViewById(R.id.idProgressBar2);
        rvServiceItem = findViewById(R.id.rvServiceItem);

        orderArrayList = new ArrayList<>();
        rvServiceItem.setHasFixedSize(true);
        rvServiceItem.setLayoutManager(new LinearLayoutManager(this));

        serviceOrderRVAdapter = new ServiceOrderRVAdapter(orderArrayList,this);
        serviceOrderRVAdapter.setClickListener(this);

        rvServiceItem.setAdapter(serviceOrderRVAdapter);
        //-->

        // Get Receipt URL for Condition
        getReceiptURLForCondition();

        // Display Service Provider Offer & Price
//        displayServiceOfferFromTableOrder();

        // Display Receipt Picture
        try {
            displayReceiptPicture();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("No Picture", "No Picture");
        }

        // Add Service Offered
        btn_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addServiceOffer(); // add service to new table Order
            }
        });


        // Upload Receipt Picture
        img_receiptPicture.setOnClickListener(new View.OnClickListener() {
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


        // Complete Appointment
        btn_completeAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ///// <--- Validation

                if (totalPrice == 0.00){
                    Toast.makeText(getApplicationContext(), "Please input service provided", Toast.LENGTH_SHORT).show();
                }else if (getReceiptURLFromDB == null){
                    Toast.makeText(getApplicationContext(), "Please upload receipt by clicking receipt picture", Toast.LENGTH_SHORT).show();
                }else{
                    // change appointment status in DB to complete
                    updateAppointmentStatusToComplete(); // update appointment status to complete on table appointment
                    addTotalPriceToAppointmentDB();


                    Intent intent = new Intent(getApplicationContext(), HomeAdmin.class);
                    intent.putExtra("appointmentID",appointmentID);
                    Log.e("testPassAppointmentID",appointmentID);
                    startActivity(intent);
                }
                ///// ----> Validation

            }
        });



        btn_clientDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("2->clientID->",clientID);

                Intent intent = new Intent(getApplicationContext(), AppointmentClientDetail.class);
                intent.putExtra("clientID",clientID);
                startActivity(intent);
            }
        });

        btn_companyDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("2->providerID->",providerID);

                Intent intent = new Intent(getApplicationContext(), AppointmentServiceProviderDetail.class);
                intent.putExtra("providerID",providerID);
                startActivity(intent);
            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InProgressAppointmentList.class);
                startActivity(intent);
            }
        });

        /////////
    }



    @Override
    public void onItemClick(View view, int position){

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void getProviderID(String getProviderID){
        providerID = getProviderID;
    }

    public void getAppointmentInfoFromDB(){
        db.collection("appointment").whereEqualTo("appointmentID",appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("QueryDocumentSnapshot->", document.getId() + " => " + document.getData());

                                try {

                                    et_appointmentID.setText(document.getData().get("appointmentID").toString());
                                    et_clientName.setText(document.getData().get("clientName").toString());
                                    et_companyName.setText(document.getData().get("companyName").toString());
                                    et_serviceType.setText(document.getData().get("companyServiceType").toString());
                                    et_appointmentDate.setText(document.getData().get("date").toString());
                                    et_appointmentTime.setText(document.getData().get("time").toString());

                                    providerID = document.getData().get("providerID").toString();
                                    Log.e("1->providerID->",providerID);
                                    displayServiceOffer(providerID);
//                                    getProviderID(providerID);
                                    displayServiceOfferFromTableOrder(providerID);

                                    clientID = document.getData().get("clientID").toString();
                                    Log.e("1->clientID->",clientID);
//                                    clientDetail(clientID);


                                }catch (Exception e){
                                    e.printStackTrace();
                                }



                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    /////////
    public void displayServiceOffer(String providerID){
        db.collection("serviceOffer")
                .whereEqualTo("userID",providerID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : list) {
                                Log.d("DocumentSnapshot->", documentSnapshot.getId() + " => " + documentSnapshot.getData());

                                Services services = documentSnapshot.toObject(Services.class);
                                servicesArrayList.add(services);
                            }

                            serviceOfferRVAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            loadingPB.setVisibility(View.GONE);
                            Log.e("displayServiceOffer->","empty");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getReceiptURLForCondition(){
        db.collection("appointment").whereEqualTo("appointmentID", appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                try {
                                    getReceiptURLFromDB = document.getData().get("receiptPictureURL").toString();

                                    Log.e("receiptURL->",getReceiptURLFromDB);


                                }catch (Exception e){
                                    e.printStackTrace();
                                    Log.e("getReceiptURLFromDB","No Data In Database");
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }


    public void addServiceOffer(){

        // <--- Validation Input

        serviceName = et_addServiceOffer.getText().toString();
        servicePrice = et_addServicePrice.getText().toString();


        if (serviceName.isEmpty() && servicePrice.isEmpty()){
            et_addServiceOffer.setError("Field is empty");
            et_addServicePrice.setError("Field is empty");
            return;
        }

        if (serviceName.isEmpty()){
            et_addServiceOffer.setError("Field is empty");
            return;
        }

        if (servicePrice.isEmpty()){
            et_addServicePrice.setError("Field is empty");
            return;
        }

//        if (!serviceName.matches("^[a-zA-Z\\s]+$")){
//            et_addServiceOffer.setError("Invalid character, input A~Z only");
//            return;
//        }


        if (!servicePrice.matches("^\\d+\\.\\d{2,2}$")){
            et_addServicePrice.setError("Invalid character, input 0.00~9.00 only");
            return;
        }
        // ----> Validation Input

        //create random id
        Random rand = new Random();
        int randomID = rand.nextInt(99999999)+1;

        orderID = "order" + randomID;

        Map<String, Object> serviceOffer = new HashMap<>();
        serviceOffer.put("providerID",providerID);
        serviceOffer.put("clientID",clientID);
        serviceOffer.put("orderID", orderID);
        serviceOffer.put("appointmentID",appointmentID);
        serviceOffer.put("serviceName",serviceName);
        serviceOffer.put("servicePrice",servicePrice);

        db.collection("order").document(orderID)
                .set(serviceOffer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(getApplicationContext(), InProgressAppointmentDetail.class);
                        intent.putExtra("appointmentID",appointmentID);
                        startActivity(intent);

                        InProgressAppointmentDetail.this.finish();

                        Toast.makeText(getApplicationContext(), "Services added successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error writing document", e);
                    }
                });
    }


    public void displayServiceOfferFromTableOrder(String providerID){
        Log.e("displayServiceOfferFromTableOrder->providerID",providerID);
        Log.e("displayServiceOfferFromTableOrder->appointmentID",appointmentID);

        // Display on recycleview and get data from DB collection order by User ID
        db.collection("order")
                .whereEqualTo("providerID",providerID)
//                .whereEqualTo("clientID",clientID)
                .whereEqualTo("appointmentID",appointmentID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        double servicePrice = 0.00;

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB2.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : list) {

                                Order order = documentSnapshot.toObject(Order.class);
                                orderArrayList.add(order);

                                servicePrice = servicePrice + Double.parseDouble(order.getServicePrice());
                            }

                            serviceOrderRVAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            loadingPB2.setVisibility(View.GONE);
                        }

                        totalPrice = servicePrice;

                        tv_totalPrice.setText(String.format("%.2f",totalPrice));

                        Log.e("totalPrice", String.valueOf(totalPrice));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void updateAppointmentStatusToComplete() {

        // Update collection Appointment, appointmentStatus to complete
        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateAppointmentStatus", "complete");

        DocumentReference docRef = db.collection("appointment").document(appointmentID);
        docRef.update("appointmentStatus", appointment.get("updateAppointmentStatus"))
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


    public void addTotalPriceToAppointmentDB(){

        String getTotalPriceToString = String.format("%.2f",totalPrice);

        Map<String, Object> dataTotalPrice = new HashMap<>();

        dataTotalPrice.put("totalPrice", getTotalPriceToString);

        db.collection("appointment").document(appointmentID)
                .set(dataTotalPrice, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("addTotalPriceToAppointmentDB->", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(),"Unsuccessfully Register", Toast.LENGTH_SHORT).show();
                        Log.e("addTotalPriceToAppointmentDB->", "Error writing document", e);
                    }
                });

    }



    // Upload Receipt Function
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
                    img_receiptPicture.setImageBitmap(bitmap);


                    ////////
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
                    Date now = new Date();
                    String fileName = formatter.format(now);
                    storageReference = FirebaseStorage.getInstance().getReference("images/"+ "receiptpic " +fileName);

                    //upload the photo uploaded from camera to storage
                    storageReference.putFile(getImageUri(InProgressAppointmentDetail.this,bitmap))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                    img_receiptPicture.setImageURI(null);
//                                    Toast.makeText(getApplicationContext(),"Successfully Uploaded" + storageReference.getDownloadUrl(),Toast.LENGTH_SHORT).show();

                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            Log.e("URL1 ", "onSuccess: " + uri);
                                            urlReceiptPicture = uri.toString();

//                                            updatePictureUrl();

                                            addReceiptPictureUrlToDB();

//                                            Intent intent = new Intent(getApplicationContext(), InProgressAppointmentServiceProvider.class);
//                                            intent.putExtra("appointmentID",appointmentID);
//                                            startActivity(intent);

                                            Log.e("statusUploadReceiptPictureURL->","Success");
                                        }
                                    });

//                                    Log.e("URL2 ", "onSuccess: " + storageReference.getDownloadUrl());
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
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public void displayReceiptPicture(){
        db.collection("appointment").whereEqualTo("appointmentID",appointmentID)
//        db.collection("order").whereEqualTo("appointmentID",appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                try {
                                    url = document.getData().get("receiptPictureURL").toString();
                                    Picasso.with(getApplicationContext()).load(url).into(img_receiptPicture);

                                }catch (Exception e){
                                    e.printStackTrace();
                                    Log.e("testPicture","No Picture");
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }


    public void addReceiptPictureUrlToDB(){

        // Make condition, if urlReceiptPicture not exist it will add new
        // if urlReceiptPicture exist it will merge

        if (getReceiptURLFromDB == null){
            try {
                Log.e("addPictureUrlToDB(null)->",getReceiptURLFromDB);
            }catch (Exception e){
                e.printStackTrace();
            }

            //////////////////////////////
            Map<String, Object> completeAppointment = new HashMap<>();

            completeAppointment.put("receiptPictureURL", urlReceiptPicture);

            Log.e("testMapData", String.valueOf(completeAppointment));

            db.collection("appointment").document(appointmentID)

                    .set(completeAppointment, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Successfully Added.", Toast.LENGTH_SHORT).show();
                            Log.e("addReceiptPictureUrlToDB->", "Added successfully");
                            Intent intent = new Intent(getApplicationContext(), InProgressAppointmentServiceProvider.class);
                            intent.putExtra("appointmentID",appointmentID);
                            startActivity(intent);

                            InProgressAppointmentDetail.this.finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("addReceiptPictureUrlToDB->", "Error writing document", e);
                        }
                    });
            //////////////////////////////

        }else{

            try {
                Log.e("addPictureUrlToDB(notNull)->",getReceiptURLFromDB);
            }catch (Exception e){
                e.printStackTrace();
            }

            DocumentReference nameRef = db.collection("appointment").document(appointmentID);
            nameRef
                    .update("receiptPictureURL", urlReceiptPicture)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("addReceiptPictureUrlToDB->", "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("addReceiptPictureUrlToDB->", "Error updating document", e);
                        }
                    });

        }


    }




    //////////
}