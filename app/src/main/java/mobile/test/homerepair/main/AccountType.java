package mobile.test.homerepair.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mobile.test.homerepair.R;
import mobile.test.homerepair.client.RegisterClient;
import mobile.test.homerepair.provider.RegisterServiceProvider;

public class AccountType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_type);



        Button btn_accountType = findViewById(R.id.btn_accountType);
        TextView tv_accountDescription = findViewById(R.id.tv_accountDescription);

        Spinner sp_AccountType = findViewById(R.id.sp_AccountType);
        String []accountTypes = {"Select Account Type","Client","Service Provider"};

        final List<String> accountTypeList = new ArrayList<>(Arrays.asList(accountTypes));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,accountTypeList){


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


        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        sp_AccountType.setAdapter(spinnerArrayAdapter);
        sp_AccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                if(position > 0){
                    // Notify the selected item text

                    switch (selectedItemText) {
                        case "Client":
                            tv_accountDescription.setTextColor(Color.parseColor("#000000"));
                            tv_accountDescription.setTextSize(12);
                            tv_accountDescription.setText("*This user will be able to see service provider.");
                            break;
                        case "Service Provider":
                            tv_accountDescription.setTextColor(Color.parseColor("#000000"));
                            tv_accountDescription.setTextSize(12);
                            tv_accountDescription.setText("*This user will be able apply their service they provide");
                            break;
                        default:
                            // nothing
                            break;
                    }


                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        btn_accountType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String getAccountTypeSpinner = sp_AccountType.getSelectedItem().toString();

                if(getAccountTypeSpinner.equals("Select Account Type")){
                    Toast.makeText(getApplicationContext(), "Please Select Account Type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (getAccountTypeSpinner.equals("Client")){
                    Intent intent = new Intent(AccountType.this, RegisterClient.class);
                    startActivity(intent);

                }else if (getAccountTypeSpinner.equals("Service Provider")){
                    Intent intent = new Intent(AccountType.this, RegisterServiceProvider.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}