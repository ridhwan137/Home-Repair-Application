package mobile.test.homerepair.testDemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mobile.test.homerepair.R;

public class TestRating extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText et_oneRating;
    Button btn_submit;
    TextView tv_averageRate;

    RatingBar setRatingBar,getRatingBar;
    float rateValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_rating);

        et_oneRating = findViewById(R.id.et_oneRating);
        btn_submit = findViewById(R.id.btn_submit);
        tv_averageRate = findViewById(R.id.tv_averageRate);
        getRatingBar = findViewById(R.id.getRatingBar);
        setRatingBar = findViewById(R.id.setRatingBar);

        calculateAverageRatingFromDB();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertRatingToDB();
            }
        });

        setRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateValue = setRatingBar.getRating();
                Log.e("rateValue->", String.valueOf(rateValue));

            }
        });

        ////////
    }

    private void insertRatingToDB() {
        // Create random invoice ID
        Random rand = new Random();
        int randomID = rand.nextInt(9999) + 1;

        String ratingID;
        ratingID = "ratingID" + randomID;

//        double getRatingInput = Double.parseDouble(et_oneRating.getText().toString());

        // Put all data to hash map
        Map<String, Object> data = new HashMap<>();
        data.put("ratingID", ratingID);
        data.put("oneRating", rateValue);

        db.collection("testRating").document(ratingID)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getApplicationContext(), "Added successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error writing document->", "Error writing document");
                    }
                });

        calculateAverageRatingFromDB();
    }

    private void calculateAverageRatingFromDB() {

        db.collection("testRating")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            int totalOfUserThatRate = 0;
                            double totalRating = 0.00;
                            double averateRating = 0.00;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("calculateAverageRatingFromDB->", document.getId() + " => " + document.getData());

                                double oneUserRating = Double.parseDouble(document.getData().get("oneRating").toString());

                                Log.e("oneUserRating->", String.valueOf(oneUserRating));

                                totalRating += oneUserRating;
                                totalOfUserThatRate++;


                            }

                            Log.e("totalRating->", String.valueOf(totalRating));
                            Log.e("totalOfUserThatRate->", String.valueOf(totalOfUserThatRate));

                            averateRating = totalRating/totalOfUserThatRate;
                            Log.e("averateRating->", String.valueOf(averateRating));

                            getRatingBar.setRating((float) averateRating);


                            if (Double.isNaN(averateRating)){
                                tv_averageRate.setText("0.0");
                                Log.e("averateRating2->", String.valueOf(averateRating));
                            }else{
                                tv_averageRate.setText(String. format("%.1f", averateRating));
                                Log.e("averateRating3->", String.valueOf(averateRating));
                            }





                        } else {
                            Log.e("getDataFormDb2->", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    ////////
}