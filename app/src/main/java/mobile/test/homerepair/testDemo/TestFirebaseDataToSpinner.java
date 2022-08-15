package mobile.test.homerepair.testDemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

import mobile.test.homerepair.R;

public class TestFirebaseDataToSpinner extends AppCompatActivity {

    Button button;
    Spinner spinner;
    ArrayList<String> arrayServices;
    ArrayAdapter<String> adapterServices;
    QuerySnapshot services;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_firebase_data_to_spinner);

        spinner = findViewById(R.id.spinner);
        button = findViewById(R.id.button);

        String [] arrayServiceList = {"Select Your Service Type"};

        arrayServices = new ArrayList<>(Arrays.asList(arrayServiceList));
        adapterServices = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, arrayServices){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }

        };


        adapterServices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterServices);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), adapterServices.getItem(position), Toast.LENGTH_SHORT).show();

                try {
                    Log.e("services->",services.getDocuments().get(position).getId());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        getData();
        db.collection("service").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                services = queryDocumentSnapshots;
                if (queryDocumentSnapshots.size()>0){


                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                        arrayServices.add(documentSnapshot.getString("serviceType"));
                    }

                    adapterServices.notifyDataSetChanged();

                }else{
                    Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });
        /////


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // <---- Validation Spinner

                String getSelectedItem = spinner.getSelectedItem().toString();

                Log.d("getServicesSpinner", getSelectedItem);

                if(getSelectedItem.equals("Select Your Service Type")){
                    Toast.makeText(getApplicationContext(), "Please Select Your Service Type", Toast.LENGTH_SHORT).show();
                    return;
                }
                // ----> Validation Spinner



                Toast.makeText(getApplicationContext(), getSelectedItem, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(){
        db.collection("service").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                services = queryDocumentSnapshots;
                if (queryDocumentSnapshots.size()>0){


                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                        arrayServices.add(documentSnapshot.getString("serviceType"));
                    }

                    adapterServices.notifyDataSetChanged();

                }else{
                    Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //////
}