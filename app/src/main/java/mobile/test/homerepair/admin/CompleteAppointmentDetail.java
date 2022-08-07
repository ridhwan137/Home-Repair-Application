package mobile.test.homerepair.admin;

import androidx.annotation.NonNull;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mobile.test.homerepair.R;
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

    EditText et_appointmentID,et_clientName,et_companyName,et_serviceType,et_appointmentDate,et_appointmentTime;

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

    Button btn_downloadReceipt;

    String url;

    ProgressBar loadingPB2;

    String receiptDownloadPictureURI;

    TextView tv_totalPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_appointment_detail);

        Intent intent = getIntent();
        appointmentID = intent.getStringExtra("appointmentID");
        Log.e("appointmentID->",appointmentID);

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


        // End Bracket
    }

    @Override
    public void onItemClick(View view, int position){

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
                                    displayServiceOfferFromTableOrder(providerID);


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