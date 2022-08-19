package mobile.test.homerepair.testDemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Appointment;

public class TestLineChartWithDB extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TestLineChartDataObject dataObject = new TestLineChartDataObject();

    LineChart lineChartDB;
    LineDataSet lineDataSet = new LineDataSet(null,null);
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineData lineData;


    EditText et_xValue,et_yValue;
    Button btn_click;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_line_chart_with_db);

        lineChartDB = findViewById(R.id.lineChartDB);

        et_xValue = findViewById(R.id.et_xValue);
        et_yValue = findViewById(R.id.et_yValue);

        btn_click = findViewById(R.id.btn_click);

        lineDataSet.setLineWidth(4);

        getDataFromDB();

        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDataToDBUsingHashMap();
//                insertDataToDBUsingPOJO();

                Intent intent = new Intent(getApplicationContext(), TestLineChartWithDB.class);
                startActivity(intent);

            }
        });



        /////////
    }

    private void insertDataToDBUsingPOJO() {

        // Create random invoice ID
        Random rand = new Random();
        int randomID = rand.nextInt(9999)+1;

        String lineChartID;
        lineChartID = "lineChartID" + randomID;

        int x = Integer.parseInt(et_xValue.getText().toString());
        int y = Integer.parseInt(et_yValue.getText().toString());

        dataObject.lineChartID = lineChartID;
        dataObject.xValue = x;
        dataObject.yValue = y;

//        dataObject = new TestLineChartDataObject(x,y);

        db.collection("testLineChart").document(lineChartID)
                .set(dataObject)
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

    }

    private void insertDataToDBUsingHashMap() {

        // Create random invoice ID
        Random rand = new Random();
        int randomID = rand.nextInt(9999)+1;

        String lineChartID;
        lineChartID = "lineChartID" + randomID;

        int x = Integer.parseInt(et_xValue.getText().toString());
        int y = Integer.parseInt(et_yValue.getText().toString());

        // Put all data to hash map
        Map<String, Object> data = new HashMap<>();
        data.put("lineChartID", lineChartID);
        data.put("xValue", x);
        data.put("yValue", y);

        db.collection("testLineChart").document(lineChartID)
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
        
        getDataFromDB();
    }

    private void getDataFromDB() {

        db.collection("testLineChart")
//                .whereEqualTo("invoiceID","invoice3535")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<Entry> dataValue = new ArrayList<Entry>();
                        dataValue.add(new Entry(0,0));

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getDataFromDB->", document.getId() + " => " + document.getData());

//                                TestLineChartDataObject dataPoint = document.toObject(TestLineChartDataObject.class);
//                                dataValue.add(new Entry(dataPoint.getxValue(),dataPoint.getyValue()));

                                try {
                                    int x_coordinate = Integer.parseInt(document.getData().get("xValue").toString());
                                    int y_coordinate = Integer.parseInt(document.getData().get("xValue").toString());

                                    Log.e("x->", String.valueOf(x_coordinate));
                                    Log.e("y->", String.valueOf(y_coordinate));

                                    dataValue.add(new Entry(x_coordinate,y_coordinate));

                                    Collections.sort(dataValue, new EntryXComparator());

                                }catch (Exception e){
                                    e.printStackTrace();
                                }


                            }

                            Log.e("getDataValuesXY->", String.valueOf(dataValue));

                            showChart(dataValue);


                        } else {
                            lineChartDB.clear();
                            lineChartDB.invalidate();

                            Log.e("getDataFormDb2->", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    private void showChart(ArrayList<Entry> dataValue) {

        lineDataSet.setValues(dataValue);
        lineDataSet.setLabel("Data Set1");

        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);

        lineChartDB.clear();
        lineChartDB.setData(lineData);
        lineChartDB.invalidate();

    }


    /////////
}