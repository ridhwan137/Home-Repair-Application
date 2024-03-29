package mobile.test.homerepair.provider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
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
import android.graphics.drawable.LayerDrawable;
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
import android.widget.RatingBar;
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
import java.util.List;
import java.util.Locale;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Order;

public class ServiceProviderAppointmentComplete extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , ServiceProviderAppointmentCompleteRVAdapter.ItemClickListener {

    private RecyclerView rvServiceItem;
    private ArrayList<Order> orderArrayList;
    private ServiceProviderAppointmentCompleteRVAdapter serviceProviderAppointmentCompleteRVAdapter;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;

    TextView et_detailClientName, et_detailClientEmail,
            et_detailClientPhone, et_detailClientAddress, tv_detailClientDate, tv_detailClientTime,tv_totalPrice;

    ImageView img_pictureClient,img_receiptPicture;

    MaterialIconView btn_addItem;
    EditText et_addServiceOffer,et_addServicePrice;

    Button btn_completeAppointment,btn_downloadReceipt,btn_generateInvoice;

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

    ProgressBar loadingPB;

    String clientAddress1,clientAddress2,clientPostcode,clientCity,clientState;
    String clientFullAddress;

    GoogleMap mGoogleMap;
    String getFullAddressForMap;

    String receiptDownloadPictureURI;

    // Rating Bar Properties
    RatingBar ratingBar;
    float rateValue;
    boolean hasRate;


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

        setContentView(R.layout.activity_complete_appointment_schedule_service_provider);

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

        et_addServiceOffer = findViewById(R.id.et_addServiceOffer);
        et_addServicePrice = findViewById(R.id.et_addServicePrice);
//        btn_addItem = findViewById(R.id.btn_addItem);

        tv_totalPrice = findViewById(R.id.tv_totalPrice);

        img_receiptPicture = findViewById(R.id.img_receiptPicture);

//        btn_completeAppointment = findViewById(R.id.btn_completeAppointment);

        btn_downloadReceipt = findViewById(R.id.btn_downloadReceipt);


        loadingPB = findViewById(R.id.idProgressBar);
        rvServiceItem = findViewById(R.id.rvServiceItem);


        orderArrayList = new ArrayList<>();
        rvServiceItem.setHasFixedSize(true);
        rvServiceItem.setLayoutManager(new LinearLayoutManager(this));

        serviceProviderAppointmentCompleteRVAdapter = new ServiceProviderAppointmentCompleteRVAdapter(orderArrayList,this);
        serviceProviderAppointmentCompleteRVAdapter.setClickListener(this);

        rvServiceItem.setAdapter(serviceProviderAppointmentCompleteRVAdapter);

        // Get Client ID from DB
        getClientIDFromDB();

        // Get Client Address from DB
        getClientAddressFromDB();

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

        // Download Receipt Picture
        btn_downloadReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadReceipt();
                Toast.makeText(getApplicationContext(), "Download Receipt", Toast.LENGTH_SHORT).show();
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
                    else
                        getAppointmentDataFormDbForInvoice();

                }
                else {

                    if (!checkStoragePermission()) {
                        requestStoragePermissions();
                    } else
                        getAppointmentDataFormDbForInvoice();
                }

            }
        });



        // Rating Bar
        ratingBar = findViewById(R.id.ratingBar);

        //////////////////////////////////////
        // Configuration Rating Bar Colour
        /////////////////////////////////////

        LayerDrawable getRatingBarDrawable = (LayerDrawable) ratingBar.getProgressDrawable();


        // Partial star
        DrawableCompat.setTint(DrawableCompat.wrap(getRatingBarDrawable.getDrawable(1)),
                ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateValue = ratingBar.getRating();
                Log.e("rateValue->", String.valueOf(rateValue));

            }
        });

        displayServiceRated();
        // Rating Bar



        // End Bracket onCreate
    }

    @Override
    public void onItemClick(View view, int position){
//        String test = serviceProviderAppointmentCompleteRVAdapter.getItem(position).getOrderID();
//        Toast.makeText(getApplicationContext(), "Test"+test, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private void checkRatingHasRate( boolean hasRate) {

        if (hasRate){
            Log.e("hasRate_IF->", String.valueOf(hasRate));
            ratingBar.setIsIndicator(true);
        }else {
            Log.e("hasRate_ELSE->", String.valueOf(hasRate));
            ratingBar.setIsIndicator(true);
        }

    }

    private void displayServiceRated() {

        db.collection("appointment")
                .whereEqualTo("appointmentID",appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            float oneUserRating = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("calculateAverageRatingFromDB->", document.getId() + " => " + document.getData());


                                try {
                                    oneUserRating = Float.parseFloat(document.getData().get("serviceRate").toString());
                                    Log.e("oneUserRating->", String.valueOf(oneUserRating));

                                    hasRate = true;
                                    Log.e("hasRate_TRY->", String.valueOf(hasRate));
                                }catch (Exception e){
                                    e.printStackTrace();
                                    hasRate = false;
                                    Log.e("hasRate_CATCH->", String.valueOf(hasRate));
                                }

                            }

                            Log.e("hasRate_FOR->", String.valueOf(hasRate));

                            checkRatingHasRate(hasRate);

                            ratingBar.setRating(oneUserRating);

                        } else {
                            Log.e("calculateAverageRatingFromDB->", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }



    private void checkExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
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
                        if (task.isSuccessful()) {

                            String invoiceAppointmentID = null,
                                    invoiceClientName = null,
                                    invoiceServiceProviderName = null,
                                    invoiceDateComplete = null;

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

                                DownloadManager downloadManager = (DownloadManager) ServiceProviderAppointmentComplete.this.getSystemService(DOWNLOAD_SERVICE);
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
//            Intent intent = new Intent(getApplicationContext(), ServiceProviderAppointmentComplete.class);
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
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addressList = geocoder.getFromLocationName(locationName, 5);

                    if (addressList.size() > 0) {
                        Address address = addressList.get(0);

                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())));
                        gotoLocation(address.getLatitude(), address.getLongitude());
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    Intent intent = new Intent(getApplicationContext(), ServiceProviderAppointmentComplete.class);
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
                                    clientID = document.getData().get("clientID").toString() + ", ";

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

                        double totalPrice;
                        double servicePrice = 0.00;

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : list) {

                                Order order = documentSnapshot.toObject(Order.class);
                                orderArrayList.add(order);

                                servicePrice = servicePrice + Double.parseDouble(order.getServicePrice());
                            }

                            serviceProviderAppointmentCompleteRVAdapter.notifyDataSetChanged();
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

                        downloadFile(ServiceProviderAppointmentComplete.this,"AppointmentReceipt",".png", Environment.DIRECTORY_DOWNLOADS,receiptDownloadPictureURI);

                    } else {
                        Log.e("downloadReceipt->", "No such document");
                    }
                } else {
                    Log.e("downloadReceipt->", "get failed with ", task.getException());
                }
            }
        });


    }

    public void downloadFile(Context context,String fileName,String fileExtension, String destinationDirectory, String url){

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
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
        Intent intent = new Intent(getApplicationContext(), HistoryAppointmentListServiceProviderTabLayout.class);
        intent.putExtra("appointmentID",appointmentID);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    // End Bracket
}