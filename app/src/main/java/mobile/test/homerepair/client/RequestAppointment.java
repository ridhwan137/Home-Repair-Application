package mobile.test.homerepair.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
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
import mobile.test.homerepair.client.unnecessary.HomeClient;
import mobile.test.homerepair.model.Services;

public class RequestAppointment extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,RequestAppointmentRVAdapter.ItemClickListener {

    private RecyclerView rvServiceDetail;
    private ArrayList<Services> servicesArrayList;
    private RequestAppointmentRVAdapter requestAppointmentRVAdapter;

    GoogleMap mGoogleMap;

    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String providerID,providerPictureURL;
    String chooseDate,chooseTime,message;
    String currentUserID;
    String appointmentID;
    String TAG = "TAG";

    String clientPictureURL,clientName,clientEmail,clientPhone,clientAddress;

    String providerAddress1,providerAddress2,providerPostcode,providerState,providerCity;
    String clientAddress1,clientAddress2,clientPostcode,clientState,clientCity;

    EditText date_in;
    EditText time_in;
    EditText et_message;

    EditText et_detailCompanyName, et_detailCompanyServiceType, et_detailCompanyEmail,
            et_detailCompanyPhone, et_detailCompanyAddress;

    ImageView img_pictureCompany;
    Button btn_BackToProviderDetail,btn_detailRequestAppointment;

    String getFullAddressForMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_appointment);

        Intent intent = getIntent();
        providerID = intent.getStringExtra("userID");
        Log.e("testGetProviderUserID", providerID);


        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);



        loadingPB = findViewById(R.id.idProgressBar);
        rvServiceDetail = findViewById(R.id.rvServiceDetail);

        servicesArrayList = new ArrayList<>();
        rvServiceDetail.setHasFixedSize(true);
        rvServiceDetail.setLayoutManager(new LinearLayoutManager(this));

        requestAppointmentRVAdapter = new RequestAppointmentRVAdapter(servicesArrayList,this);
        requestAppointmentRVAdapter.setClickListener(this);

        rvServiceDetail.setAdapter(requestAppointmentRVAdapter);


        btn_BackToProviderDetail = findViewById(R.id.btn_BackToProviderDetail);
        btn_detailRequestAppointment = findViewById(R.id.btn_detailRequestAppointment);

        img_pictureCompany = findViewById(R.id.img_pictureCompany);

        et_detailCompanyName =  findViewById(R.id.et_detailCompanyName);
        et_detailCompanyServiceType =  findViewById(R.id.et_detailCompanyServiceType);
        et_detailCompanyEmail =  findViewById(R.id.et_detailCompanyEmail);
        et_detailCompanyPhone =  findViewById(R.id.et_detailCompanyPhone);
        et_detailCompanyAddress =  findViewById(R.id.et_detailCompanyAddress);


        date_in=findViewById(R.id.date_input);
        time_in=findViewById(R.id.time_input);

        et_message = findViewById(R.id.et_message);

        date_in.setInputType(InputType.TYPE_NULL);
        time_in.setInputType(InputType.TYPE_NULL);

        displayProviderInfoFromDB();
        getClientInfoFromDB();
        getClientAddressFromDB();
        getProviderAddressFromDB();
//        displayServiceOffer();
        initMap();

        date_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                showDateDialog(date_in);


                // Calendar for Date Picker
                Calendar calendar = Calendar.getInstance();

                int year = calendar.get(Calendar.YEAR);
                int dayOfMonth = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(RequestAppointment.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        int getDay = dayOfMonth;
                        int getMonth = month+1;
                        int getYear = year;

                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");

//                        date_in.setText(simpleDateFormat.format(calendar.getTime()));

//                        String dateSelected = getDay + "/" + getMonth + "/" + getYear;

//                        date_in.setText(dateSelected);

                        date_in.setText(simpleDateFormat.format(calendar.getTime()));

                    }
                },year,dayOfMonth,day);

                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();

            }
        });

        time_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(time_in);
            }
        });


        btn_detailRequestAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                requestAppointment();
//                requestAppointmentUsingAppointmentStatusFromDB();

                requestAppointmentMakeItSortByDate();

//                addAppointmentIDToUserDB();

//                Toast.makeText(getApplicationContext(), "Your request has been sent to the service provider, you are eligible to cancel the appointment if the service provider does not respond within 2 to 3 days", Toast.LENGTH_LONG).show();



            }
        });



        btn_BackToProviderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchServices.class);
                Log.e("testPassUserID", providerID);
                intent.putExtra("userID", providerID);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onItemClick(View view, int position){
        String test = requestAppointmentRVAdapter.getItem(position).getServiceID();
        Toast.makeText(getApplicationContext(), "Test"+test, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }



    ///// Map Function
    private void geoLocate() {
        String locationName = getFullAddressForMap;

        Log.e("geoLocate->",locationName);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addressList =geocoder.getFromLocationName(locationName,5);

            if(addressList.size()>0){
                Address address = addressList.get(0);

                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())));
                gotoLocation(address.getLatitude(),address.getLongitude());


            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(getApplicationContext(),RequestAppointment.class);
            intent.putExtra("userID",providerID);
            startActivity(intent);
        }
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


    public void displayProviderInfoFromDB(){
        db.collection("users").whereEqualTo("userID", providerID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                try {
                                    et_detailCompanyName.setText(document.getData().get("companyName").toString());
                                    et_detailCompanyServiceType.setText(document.getData().get("serviceType").toString());
                                    et_detailCompanyPhone.setText(document.getData().get("phone").toString());
                                    et_detailCompanyEmail.setText(document.getData().get("email").toString());

                                    String getServiceType = document.getData().get("serviceType").toString();
                                    displayServiceOffer(getServiceType);

                                    // Get Full Address
                                    String fullAddress;

                                    fullAddress = document.getData().get("address1").toString() + ", ";
                                    fullAddress += document.getData().get("address2").toString() + ",\n";
                                    fullAddress += document.getData().get("postcode").toString() + " ";
                                    fullAddress += document.getData().get("city").toString() + ",\n";
                                    fullAddress += document.getData().get("state").toString() ;

                                    et_detailCompanyAddress.setText(fullAddress);

                                    getFullAddressForMap = fullAddress;
                                    Log.e("displayClientInfoFromDB->",getFullAddressForMap);

                                    geoLocate();


                                    providerPictureURL = document.getData().get("pictureURL").toString();
                                    Picasso.with(getApplicationContext()).load(providerPictureURL).into(img_pictureCompany);


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


    private void showTimeDialog(final EditText time_in) {
        final Calendar calendar=Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);

                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");

                time_in.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        new TimePickerDialog(RequestAppointment.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
    }




/*    private void showDateDialog(final EditText date_in) {
        final Calendar calendar=Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");

                date_in.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };

//        new DatePickerDialog(RequestAppointment.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
        DatePickerDialog datePickerDialog = new DatePickerDialog(RequestAppointment.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();
    }
    */


    public void getClientInfoFromDB(){
        db.collection("users").whereEqualTo("userID", currentUserID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                try{
                                    clientPictureURL = document.getData().get("pictureURL").toString();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }


                                clientName = document.getData().get("name").toString();
                                clientPhone = document.getData().get("phone").toString();
                                clientEmail = document.getData().get("email").toString();

                                // Get Full Address
                                String fullAddress;

                                fullAddress = document.getData().get("address1").toString() + ", ";
                                fullAddress += document.getData().get("address2").toString() + ", \n";
                                fullAddress += document.getData().get("postcode").toString() + " ";
                                fullAddress += document.getData().get("city").toString() + ", \n";
                                fullAddress += document.getData().get("state").toString() ;

                                clientAddress = fullAddress;

                                Log.e(TAG, "Successful getting documents: ", task.getException());

                                Log.e("clientName",clientName);
                                Log.e("clientEmail",clientEmail);
                                Log.e("clientPhone",clientPhone);
                                Log.e("clientAddress",clientAddress);
//                                Log.e("clientPicture",clientPictureURL);


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }



    public void getClientAddressFromDB(){
        db.collection("users").whereEqualTo("userID", currentUserID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                clientAddress1 = document.getData().get("address1").toString();
                                clientAddress2 = document.getData().get("address2").toString();
                                clientPostcode = document.getData().get("postcode").toString();
                                clientCity = document.getData().get("city").toString();
                                clientState = document.getData().get("state").toString();

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }




    public void getProviderAddressFromDB(){
        db.collection("users").whereEqualTo("userID", providerID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                try {
                                    providerAddress1 = document.getData().get("address1").toString();
                                    providerAddress2 = document.getData().get("address2").toString();
                                    providerPostcode = document.getData().get("postcode").toString();
                                    providerCity = document.getData().get("city").toString();
                                    providerState = document.getData().get("state").toString() ;

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

    public void displayServiceOffer(String serviceType){

        Log.e("getServiceType->",serviceType);

        db.collection("serviceOffer")
//                .whereEqualTo("userID",providerID)
                .whereEqualTo("serviceType",serviceType)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : list) {

                                Services services = documentSnapshot.toObject(Services.class);

                                servicesArrayList.add(services);
                            }

                            requestAppointmentRVAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            loadingPB.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void requestAppointment(){

        String companyName,companyServiceType,companyEmail,companyPhone,companyAddress;

        message = et_message.getText().toString();
        chooseDate = date_in.getText().toString();
        chooseTime = time_in.getText().toString();

        companyName = et_detailCompanyName.getText().toString();
        companyServiceType = et_detailCompanyServiceType.getText().toString();
        companyEmail = et_detailCompanyEmail.getText().toString();
        companyPhone = et_detailCompanyPhone.getText().toString();
//        companyAddress = tv_detailCompanyAddress.getText().toString();


        //create random id
        Random rand = new Random();
        int randomID = rand.nextInt(99999999)+1;

        appointmentID = "appointment" + randomID;

        Map<String, Object> requestAppointment = new HashMap<>();

        requestAppointment.put("appointmentID",appointmentID);

        requestAppointment.put("providerID", providerID);
        requestAppointment.put("providerPictureURL",providerPictureURL);
        requestAppointment.put("companyName",companyName);
        requestAppointment.put("companyServiceType",companyServiceType);
        requestAppointment.put("companyEmail",companyEmail);
        requestAppointment.put("companyPhone",companyPhone);
        requestAppointment.put("companyAddress1",providerAddress1);
        requestAppointment.put("companyAddress2",providerAddress2);
        requestAppointment.put("companyPostcode",providerPostcode);
        requestAppointment.put("companyState",providerState);
        requestAppointment.put("companyCity",providerCity);
//        requestAppointment.put("companyAddress",companyAddress);


        requestAppointment.put("clientID", currentUserID);
        requestAppointment.put("clientPictureURL",clientPictureURL);
        requestAppointment.put("clientName", clientName);
        requestAppointment.put("clientEmail", clientEmail);
        requestAppointment.put("clientPhone", clientPhone);
        requestAppointment.put("clientAddress1",clientAddress1);
        requestAppointment.put("clientAddress2",clientAddress2);
        requestAppointment.put("clientPostcode",clientPostcode);
        requestAppointment.put("clientState",clientState);
        requestAppointment.put("clientCity",clientCity);
//        requestAppointment.put("clientAddress", clientAddress);


        requestAppointment.put("date",chooseDate);
        requestAppointment.put("time",chooseTime);
        requestAppointment.put("message",message);
        requestAppointment.put("appointmentStatus","pending");
        requestAppointment.put("requestStatus","pending");
//        requestAppointment.put("serviceID",serviceID);

        Log.e("testMapData", String.valueOf(requestAppointment));


        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testIdentifyCurrentUserID",userID);

        db.collection("appointment").document(appointmentID)
                .set(requestAppointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(getApplicationContext(), SearchServices.class);

                        Log.e("testPassProviderUserID",providerID);
                        intent.putExtra("userID",providerID);

                        startActivity(intent);

                        RequestAppointment.this.finish();

                        Toast.makeText(getApplicationContext(), "Services added successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }


    public void addAppointmentIDToUserDB(){

        Map<String, Object> data = new HashMap<>();

        data.put("appointmentID",appointmentID);

        Log.e("testMapData->", String.valueOf(data));

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testIdentifyCurrentUserID->",userID);

        db.collection("users").document(currentUserID)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("addAppointmentIDToUserDB->", "Successfully writing document");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("addAppointmentIDToUserDB->", "Error writing document", e);
                    }
                });
    }


    public void requestAppointmentMakeItSortByDate(){

        String companyName,companyServiceType,companyEmail,companyPhone;

        message = et_message.getText().toString();
        chooseDate = date_in.getText().toString();
        chooseTime = time_in.getText().toString();

        companyName = et_detailCompanyName.getText().toString();
        companyServiceType = et_detailCompanyServiceType.getText().toString();
        companyEmail = et_detailCompanyEmail.getText().toString();
        companyPhone = et_detailCompanyPhone.getText().toString();

        /// <-- Validation Date Time Null
        if(chooseDate.isEmpty() && chooseTime.isEmpty()){
            Toast.makeText(getApplicationContext(), "Date and Time not select yet", Toast.LENGTH_SHORT).show();
        }else if(chooseDate.isEmpty()){
            Toast.makeText(getApplicationContext(), "Date not select yet", Toast.LENGTH_SHORT).show();
        }else if(chooseTime.isEmpty()){
            Toast.makeText(getApplicationContext(), "Time not select yet", Toast.LENGTH_SHORT).show();
        }else{
        /// --> Validation Date Time Null

//        //create random id
//        Random rand = new Random();
//        int randomID = rand.nextInt(99999999)+1;
//        appointmentID = "appointment" + randomID;


            //create appointment id by date
            Date currentDate = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmssSSS");

            String formatCurrentDate = simpleDateFormat.format(currentDate);
            Log.e("formatCurrentDate->",formatCurrentDate);

            Random rand = new Random();
            int randomID = rand.nextInt(9999)+1;

            appointmentID = "appointment" + formatCurrentDate + randomID;



            Map<String, Object> requestAppointment = new HashMap<>();

            requestAppointment.put("appointmentID",appointmentID);

            requestAppointment.put("providerID", providerID);
            requestAppointment.put("providerPictureURL",providerPictureURL);
            requestAppointment.put("companyName",companyName);
            requestAppointment.put("companyServiceType",companyServiceType);
            requestAppointment.put("companyEmail",companyEmail);
            requestAppointment.put("companyPhone",companyPhone);
            requestAppointment.put("companyAddress1",providerAddress1);
            requestAppointment.put("companyAddress2",providerAddress2);
            requestAppointment.put("companyPostcode",providerPostcode);
            requestAppointment.put("companyState",providerState);
            requestAppointment.put("companyCity",providerCity);


            requestAppointment.put("clientID", currentUserID);
            requestAppointment.put("clientPictureURL",clientPictureURL);
            requestAppointment.put("clientName", clientName);
            requestAppointment.put("clientEmail", clientEmail);
            requestAppointment.put("clientPhone", clientPhone);
            requestAppointment.put("clientAddress1",clientAddress1);
            requestAppointment.put("clientAddress2",clientAddress2);
            requestAppointment.put("clientPostcode",clientPostcode);
            requestAppointment.put("clientState",clientState);
            requestAppointment.put("clientCity",clientCity);


            requestAppointment.put("date",chooseDate);


            Date date = new Date();
            Timestamp ts = new Timestamp(date);
            requestAppointment.put("dateRequest",ts);


            requestAppointment.put("time",chooseTime);
            requestAppointment.put("message",message);
            requestAppointment.put("appointmentStatus","pending");

            Log.e("testMapData->", String.valueOf(requestAppointment));


            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.e("testIdentifyCurrentUserID->",userID);

            db.collection("appointment").document(appointmentID)
                    .set(requestAppointment)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("requestAppointmentMakeItSortByDate->", "Successfully writing document");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("requestAppointmentMakeItSortByDate->", "Error writing document", e);
                        }
                    });


            AlertDialog.Builder dialog = new AlertDialog.Builder(RequestAppointment.this);
            dialog.setCancelable(false);
            dialog.setTitle("Successfully Request Appointment");
            dialog.setMessage("Your request has been sent to the service provider, you are eligible to cancel the appointment if the service provider does not respond within 2 to 3 days" );
            dialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    Intent intent = new Intent(getApplicationContext(), SearchServices.class);

                    Log.e("testPassProviderUserID",providerID);
                    intent.putExtra("userID",providerID);

                    startActivity(intent);
                    RequestAppointment.this.finish();
                }
            });

            final AlertDialog alert = dialog.create();
            alert.show();


        }


    }






    public void requestAppointmentUsingAppointmentStatusFromDB(){

        String companyName,companyServiceType,companyEmail,companyPhone,companyAddress;

        message = et_message.getText().toString();
        chooseDate = date_in.getText().toString();
        chooseTime = time_in.getText().toString();

        companyName = et_detailCompanyName.getText().toString();
        companyServiceType = et_detailCompanyServiceType.getText().toString();
        companyEmail = et_detailCompanyEmail.getText().toString();
        companyPhone = et_detailCompanyPhone.getText().toString();
//        companyAddress = tv_detailCompanyAddress.getText().toString();


        //create random id
        Random rand = new Random();
        int randomID = rand.nextInt(99999999)+1;

        appointmentID = "appointment" + randomID;

        Map<String, Object> requestAppointment = new HashMap<>();

        requestAppointment.put("appointmentID",appointmentID);

        requestAppointment.put("providerID", providerID);
        requestAppointment.put("providerPictureURL",providerPictureURL);
        requestAppointment.put("companyName",companyName);
        requestAppointment.put("companyServiceType",companyServiceType);
        requestAppointment.put("companyEmail",companyEmail);
        requestAppointment.put("companyPhone",companyPhone);
        requestAppointment.put("companyAddress1",providerAddress1);
        requestAppointment.put("companyAddress2",providerAddress2);
        requestAppointment.put("companyPostcode",providerPostcode);
        requestAppointment.put("companyState",providerState);
        requestAppointment.put("companyCity",providerCity);
//        requestAppointment.put("companyAddress",companyAddress);


        requestAppointment.put("clientID", currentUserID);
        requestAppointment.put("clientPictureURL",clientPictureURL);
        requestAppointment.put("clientName", clientName);
        requestAppointment.put("clientEmail", clientEmail);
        requestAppointment.put("clientPhone", clientPhone);
        requestAppointment.put("clientAddress1",clientAddress1);
        requestAppointment.put("clientAddress2",clientAddress2);
        requestAppointment.put("clientPostcode",clientPostcode);
        requestAppointment.put("clientState",clientState);
        requestAppointment.put("clientCity",clientCity);
//        requestAppointment.put("clientAddress", clientAddress);


        requestAppointment.put("date",chooseDate);
        requestAppointment.put("time",chooseTime);
        requestAppointment.put("message",message);
        requestAppointment.put("appointmentStatus","pending");
//        requestAppointment.put("requestStatus","pending");
//        requestAppointment.put("serviceID",serviceID);

        Log.e("testMapData", String.valueOf(requestAppointment));


        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testIdentifyCurrentUserID",userID);

        db.collection("appointment").document(appointmentID)
                .set(requestAppointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Intent intent = new Intent(getApplicationContext(), SearchServices.class);
//
//                        Log.e("testPassProviderUserID",providerID);
//                        intent.putExtra("userID",providerID);
//
//                        startActivity(intent);
//
//                        RequestAppointment.this.finish();

//                        Toast.makeText(getApplicationContext(), "Services added successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }



    public void requestAppointmentBugError(){

//        getClientInfoFromDB();

        String companyName,companyServiceType,companyEmail,companyPhone,companyAddress;

        message = et_message.getText().toString();
        chooseDate = date_in.getText().toString();
        chooseTime = time_in.getText().toString();

        companyName = et_detailCompanyName.getText().toString();
        companyServiceType = et_detailCompanyServiceType.getText().toString();
        companyEmail = et_detailCompanyEmail.getText().toString();
        companyPhone = et_detailCompanyPhone.getText().toString();
        companyAddress = et_detailCompanyAddress.getText().toString();


        //create random id
        Random rand = new Random();
        int randomID = rand.nextInt(99999999)+1;

        appointmentID = "appointment" + randomID;



        Map<String, Object> requestAppointment = new HashMap<>();

        requestAppointment.put("appointmentID",appointmentID);

        requestAppointment.put("providerID", providerID);
        requestAppointment.put("providerPictureURL",providerPictureURL);
        requestAppointment.put("companyName",companyName);
        requestAppointment.put("companyServiceType",companyServiceType);
        requestAppointment.put("companyEmail",companyEmail);
        requestAppointment.put("companyPhone",companyPhone);
        requestAppointment.put("companyAddress",companyAddress);


        requestAppointment.put("clientID", currentUserID);
        requestAppointment.put("clientPictureURL",clientPictureURL);
        requestAppointment.put("clientName", clientName);
        requestAppointment.put("clientEmail", clientEmail);
        requestAppointment.put("clientPhone", clientPhone);
        requestAppointment.put("clientAddress", clientAddress);


        requestAppointment.put("date",chooseDate);
        requestAppointment.put("time",chooseTime);
        requestAppointment.put("message",message);
        requestAppointment.put("appointmentStatus","pending");
        requestAppointment.put("requestStatus","pending");
//        requestAppointment.put("serviceID",serviceID);

        Log.e("testMapData", String.valueOf(requestAppointment));


        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testIdentifyCurrentUserID",userID);

        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("userID", currentUserID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        Log.e("testIdentifyDocumentMaxSize", String.valueOf(queryDocumentSnapshots.size()));
                        if (queryDocumentSnapshots.size() == 0 ) {
                            db.collection("appointment").document(appointmentID)
                                    .set(requestAppointment)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(getApplicationContext(), HomeClient.class);

                                            Log.e("testPassProviderUserID",providerID);
                                            intent.putExtra("userID",providerID);

                                            startActivity(intent);

                                            RequestAppointment.this.finish();

                                            Toast.makeText(getApplicationContext(), "Services added successfully.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Unsuccessfully Add Services", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
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

}