package mobile.test.homerepair.testDemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import mobile.test.homerepair.R;
import mobile.test.homerepair.testDemo.TestJavaMailAPI.TestSendJavaMailAPI;

public class TestMainHomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main_home_page);

        Button btn_testFirebaseDataToSpinner = findViewById(R.id.btn_testFirebaseDataToSpinner);
        Button btn_testSendEmail = findViewById(R.id.btn_testSendEmail);
        Button btn_testAdminAddService = findViewById(R.id.btn_testAdminAddService);
        Button btn_testProviderDislayServiceOffer = findViewById(R.id.btn_testProviderDislayServiceOffer);
        Button btn_testCreatePDF = findViewById(R.id.btn_testCreatePDF);
        Button btn_testLineChart = findViewById(R.id.btn_testLineChart);
        Button btn_testLineChartWithDB = findViewById(R.id.btn_testLineChartWithDB);
        Button btn_testRating = findViewById(R.id.btn_testRating);
        Button btn_testSendEmailJavaMail = findViewById(R.id.btn_testSendEmailJavaMail);



        btn_testFirebaseDataToSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestFirebaseDataToSpinner.class);
                startActivity(intent);
            }
        });

        btn_testSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestSendEmail.class);
                startActivity(intent);
            }
        });

        btn_testAdminAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestAdminAddService.class);
                startActivity(intent);
            }
        });

        btn_testProviderDislayServiceOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestProviderDisplayServiceOffer.class);
                startActivity(intent);
            }
        });


        btn_testCreatePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestCreatePDF.class);
                startActivity(intent);
            }
        });

        btn_testLineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestLineChart.class);
                startActivity(intent);
            }
        });



        btn_testLineChartWithDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestLineChartWithDB.class);
                startActivity(intent);
            }
        });


        btn_testRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestRating.class);
                startActivity(intent);
            }
        });

        btn_testSendEmailJavaMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestSendJavaMailAPI.class);
                startActivity(intent);
            }
        });

    }
}