package mobile.test.homerepair.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.test.homerepair.R;
import mobile.test.homerepair.admin.unnecessary.AppointmentServiceProviderDetail;
import mobile.test.homerepair.model.Order;
import mobile.test.homerepair.model.Services;

public class CompleteAppointmentDetail extends AppCompatActivity implements ServiceOfferRVAdapter.ItemClickListener,CompleteServiceOrderRVAdapter.ItemClickListener{

    // 1st RecyclerView to show service offer
    private RecyclerView rvServiceDetail;
    private ArrayList<Services> servicesArrayList;
    private ServiceOfferRVAdapter serviceOfferRVAdapter;

    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;

    EditText et_appointmentID,et_clientName,et_companyName,et_serviceType,et_appointmentDate,et_appointmentTime,
            et_appointmentComplete,et_message;

    MaterialIconView btn_clientDetail,btn_companyDetail;

    String TAG = "TAG";
    String appointmentID;
    String providerID,clientID;


    // 2nd RecyclerView to show service charges
    private RecyclerView rvServiceItem;
    private ArrayList<Order> orderArrayList;
    private CompleteServiceOrderRVAdapter completeServiceOrderRVAdapter;

    ImageView img_receiptPicture;

    EditText et_addServiceOffer,et_addServicePrice;

    Button btn_downloadReceipt,btn_generateInvoice,btn_rejectCompleteAppointment;

    String url;

    ProgressBar loadingPB2;

    String receiptDownloadPictureURI;

    TextView tv_totalPrice;

    Button btn_back;


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
        setContentView(R.layout.activity_complete_appointment_detail);

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
        et_appointmentComplete = findViewById(R.id.et_appointmentComplete);
        et_message = findViewById(R.id.et_message);

        btn_clientDetail = findViewById(R.id.btn_clientDetail);
        btn_companyDetail = findViewById(R.id.btn_companyDetail);
        btn_rejectCompleteAppointment = findViewById(R.id.btn_rejectCompleteAppointment);

        getAppointmentInfoFromDB();

        loadingPB = findViewById(R.id.idProgressBar);
        rvServiceDetail = findViewById(R.id.rvServiceDetail);

        servicesArrayList = new ArrayList<>();
        rvServiceDetail.setHasFixedSize(true);
        rvServiceDetail.setLayoutManager(new LinearLayoutManager(this));

        serviceOfferRVAdapter = new ServiceOfferRVAdapter(servicesArrayList,this);
        serviceOfferRVAdapter.setClickListener(this);

        rvServiceDetail.setAdapter(serviceOfferRVAdapter);


        // <-- 2nd RecyclerView
        et_addServiceOffer = findViewById(R.id.et_addServiceOffer);
        et_addServicePrice = findViewById(R.id.et_addServicePrice);

        tv_totalPrice = findViewById(R.id.tv_totalPrice);

        img_receiptPicture = findViewById(R.id.img_receiptPicture);

        btn_downloadReceipt = findViewById(R.id.btn_downloadReceipt);

        loadingPB2 = findViewById(R.id.idProgressBar2);
        rvServiceItem = findViewById(R.id.rvServiceItem);

        orderArrayList = new ArrayList<>();
        rvServiceItem.setHasFixedSize(true);
        rvServiceItem.setLayoutManager(new LinearLayoutManager(this));

        completeServiceOrderRVAdapter = new CompleteServiceOrderRVAdapter(orderArrayList,this);
        completeServiceOrderRVAdapter.setClickListener(this);

        rvServiceItem.setAdapter(completeServiceOrderRVAdapter);
        // -->


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CompleteAppointmentList.class);
                startActivity(intent);
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


        btn_rejectCompleteAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectCompleteAppointment();
            }
        });


        // End Bracket
    }

    private void rejectCompleteAppointment() {

        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateAppointmentStatus", "reject");

        DocumentReference docRef = db.collection("appointment").document(appointmentID);
        docRef.update("appointmentStatus", appointment.get("updateAppointmentStatus"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                        Intent intent = new Intent(getApplicationContext(), AdminManageAppointment.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });

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

    @Override
    public void onItemClick(View view, int position){

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

                                DownloadManager downloadManager = (DownloadManager) CompleteAppointmentDetail.this.getSystemService(DOWNLOAD_SERVICE);
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
                                    et_appointmentComplete.setText(document.getData().get("dateCompleteAppointment").toString());
                                    et_message.setText(document.getData().get("message").toString());

                                    providerID = document.getData().get("providerID").toString();
                                    Log.e("1->providerID->",providerID);
//                                    displayServiceOffer(providerID);
                                    displayServiceOfferFromTableOrder(providerID);


                                    String serviceType = document.getData().get("companyServiceType").toString();
                                    Log.e("1->serviceType->",serviceType);
                                    displayServiceOffer(serviceType);

                                    clientID = document.getData().get("clientID").toString();
                                    Log.e("1->clientID->",clientID);


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


    public void displayServiceOffer(String serviceType){
        db.collection("serviceOffer")
                .whereEqualTo("serviceType",serviceType)
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


    public void displayServiceOfferFromTableOrder(String providerID){
        Log.e("displayServiceOfferFromTableOrder->providerID",providerID);
        Log.e("displayServiceOfferFromTableOrder->appointmentID",appointmentID);
//        Log.e("displayServiceOfferFromTableOrder->clientID",clientID);

        // Display on recycleview and get data from DB collection order by User ID
        db.collection("order")
                .whereEqualTo("providerID",providerID)
//                .whereEqualTo("clientID",clientID)
                .whereEqualTo("appointmentID",appointmentID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        double totalPrice;
                        double servicePrice = 0.00;

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB2.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : list) {

                                Order order = documentSnapshot.toObject(Order.class);
                                orderArrayList.add(order);

                                servicePrice = servicePrice + Double.parseDouble(order.getServicePrice());
                            }

                            completeServiceOrderRVAdapter.notifyDataSetChanged();
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

                        downloadFile(CompleteAppointmentDetail.this,"AppointmentReceipt",".png", Environment.DIRECTORY_DOWNLOADS,receiptDownloadPictureURI);

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

    public void displayReceiptPicture(){
        db.collection("appointment").whereEqualTo("appointmentID",appointmentID)
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

    // End Bracket
}