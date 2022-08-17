package mobile.test.homerepair.testDemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import mobile.test.homerepair.R;

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

    }
}