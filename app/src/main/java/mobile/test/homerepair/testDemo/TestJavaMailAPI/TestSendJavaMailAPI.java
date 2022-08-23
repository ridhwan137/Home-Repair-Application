package mobile.test.homerepair.testDemo.TestJavaMailAPI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import mobile.test.homerepair.R;

public class TestSendJavaMailAPI extends AppCompatActivity {

    public EditText mEmail;
    public EditText mSubject;
    public EditText mMessage;
    public Button sendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_send_java_mail_api);

        mEmail = (EditText)findViewById(R.id.mailID);
        mMessage = (EditText)findViewById(R.id.messageID);
        mSubject = (EditText)findViewById(R.id.subjectID);
        sendEmail = (Button) findViewById(R.id.sendEmail);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailNotification();
            }
        });

    }


    private void sendEmailNotification() {
        String mail = mEmail.getText().toString().trim();
        String message = mMessage.getText().toString();
        String subject = mSubject.getText().toString().trim();

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message);

        javaMailAPI.execute();
    }


}