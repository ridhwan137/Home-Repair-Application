package mobile.test.homerepair.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mobile.test.homerepair.MailAPI.JavaMailAPI;
import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Order;
import mobile.test.homerepair.provider.CompleteAppointmentScheduleServiceProvider;

public class InProgressAppointmentClient extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener ,CompleteAppointmentScheduleClientRVAdapter.ItemClickListener   {


    private RecyclerView rvServiceItem;
    private ArrayList<Order> orderArrayList;
    private CompleteAppointmentScheduleClientRVAdapter completeAppointmentScheduleClientRVAdapter;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;

    EditText et_detailCompanyName, et_detailCompanyEmail, et_detailCompanyServiceType,
            et_detailCompanyPhone, et_detailCompanyAddress;

    TextView tv_detailCompanyDate, tv_detailCompanyTime,tv_totalPrice;

    ImageView img_pictureCompany,img_receiptPicture;

    MaterialIconView btn_addItem;
    EditText et_addServiceOffer,et_addServicePrice;

    Button btn_completeAppointment,btn_downloadReceipt,btn_generateInvoice;

    String TAG = "TAG";
    String providerPictureURL;

    String appointmentID;
    String currentUserID;
    String providerID;

    double totalPrice;

    String urlReceiptPicture;
    String url;
    ProgressDialog progressDialog;

    ProgressBar loadingPB;

    String companyAddress1,companyAddress2,companyPostcode,companyCity,companyState;
    String companyFullAddress;

    GoogleMap mGoogleMap;
    String getFullAddressForMap;

    String receiptDownloadPictureURI;

    String getReceiptURLFromDB;



    // For Appointment Invoice
    String invoiceTitle = "Home Repair Apps";
    String invoiceAppointmentID = "";
    String invoiceServiceProviderName = "";
    String invoiceClientName = "";
    String invoiceDateComplete = "";
    String invoicePaymentMethod = "Payment Method: Cash";
    String invoiceGreeting = "Thank You";
    String invoiceTitleServiceCharges = "Service Charges:";
    String invoiceTitleServiceOffer = "Service Offer";
    String invoiceTitleServicePrice = "Price(RM)";
    String invoiceServiceOffer = "";
    String invoiceServicePrice = "";


    PdfDocument myPdfDocument = new PdfDocument();
    Paint forLinePaint = new Paint();
    PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(250,350,1).create();

    PdfDocument.Page myPage=myPdfDocument.startPage(myPageInfo);
    Canvas canvas = myPage.getCanvas();
    Paint paint = new Paint();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress_appointment_client);

        initMap();

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


        img_pictureCompany = findViewById(R.id.img_pictureCompany);

        et_detailCompanyName =  findViewById(R.id.et_detailCompanyName);
        et_detailCompanyServiceType = findViewById(R.id.et_detailCompanyServiceType);
        et_detailCompanyEmail =  findViewById(R.id.et_detailCompanyEmail);
        et_detailCompanyPhone =  findViewById(R.id.et_detailCompanyPhone);
        et_detailCompanyAddress =  findViewById(R.id.et_detailCompanyAddress);

        tv_detailCompanyDate =  findViewById(R.id.tv_detailCompanyDate);
        tv_detailCompanyTime =  findViewById(R.id.tv_detailCompanyTime);

        et_addServiceOffer = findViewById(R.id.et_addServiceOffer);
        et_addServicePrice = findViewById(R.id.et_addServicePrice);
        btn_addItem = findViewById(R.id.btn_addItem);

        tv_totalPrice = findViewById(R.id.tv_totalPrice);

        img_receiptPicture = findViewById(R.id.img_receiptPicture);

        btn_completeAppointment = findViewById(R.id.btn_completeAppointment);

        btn_downloadReceipt = findViewById(R.id.btn_downloadReceipt);


        loadingPB = findViewById(R.id.idProgressBar);
        rvServiceItem = findViewById(R.id.rvServiceItem);




        orderArrayList = new ArrayList<>();
        rvServiceItem.setHasFixedSize(true);
        rvServiceItem.setLayoutManager(new LinearLayoutManager(this));

        completeAppointmentScheduleClientRVAdapter = new CompleteAppointmentScheduleClientRVAdapter(orderArrayList,this);
        completeAppointmentScheduleClientRVAdapter.setClickListener(this);

        rvServiceItem.setAdapter(completeAppointmentScheduleClientRVAdapter);

        // Get Provider ID from DB
        getProviderIDFromDB();

        // Get Provider Address from DB
        getProviderAddressFromDB();

        // Display Provider Information
        displayProviderInfoFromDB();

        // Display Service Provider Offer & Price
//        displayServiceOfferFromTableServiceOffer();
        displayServiceOfferFromTableOrder();

        // Get Receipt URL for Condition
        getReceiptURLForCondition();

        // Display Receipt Picture
        try {
            displayReceiptPicture();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("No Picture", "No Picture");
        }



        // Download Receipt Picture
        btn_downloadReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///// <--- Validation
                if (getReceiptURLFromDB == null){
                    Toast.makeText(getApplicationContext(), "Please contact service provider to upload receipt by clicking receipt picture", Toast.LENGTH_SHORT).show();
                }else {
                    downloadReceipt();
                    Toast.makeText(getApplicationContext(), "Download Receipt", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Generate Invoice
        btn_generateInvoice = findViewById(R.id.btn_generateInvoice);
        btn_generateInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                checkExternalStoragePermission();
//                getAppointmentDataFormDbForInvoice();

                boolean pick = true;
                if(pick == true){

                    if(!checkCameraPermission()){
                        requestCameraPermissions();
                    }
                    else{
                        ///// <--- Validation

                        if (totalPrice == 0.00){
                            Toast.makeText(getApplicationContext(), "Please contact service provider to input service provided", Toast.LENGTH_SHORT).show();
                        }else {
                            getAppointmentDataFormDbForInvoice();
                        }


                    }

                }
                else {

                    if (!checkStoragePermission()) {
                        requestStoragePermissions();
                    } else {
                        ///// <--- Validation

                        if (totalPrice == 0.00){
                            Toast.makeText(getApplicationContext(), "Please contact service provider to input service provided", Toast.LENGTH_SHORT).show();
                        }else {
                            getAppointmentDataFormDbForInvoice();
                        }
                    }
                }
            }
        });


        btn_completeAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ///// <--- Validation

                if (totalPrice == 0.00){
                    Toast.makeText(getApplicationContext(), "Please contact service provider to input service provided", Toast.LENGTH_SHORT).show();
                }else if (getReceiptURLFromDB == null){
                    Toast.makeText(getApplicationContext(), "Please contact service provider to upload receipt by clicking receipt picture", Toast.LENGTH_SHORT).show();
                }else {
                    completeAppointment();
                }



            }
        });

        // End Bracket onCreate


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


    // <-- Notification Through Email
    private void getAppointmentDetailFromDB_notifyProviderThroughEmail(String appointmentID) {

        db.collection("appointment")
                .whereEqualTo("appointmentID", appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        String providerEmail = null,
                                clientName = null,
                                clientPhone = null,
                                clientEmail = null,
                                clientFullAddress = null,
                                clientAppointmentDate = null,
                                clientAppointmentTime = null;

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getDataFormDb->", document.getId() + " => " + document.getData());

                                providerEmail = document.getData().get("companyEmail").toString();
                                clientName = document.getData().get("clientName").toString();
                                clientPhone = document.getData().get("clientPhone").toString();
                                clientEmail = document.getData().get("clientEmail").toString();
                                clientAppointmentDate = document.getData().get("date").toString();
                                clientAppointmentTime = document.getData().get("time").toString();


                                // Get Full Address
                                String fullAddress;

                                fullAddress = document.getData().get("clientAddress1").toString() + ", ";
                                fullAddress += document.getData().get("clientAddress2").toString() + ", \n";
                                fullAddress += document.getData().get("clientPostcode").toString() + " ";
                                fullAddress += document.getData().get("clientState").toString() + ", \n";
                                fullAddress += document.getData().get("clientCity").toString() ;

                                clientFullAddress = fullAddress;

                            }

                            sendEmailNotificationToProvider(providerEmail, clientName,
                                    clientPhone, clientEmail, clientFullAddress, clientAppointmentDate, clientAppointmentTime);


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
        String subjectNotify = "Home Repair Apps: Appointment Complete By Client";
        String messageNotify = "Your appointment has been completed by" +
                "\n\nClient Name: " + name +
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




    private void completeAppointment() {

        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateAppointmentStatus", "complete");

        DocumentReference docRef = db.collection("appointment").document(appointmentID);
        docRef.update("appointmentStatus", appointment.get("updateAppointmentStatus"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                        addTotalPriceToAppointmentDB();

                        addDateCompleteAppointmentToDB();

                        getAppointmentDetailFromDB_notifyProviderThroughEmail(appointmentID);

                        Intent intent = new Intent(getApplicationContext(), AppointmentScheduleClientTabLayout.class);
                        intent.putExtra("testPassAppointmentID",appointmentID);
                        startActivity(intent);
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



    @Override
    public void onItemClick(View view, int position){
        String test = completeAppointmentScheduleClientRVAdapter.getItem(position).getOrderID();
        Toast.makeText(getApplicationContext(), "Test"+test, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }



    ///////////////////////////
    // Function Create PDF
    ///////////////////////////

    public void getAppointmentDataFormDbForInvoice(){

        db.collection("appointment")
                .whereEqualTo("appointmentID",appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        String invoiceAppointmentID = null,
                                invoiceClientName = null,
                                invoiceServiceProviderName = null,
                                invoiceDateComplete = null;

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getAppointmentDataFormDbForInvoice->", document.getId() + " => " + document.getData());

                                try {
                                    invoiceAppointmentID = appointmentID;
                                    invoiceClientName = document.getData().get("clientName").toString();
                                    invoiceServiceProviderName = document.getData().get("companyName").toString();

                                    invoiceDateComplete = document.getData().get("dateCompleteAppointment").toString();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }

                            createPDFGetDataFromDbForInvoice(invoiceAppointmentID,invoiceServiceProviderName,
                                    invoiceClientName,invoiceDateComplete);

                        } else {
                            Log.e("getAppointmentDataFormDbForInvoice->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public void createPDFGetDataFromDbForInvoice(String appointmentID,String providerName, String clientName, String date) {

        myPdfDocument = new PdfDocument();
        forLinePaint = new Paint();
        myPageInfo = new PdfDocument.PageInfo.Builder(250, 350, 1).create();

        myPage = myPdfDocument.startPage(myPageInfo);
        canvas = myPage.getCanvas();
        paint = new Paint();

        forLinePaint.setStyle(Paint.Style.STROKE);
        forLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
        forLinePaint.setStrokeWidth(2);

        paint.setTextSize(15.5f);
        paint.setColor(Color.rgb(0, 50, 250));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(invoiceTitle, canvas.getWidth() / 2, 20, paint);

        canvas.drawLine(20, 40, 230, 40, forLinePaint);

        paint.setTextSize(8.5f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Appointment ID: " +appointmentID, 20, 55, paint);

        canvas.drawText("Service Provider: " + providerName, 20, 67, paint);

        canvas.drawText("Client: " + clientName, 20, 80, paint);

        canvas.drawLine(20, 90, 230, 90, forLinePaint);

        canvas.drawText(invoiceTitleServiceCharges, 20, 105, paint);

        canvas.drawText(invoiceTitleServiceOffer, 20, 135, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(invoiceTitleServicePrice, 225, 135, paint);

        paint.setTextAlign(Paint.Align.LEFT);

        db.collection("order")
                .whereEqualTo("appointmentID", appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int yPlus = 140;

                            double servicePrice;
                            double totalAmount = 0.00;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("createPDFGetDataFromDbForInvoice->", document.getId() + " => " + document.getData());


                                invoiceServiceOffer = document.getData().get("serviceName").toString();
                                invoiceServicePrice = document.getData().get("servicePrice").toString();

                                Log.e("invoiceServiceOffer->", invoiceServiceOffer);
                                Log.e("invoiceServicePrice->", invoiceServicePrice);
                                Log.e("invoiceServicePrice->", invoiceServicePrice);

                                servicePrice = Double.parseDouble(invoiceServicePrice);

                                yPlus += 15;
                                canvas.drawText(invoiceServiceOffer, 20, yPlus, paint);

                                totalAmount += servicePrice;

                                paint.setTextAlign(Paint.Align.RIGHT);
                                canvas.drawText(String.format("%.2f",servicePrice), 225, yPlus, paint);

                                paint.setTextAlign(Paint.Align.LEFT);

                            }

                            Log.e("yPlusLast->", String.valueOf(yPlus));

                            yPlus += 20;

                            paint.setTextAlign(Paint.Align.LEFT);

                            canvas.drawLine(20, yPlus, 230, yPlus, forLinePaint);

                            paint.setTextSize(10f);

                            yPlus += 15;
                            canvas.drawText("Total(RM)", 120, yPlus, paint);

                            paint.setTextAlign(Paint.Align.RIGHT);
                            canvas.drawText(String.format("%.2f",totalAmount), 225, yPlus, paint);



                            yPlus += 35;
                            paint.setTextAlign(Paint.Align.LEFT);
                            paint.setTextSize(8.5f);
                            canvas.drawText("Date: " + date, 20, yPlus, paint);

                            yPlus += 15;
                            canvas.drawText(invoicePaymentMethod, 20, yPlus, paint);

                            yPlus += 30;
                            paint.setTextAlign(Paint.Align.CENTER);
                            paint.setTextSize(12f);
                            canvas.drawText(invoiceGreeting, canvas.getWidth() / 2, yPlus, paint);


                            myPdfDocument.finishPage(myPage);

                            String pdfDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                            File pdfFile = new File(pdfDirectory, "AppointmentInvoice" + ".pdf");

                            try {

                                myPdfDocument.writeTo(new FileOutputStream(pdfFile));

                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                myPdfDocument.close();

                                DownloadManager downloadManager = (DownloadManager) InProgressAppointmentClient.this.getSystemService(DOWNLOAD_SERVICE);
                                downloadManager.addCompletedDownload(pdfFile.getName(), pdfFile.getName(), true, "application/pdf", pdfFile.getAbsolutePath(), pdfFile.length(), true);

                                Toast.makeText(getApplicationContext(), "Saved to Downloads.", Toast.LENGTH_LONG).show();
                            }


                        } else {
                            Log.e("createPDFGetDataFromDbForInvoice->", "Error getting documents: ", task.getException());
                        }
                    }
                });

        ///////
    }

    ///////////////////////////
    ///////////////////////////




    ///// Map Function
    private void initMap() {
        SupportMapFragment supportMapFragment =  (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        supportMapFragment.getMapAsync(this);
    }

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
//
//            Intent intent = new Intent(getApplicationContext(), InProgressAppointmentClient.class);
//            intent.putExtra("appointmentID",appointmentID);
//
//            startActivity(intent);
//        }


        // Run on Thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //
                try {
                    Geocoder geocoder = new Geocoder(InProgressAppointmentClient.this, Locale.getDefault());
                    List<Address> addressList = geocoder.getFromLocationName(locationName, 5);

                    if (addressList.size() > 0) {
                        Address address = addressList.get(0);

                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())));
                        gotoLocation(address.getLatitude(), address.getLongitude());
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    Intent intent = new Intent(getApplicationContext(), InProgressAppointmentClient.class);
                    intent.putExtra("appointmentID",appointmentID);
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


    ///// Map Function

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


    public void getProviderIDFromDB(){
        db.collection("appointment").whereEqualTo("appointmentID", appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                try {
                                    providerID = document.getData().get("providerID").toString();

                                    Log.e("providerID",providerID);


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


    public void getProviderAddressFromDB(){
        db.collection("appointment").whereEqualTo("appointmentID", appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                try {
                                    companyAddress1 = document.getData().get("companyAddress1").toString() + ", ";
                                    companyAddress2 = document.getData().get("companyAddress2").toString() + ",\n";
                                    companyPostcode = document.getData().get("companyPostcode").toString() + " ";
                                    companyCity = document.getData().get("companyCity").toString() + ",\n";
                                    companyState = document.getData().get("companyState").toString();

                                    companyFullAddress = companyAddress1 + companyAddress2 + companyPostcode + companyCity + companyState;

                                    Log.e("companyFullAddress",companyFullAddress);

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


    public void displayProviderInfoFromDB(){
        db.collection("appointment").whereEqualTo("appointmentID",appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                et_detailCompanyName.setText(document.getData().get("companyName").toString());
                                et_detailCompanyServiceType.setText(document.getData().get("companyServiceType").toString());
                                et_detailCompanyEmail.setText(document.getData().get("companyEmail").toString());
                                et_detailCompanyPhone.setText(document.getData().get("companyPhone").toString());
                                et_detailCompanyAddress.setText(companyFullAddress);
                                tv_detailCompanyDate.setText(document.getData().get("date").toString());
                                tv_detailCompanyTime.setText(document.getData().get("time").toString());

                                getFullAddressForMap = companyFullAddress;
                                Log.e("displayClientInfoFromDB->",getFullAddressForMap);
                                geoLocate();


                                try {
                                    providerPictureURL = document.getData().get("providerPictureURL").toString();
                                    Picasso.with(getApplicationContext()).load(providerPictureURL).into(img_pictureCompany);
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




    public void displayServiceOfferFromTableOrder(){
        Log.e("displayServiceOfferFromTableOrder->clientID",currentUserID);
        Log.e("displayServiceOfferFromTableOrder->appointmentID",appointmentID);
//        Log.e("displayServiceOfferFromTableOrder->clientID",clientID);

        // Display on recycleview and get data from DB collection order by User ID
        db.collection("order")
                .whereEqualTo("clientID",currentUserID)
//                .whereEqualTo("clientID",clientID)
                .whereEqualTo("appointmentID",appointmentID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        double servicePrice = 0.00;

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : list) {

                                Order order = documentSnapshot.toObject(Order.class);
                                orderArrayList.add(order);

                                servicePrice = servicePrice + Double.parseDouble(order.getServicePrice());
                            }

                            completeAppointmentScheduleClientRVAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            loadingPB.setVisibility(View.GONE);
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


    /// Download Receipt Function
    private void downloadReceipt() {

        storageReference = FirebaseStorage.getInstance().getReference();

        DocumentReference docRef = db.collection("appointment").document(appointmentID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Log.e("downloadReceipt->", "DocumentSnapshot data: " + document.getData());

                        try {
                            receiptDownloadPictureURI = document.getData().get("receiptPictureURL").toString();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        downloadFile(InProgressAppointmentClient.this,"AppointmentReceipt",".png", Environment.DIRECTORY_DOWNLOADS,receiptDownloadPictureURI);

                    } else {
                        Log.e("downloadReceipt->", "No such document");
                    }
                } else {
                    Log.e("downloadReceipt->", "get failed with ", task.getException());
                }
            }
        });


    }

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url){

        DownloadManager downloadManager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory,fileName+fileExtension);

        downloadManager.enqueue(request);

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
                    storageReference = FirebaseStorage.getInstance().getReference("images/"+ " ReceiptPic " +fileName);

                    //upload the photo uploaded from camera to storage
                    storageReference.putFile(getImageUri(getApplicationContext(),bitmap))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    img_receiptPicture.setImageURI(null);
                                    Toast.makeText(getApplicationContext(),"Successfully Uploaded" + storageReference.getDownloadUrl(),Toast.LENGTH_SHORT).show();

                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            Log.e("URL ", "onSuccess: " + uri);
                                            urlReceiptPicture = uri.toString();
//                                            updatePictureUrl();

//                                            addPictureUrlToDB();
                                        }
                                    });

                                    Log.e("URL ", "onSuccess: " + storageReference.getDownloadUrl());
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

    public void backButton(View view) {
        Intent intent = new Intent(getApplicationContext(), AppointmentScheduleClientTabLayout.class);
        intent.putExtra("testPassAppointmentID",appointmentID);
        startActivity(intent);
    }




    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
    // End Bracket

}