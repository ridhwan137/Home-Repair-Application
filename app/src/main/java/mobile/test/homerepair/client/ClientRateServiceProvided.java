package mobile.test.homerepair.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import mobile.test.homerepair.R;
import mobile.test.homerepair.testDemo.TestAdminAddServiceOffer;

public class ClientRateServiceProvided extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    String appointmentID,clientID,providerID;


    Button btn_back,btn_submitRate;
    RatingBar ratingBar;
    float rateValue;

    boolean hasRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_rate_service_provided);


        try {
            Intent intent = getIntent();
            appointmentID = intent.getStringExtra("appointmentID");
            providerID = intent.getStringExtra("providerID");
            Log.e("appointmentID->", appointmentID);
            Log.e("providerID->", providerID);
        }catch (Exception e){
            e.printStackTrace();
        }



        btn_back = findViewById(R.id.btn_back);

        ratingBar = findViewById(R.id.ratingBar);
        btn_submitRate = findViewById(R.id.btn_submitRate);



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


        Log.e("hasRate_MAIN->", String.valueOf(hasRate));

        displayServiceRated();


        ///////////////////


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CompleteAppointmentScheduleClient.class);
                intent.putExtra("appointmentID",appointmentID);
                startActivity(intent);
            }
        });

    }

    private void checkRatingHasRate( boolean hasRate) {

        if (hasRate){
            Log.e("hasRate_IF->", String.valueOf(hasRate));

            ratingBar.setIsIndicator(true);
            btn_submitRate.setVisibility(View.INVISIBLE);
        }else {
            Log.e("hasRate_ELSE->", String.valueOf(hasRate));
            btn_submitRate.setVisibility(View.VISIBLE);
            btn_submitRate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insertRatingToDB();
                }
            });
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

    private void insertRatingToDB() {

        Map<String, Object> dataServiceRate = new HashMap<>();

        dataServiceRate.put("serviceRate", rateValue);

        db.collection("appointment")
                .document(appointmentID)
                .set(dataServiceRate, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("insertRatingToDB->", "DocumentSnapshot successfully written!");
                        Toast.makeText(getApplicationContext(), "Thank you for rating", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(),"Unsuccessfully Register", Toast.LENGTH_SHORT).show();
                        Log.e("insertRatingToDB->", "Error writing document", e);
                    }
                });

        calculateAverageRatingFromDB();
    }


    private void calculateAverageRatingFromDB() {

        db.collection("appointment")
                .whereEqualTo("providerID",providerID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            int totalOfUserThatRate = 0;
                            float totalRating = 0.0F;
                            float averateRating = 0.0F;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("calculateAverageRatingFromDB->", document.getId() + " => " + document.getData());

                                float oneUserRating = Float.parseFloat(document.getData().get("serviceRate").toString());

                                Log.e("oneUserRating->", String.valueOf(oneUserRating));

                                totalRating += oneUserRating;
                                totalOfUserThatRate++;


                            }

                            Log.e("totalRating->", String.valueOf(totalRating));
                            Log.e("totalOfUserThatRate->", String.valueOf(totalOfUserThatRate));

                            averateRating = totalRating/totalOfUserThatRate;
                            Log.e("TotalAverageRating->", String.valueOf(averateRating));

//                            DecimalFormat decimalFormat = new DecimalFormat("#.#");
//                            String formatAverageRating = decimalFormat.format(averateRating);
//                            Log.e("formatAverageRating2->", formatAverageRating);

//                            ratingBar.setRating(Float.parseFloat(formatAverageRating));

                            insertAverageRatingToProviderDB(averateRating);


                        } else {
                            Log.e("calculateAverageRatingFromDB->", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void insertAverageRatingToProviderDB(float averageRating) {

        Map<String, Object> dataAverageServiceRate = new HashMap<>();

        dataAverageServiceRate.put("userServiceRating", averageRating);

        db.collection("users")
                .document(providerID)
                .set(dataAverageServiceRate, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("insertAverageRatingToProviderDB->", "DocumentSnapshot successfully written!");

                        Intent intent = new Intent(getApplicationContext(), CompleteAppointmentScheduleClient.class);
                        intent.putExtra("appointmentID",appointmentID);
                        startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(),"Unsuccessfully Register", Toast.LENGTH_SHORT).show();
                        Log.e("insertAverageRatingToProviderDB->", "Error writing document", e);
                    }
                });
    }


    ////////////
}