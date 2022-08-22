package mobile.test.homerepair.testDemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
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
    LineDataSet lineDataSet = new LineDataSet(null, "Income");
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineData lineData;


    EditText et_xValue, et_yValue;
    Button btn_click;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_line_chart_with_db);

        lineChartDB = findViewById(R.id.lineChartDB);

        et_xValue = findViewById(R.id.et_xValue);
        et_yValue = findViewById(R.id.et_yValue);

        btn_click = findViewById(R.id.btn_click);


        getDataFromDB2();

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
        int randomID = rand.nextInt(9999) + 1;

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
        int randomID = rand.nextInt(9999) + 1;

        String lineChartID;
        lineChartID = "lineChartID" + randomID;

        String x = et_xValue.getText().toString();
        String y = et_yValue.getText().toString();

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

                        dataValue.add(new Entry(0, 0));

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getDataFromDB->", document.getId() + " => " + document.getData());

//                                TestLineChartDataObject dataPoint = document.toObject(TestLineChartDataObject.class);
//                                dataValue.add(new Entry(dataPoint.getxValue(),dataPoint.getyValue()));

                                try {
                                    float x_coordinate = Float.parseFloat(document.getData().get("xValue").toString());
                                    float y_coordinate = Float.parseFloat(document.getData().get("yValue").toString());

                                    Log.e("x->", String.valueOf(x_coordinate));
                                    Log.e("y->", String.valueOf(y_coordinate));

                                    dataValue.add(new Entry(x_coordinate, y_coordinate));

                                    Collections.sort(dataValue, new EntryXComparator());

                                } catch (Exception e) {
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


        XAxis xAxis = lineChartDB.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);


        lineDataSet.setValues(dataValue);
        lineDataSet.setLabel("Data Set1");
        lineDataSet.setLineWidth(4);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(15f);

        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);

        lineChartDB.clear();
        lineChartDB.setData(lineData);
        lineChartDB.invalidate();

    }


    private void getDataFromDB2() {

        db.collection("testLineChart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<Entry> dataValue = new ArrayList<Entry>();
                        List<Float> listValueY = new ArrayList<>();

//                        dataValue.add(new Entry(0,0));

                        int x_coordinate = 1;

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getDataFromDB->", document.getId() + " => " + document.getData());

                                try {

                                    float y_coordinate = Float.parseFloat(document.getData().get("yValue").toString());

                                    Log.e("y->", String.valueOf(y_coordinate));


                                    listValueY.add(y_coordinate);

                                    Log.e("listValueY->", String.valueOf(listValueY));


                                    dataValue.add(new Entry(x_coordinate, y_coordinate));

                                    Log.e("dataValue->", String.valueOf(dataValue));

//                                    Collections.sort(dataValue, new EntryXComparator());

                                    x_coordinate++;

                                    Log.e("x->", String.valueOf(x_coordinate));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }

                            Log.e("getDataValuesXY->", String.valueOf(dataValue));

//                            showChart2(dataValue);


                            LineDataSet dataSet = new LineDataSet(dataValue, "Income");
                            dataSet.setLineWidth(2);
                            dataSet.setColor(Color.RED);
                            dataSet.setValueTextColor(Color.BLACK);
                            dataSet.setValueTextSize(15f);
                            dataSet.setValues(dataValue);

                            LineData lineData = new LineData(dataSet);

                            XAxis xAxis = lineChartDB.getXAxis();
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setTextSize(10f);
                            xAxis.setTextColor(Color.RED);
                            xAxis.setDrawAxisLine(true);
                            xAxis.setDrawGridLines(false);
                            xAxis.setGranularity(1f);
                            xAxis.setGranularityEnabled(true);


                            List<String> list2 = new ArrayList<>();
                            list2.add("24/07/2022 05:02PM");
                            list2.add("28/07/2022 06:02PM");
                            list2.add("17/07/2022 01:02PM");
                            list2.add("19/07/2022 02:02PM");
                            list2.add("21/07/2022 03:02PM");
                            list2.add("22/07/2022 04:02PM");


                            Log.e("List2->", String.valueOf(list2));

                            // Sort in Ascending Order
//                            Collections.sort(list2);

                            // Sort in Descending Order
                            Collections.sort(list2, Collections.reverseOrder());

                            Log.e("sort->List2->", String.valueOf(list2));

                            lineChartDB.getAxisRight().setDrawLabels(false);

                            lineChartDB.getXAxis().setValueFormatter(new ValueFormatter() {
                                @Override
                                public String getAxisLabel(float value, AxisBase axis) {

                                    List<String> labelList = new ArrayList<>();
                                    String label = "";

                                    float counter;
                                    int listIndex = 0;

                                    for(counter = 0; counter <= list2.size();counter++){


                                        if (value == counter){

                                            Log.e("getAxisLabel->value->", String.valueOf(value));
                                            Log.e("getAxisLabel->counter->", String.valueOf(counter));
                                            Log.e("getAxisLabel->listIndex->", String.valueOf(listIndex));


                                            label = list2.get(listIndex);
                                            Log.e("getAxisLabel->label->", String.valueOf(label));

                                            value++;
                                            listIndex++;

                                        }


                                        labelList.add(label);

                                    }

                                    Log.e("labelList->", String.valueOf(labelList));

                                    return label;

                              /*      if (value == 1)
                                        label = list2.get(0);

                                    else if (value == 2)
                                        label = list2.get(1);

                                    else if (value == 3)
                                        label = list2.get(2);

                                    else if (value == 4)
                                        label = list2.get(3);

                                    else if (value == 5)
                                        label = list2.get(4);

                                    else if (value == 6)
                                        label = list2.get(5);
*/
//                                    return label;

                                }
                            });



                            lineChartDB.setXAxisRenderer(new CustomXAxisRenderer
                                    (
                                            lineChartDB.getViewPortHandler(),
                                            lineChartDB.getXAxis(),
                                            lineChartDB.getTransformer(YAxis.AxisDependency.LEFT)
                                    ));

                            lineChartDB.setExtraBottomOffset(20f);

                            lineChartDB.setData(lineData);
                            lineChartDB.getDescription().setText("Income Chart");


                        } else {
                            lineChartDB.clear();
                            lineChartDB.invalidate();

                            Log.e("getDataFormDb2->", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }


    private void showChart2(ArrayList<Entry> dataValue) {

//        ArrayList<ILineDataSet> iLineDataSets2 = new ArrayList<>();

        LineDataSet dataSet = new LineDataSet(dataValue, "Income");
//        dataSet.setLabel("Data Set1");
        dataSet.setLineWidth(4);
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(15f);
        dataSet.setValues(dataValue);

        LineData lineData = new LineData(dataSet);

        XAxis xAxis = lineChartDB.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);


        List<String> list2 = new ArrayList<>();

        list2.add("17/07/2022 04:02PM");
        list2.add("19/07/2022 04:02PM");
        list2.add("21/07/2022 04:02PM");
        list2.add("22/07/2022 04:02PM");
        list2.add("24/07/2022 04:02PM");

        Log.e("List2->", String.valueOf(list2));

        lineChartDB.getAxisRight().setDrawLabels(false);

        lineChartDB.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {

                String label = "";

                if (value == 1)
                    label = list2.get(0);
                else if (value == 2)
                    label = list2.get(1);

                else if (value == 3)
                    label = list2.get(2);

                else if (value == 4)
                    label = list2.get(3);

                else if (value == 5)
                    label = list2.get(4);

                return label;

            }
        });

        lineChartDB.setXAxisRenderer(new CustomXAxisRenderer
                (
                        lineChartDB.getViewPortHandler(),
                        lineChartDB.getXAxis(),
                        lineChartDB.getTransformer(YAxis.AxisDependency.LEFT)
                ));

        lineChartDB.setExtraBottomOffset(20f);

        lineChartDB.setData(lineData);
        lineChartDB.getDescription().setText("Income Chart");


//        lineChartDB.clear();
//        lineChartDB.setData(lineData);
//        lineChartDB.invalidate();

    }


    /////////
}