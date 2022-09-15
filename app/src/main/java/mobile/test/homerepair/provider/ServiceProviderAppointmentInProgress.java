package mobile.test.homerepair.provider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.location.Address;
import android.location.Geocoder;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

import mobile.test.homerepair.MailAPI.JavaMailAPI;
import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Order;

public class ServiceProviderAppointmentInProgress extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , ServiceProviderAppointmentInProgressRVAdapter.ItemClickListener {

    private RecyclerView rvServiceItem;
    private ArrayList<Order> orderArrayList;
    private ServiceProviderAppointmentInProgressRVAdapter serviceProviderAppointmentInProgressRVAdapter;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;

    TextView et_detailClientName, et_detailClientEmail,
            et_detailClientPhone, et_detailClientAddress, tv_detailClientDate, tv_detailClientTime,tv_totalPrice;

    ImageView img_pictureClient,img_receiptPicture;

    MaterialIconView btn_addItem;
    EditText et_addServiceOffer,et_addServicePrice;

    Button btn_completeAppointment;

    String TAG = "TAG";
    String clientPictureURL;

    String appointmentID;
    String currentUserID;
    String clientID;

    String orderID;
    String serviceName,servicePrice;

    String urlReceiptPicture;
    String url;
    ProgressDialog progressDialog;

    String getReceiptURLFromDB;

    double totalPrice;

    String clientAddress1,clientAddress2,clientPostcode,clientCity,clientState;
    String clientFullAddress;

    ProgressBar loadingPB;

    GoogleMap mGoogleMap;
    String getFullAddressForMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress_appointment_service_provider);


        try {
            Intent intent = getIntent();
            appointmentID = intent.getStringExtra("appointmentID");
            Log.e("testGetAppointmentID", appointmentID);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("testGetAppointmentID", "No-appointmentID");
        }



        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);


        img_pictureClient = findViewById(R.id.img_pictureClient);

        et_detailClientName =  findViewById(R.id.et_detailClientName);
        et_detailClientEmail =  findViewById(R.id.et_detailClientEmail);
        et_detailClientPhone =  findViewById(R.id.et_detailClientPhone);
        et_detailClientAddress =  findViewById(R.id.et_detailClientAddress);

        tv_detailClientDate =  findViewById(R.id.tv_detailClientDate);
        tv_detailClientTime =  findViewById(R.id.tv_detailClientTime);

        //////
        et_addServiceOffer = findViewById(R.id.et_addServiceOffer);
        et_addServicePrice = findViewById(R.id.et_addServicePrice);
        btn_addItem = findViewById(R.id.btn_addItem);

        tv_totalPrice = findViewById(R.id.tv_totalPrice);

        img_receiptPicture = findViewById(R.id.img_receiptPicture);

        btn_completeAppointment = findViewById(R.id.btn_completeAppointment);

        loadingPB = findViewById(R.id.idProgressBar);
        rvServiceItem = findViewById(R.id.rvServiceItem);

        orderArrayList = new ArrayList<>();
        rvServiceItem.setHasFixedSize(true);
        rvServiceItem.setLayoutManager(new LinearLayoutManager(this));

        serviceProviderAppointmentInProgressRVAdapter = new ServiceProviderAppointmentInProgressRVAdapter(orderArrayList,this);
        serviceProviderAppointmentInProgressRVAdapter.setClickListener(this);

        rvServiceItem.setAdapter(serviceProviderAppointmentInProgressRVAdapter);

        // Get Client ID from DB
        getClientIDFromDB();

        // Get Client Address from DB
        getClientAddressFromDB();

        // Get Receipt URL for Condition
        getReceiptURLForCondition();

        // Display Client Information
        displayClientInfoFromDB();

        // Display Service Provider Offer & Price
//        displayServiceOfferFromTableServiceOffer();

        displayServiceOfferFromTableOrder();

        // Display Receipt Picture
        try {
            displayReceiptPicture();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("No Picture", "No Picture");
        }


        initMap();

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

//                    updateAppointmentStatusToComplete();

//                    addDateCompleteAppointmentToDB();

//                    addTotalPriceToAppointmentDB();

//                addReceiptPictureUrlToDB(); // add picture URL on table appointment


                    getAppointmentDetailFromDB_notifyClientThroughEmail(appointmentID);

                    Toast.makeText(getApplicationContext(), "Successfully Updated, We have notify the client, please wait for client to complete the appointment", Toast.LENGTH_SHORT).show();


//                    Intent intent = new Intent(getApplicationContext(), AppointmentScheduleListServiceProviderTabLayout.class);
//                    intent.putExtra("appointmentID",appointmentID);
//                    Log.e("testPassAppointmentID",appointmentID);
//                    startActivity(intent);
                }
                ///// ----> Validation

            }
        });



    }



    @Override
    public void onItemClick(View view, int position){
        String test = serviceProviderAppointmentInProgressRVAdapter.getItem(position).getOrderID();
//        Toast.makeText(getApplicationContext(), "Test"+test, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // <-- Notification Through Email
    private void getAppointmentDetailFromDB_notifyClientThroughEmail(String appointmentID) {

        db.collection("appointment")
                .whereEqualTo("appointmentID", appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        String clientEmailFromDB = null,
                                providerName = null,
                                providerPhone = null,
                                providerEmail = null,
                                providerFullAddress = null,
                                providerAppointmentDate = null,
                                providerAppointmentTime = null;

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getDataFormDb->", document.getId() + " => " + document.getData());

                                clientEmailFromDB = document.getData().get("clientEmail").toString();
                                providerName = document.getData().get("companyName").toString();
                                providerPhone = document.getData().get("companyPhone").toString();
                                providerEmail = document.getData().get("companyEmail").toString();
                                providerAppointmentDate = document.getData().get("date").toString();
                                providerAppointmentTime = document.getData().get("time").toString();


                                // Get Full Address
                                String fullAddress;

                                fullAddress = document.getData().get("companyAddress1").toString() + ", ";
                                fullAddress += document.getData().get("companyAddress2").toString() + ", \n";
                                fullAddress += document.getData().get("companyPostcode").toString() + " ";
                                fullAddress += document.getData().get("companyState").toString() + ", \n";
                                fullAddress += document.getData().get("companyCity").toString() ;

                                providerFullAddress = fullAddress;

                            }

                            sendEmailNotificationToProvider(clientEmailFromDB, providerName,
                                    providerPhone, providerEmail, providerFullAddress, providerAppointmentDate, providerAppointmentTime);


                        } else {
                            Log.e("getDataFormDb2->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void sendEmailNotificationToProvider(String receiverEmail, String name,
                                                              String phone, String email,
                                                              String fullAddress, String date, String time) {

        String emailReceiver = receiverEmail;
        String subjectNotify = "Home Repair Apps: Appointment Complete By Service Provider";
        String messageNotify = "Your appointment has been completed by" +
                "\n\nService Provider: " + name +
                "\nPhone: " + phone +
                "\nEmail: " + email +
                "\nLocation: " + fullAddress +
                "\n\nAppointment Date: " + date + " " + time+
                "\n\nKindly login to the application to see the detail about the appointment.";


        String mail = emailReceiver.trim();
        String subject = subjectNotify.trim();
        String message = messageNotify;

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mail, subject, message);
        javaMailAPI.execute();
    }

    // --> Notification Through Email




    ///// Map Function
    private void geoLocate() {
        String locationName = getFullAddressForMap;

        Log.e("geoLocate->",locationName);


//        try {
//            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//            List<Address> addressList = geocoder.getFromLocationName(locationName,5);
//
//            if(addressList.size()>0){
//                Address address = addressList.get(0);
//                Log.e("geoLocate->addressList->", String.valueOf(address));
//
//                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())));
//                gotoLocation(address.getLatitude(),address.getLongitude());
//
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Intent intent = new Intent(getApplicationContext(),InProgressAppointmentServiceProvider.class);
//            intent.putExtra("appointmentID",appointmentID);
//            overridePendingTransition(0,0);
//            startActivity(intent);
//        }



        // Run on Thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //
                try {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addressList = geocoder.getFromLocationName(locationName, 5);

                    if (addressList.size() > 0) {
                        Address address = addressList.get(0);

                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())));
                        gotoLocation(address.getLatitude(), address.getLongitude());
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    Intent intent = new Intent(getApplicationContext(), ServiceProviderAppointmentInProgress.class);
                    intent.putExtra("appointmentID",appointmentID);
                    overridePendingTransition(0,0);
                    startActivity(intent);
                }
            }
        });


    }

    private void gotoLocation(double latitude, double longitude) {

        LatLng LatLng = new LatLng(latitude, longitude);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng, 18);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void initMap() {
        SupportMapFragment supportMapFragment =  (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        supportMapFragment.getMapAsync(this);
    }
    ///// Map Function



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


    public void getClientIDFromDB(){
        db.collection("appointment").whereEqualTo("appointmentID", appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                try {
                                    clientID = document.getData().get("clientID").toString();

                                    Log.e("clientID",clientID);


                                }catch (Exception e){
                                    e.printStackTrace();
                                    Log.e("LogDisplayUserInformation","No Data In Database");
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }


    public void getClientAddressFromDB(){
        db.collection("appointment").whereEqualTo("appointmentID", appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                try {
                                    clientAddress1 = document.getData().get("clientAddress1").toString() + ", ";
                                    clientAddress2 = document.getData().get("clientAddress2").toString() + ",\n";
                                    clientPostcode = document.getData().get("clientPostcode").toString() + " ";
                                    clientCity = document.getData().get("clientCity").toString() + ",\n";
                                    clientState = document.getData().get("clientState").toString();

                                    Log.e("clientAddress1",clientAddress1);
                                    Log.e("clientAddress2",clientAddress2);
                                    Log.e("clientPostcode",clientPostcode);
                                    Log.e("clientCity",clientCity);
                                    Log.e("clientState",clientState);

                                    clientFullAddress = clientAddress1 + clientAddress2 + clientPostcode + clientCity + clientState;

                                    Log.e("clientFullAddress",clientFullAddress);

                                }catch (Exception e){
                                    e.printStackTrace();
                                    Log.e("LogDisplayUserInformation","No Data In Database");
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }


    public void displayClientInfoFromDB(){
        db.collection("appointment").whereEqualTo("appointmentID",appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                clientPictureURL = document.getData().get("clientPictureURL").toString();
                                Picasso.with(getApplicationContext()).load(clientPictureURL).into(img_pictureClient);

                                et_detailClientName.setText(document.getData().get("clientName").toString());
                                et_detailClientEmail.setText(document.getData().get("clientEmail").toString());
                                et_detailClientPhone.setText(document.getData().get("clientPhone").toString());
                                et_detailClientAddress.setText(clientFullAddress);
                                tv_detailClientDate.setText(document.getData().get("date").toString());
                                tv_detailClientTime.setText(document.getData().get("time").toString());

                                getFullAddressForMap = clientFullAddress;
                                Log.e("displayClientInfoFromDB->",getFullAddressForMap);
                                geoLocate();
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
        serviceOffer.put("providerID",currentUserID);
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
                        Intent intent = new Intent(getApplicationContext(), ServiceProviderAppointmentInProgress.class);
                        intent.putExtra("appointmentID",appointmentID);
                        startActivity(intent);

                        ServiceProviderAppointmentInProgress.this.finish();

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


    public void displayServiceOfferFromTableOrder(){
        Log.e("displayServiceOfferFromTableOrder->providerID",currentUserID);
        Log.e("displayServiceOfferFromTableOrder->appointmentID",appointmentID);
//        Log.e("displayServiceOfferFromTableOrder->clientID",clientID);

        // Display on recycleview and get data from DB collection order by User ID
        db.collection("order")
                .whereEqualTo("providerID",currentUserID)
//                .whereEqualTo("clientID",clientID)
                .whereEqualTo("appointmentID",appointmentID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

//                        double totalPrice;
                        double servicePrice = 0.00;

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : list) {

                                Order order = documentSnapshot.toObject(Order.class);
                                orderArrayList.add(order);

                                servicePrice = servicePrice + Double.parseDouble(order.getServicePrice());
                            }

                            serviceProviderAppointmentInProgressRVAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            loadingPB.setVisibility(View.GONE);
                        }

                        totalPrice = servicePrice;

//                        String.valueOf(totalPrice);
//                        tv_totalPrice.setText(String.valueOf(totalPrice));

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

    public void addDateCompleteAppointmentToDB(){

        // format Date
        Date currentDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

        String formatCurrentDate = simpleDateFormat.format(currentDate);
        Log.e("formatCurrentDate->",formatCurrentDate);


        Map<String, Object> data = new HashMap<>();

        data.put("dateCompleteAppointment", formatCurrentDate);

        db.collection("appointment")
                .document(appointmentID)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("addDateCompleteAppointmentToDB->", "DocumentSnapshot successfully written!");
//                        Toast.makeText(getApplicationContext(), "Thank you for rating", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(),"Unsuccessfully Register", Toast.LENGTH_SHORT).show();
                        Log.e("insertRatingToDB->", "Error writing document", e);
                    }
                });
    }


    public void addTotalPriceToAppointmentDB(){

        String getTotalPriceToString = String.format("%.2f",totalPrice);

        Map<String, Object> dataTotalPrice = new HashMap<>();

        dataTotalPrice.put("totalPrice", getTotalPriceToString);

        db.collection("appointment").document(appointmentID)
                .set(dataTotalPrice,SetOptions.merge())
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
                    storageReference.putFile(getImageUri(ServiceProviderAppointmentInProgress.this,bitmap))
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
                            Intent intent = new Intent(getApplicationContext(), ServiceProviderAppointmentInProgress.class);
                            intent.putExtra("appointmentID",appointmentID);
                            overridePendingTransition(0,0);
                            startActivity(intent);

                            ServiceProviderAppointmentInProgress.this.finish();
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




    ///// Map Function/Method
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }
    ///// Map Function/Method


    public void backButton(View view) {
        Intent intent = new Intent(getApplicationContext(), AppointmentScheduleListServiceProviderTabLayout.class);
        intent.putExtra("appointmentID",appointmentID);
        startActivity(intent);
    }


    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}