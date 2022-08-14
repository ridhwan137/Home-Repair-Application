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

    }
}