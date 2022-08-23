package mobile.test.homerepair.testDemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import mobile.test.homerepair.R;

public class TestSendEmail extends AppCompatActivity {

    Button btn_sendEmail,btn_sendEmailByHardCodeEMail;
    EditText et_textToSend,et_receiverEmailAddress;

    String receiverEmailAddress = null;
    String textToSend = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_send_email);

        btn_sendEmail = findViewById(R.id.btn_sendEmail);
        btn_sendEmailByHardCodeEMail = findViewById(R.id.btn_sendEmailByHardCodeEMail);

        et_textToSend = findViewById(R.id.et_textToSend);
        et_receiverEmailAddress = findViewById(R.id.et_receiverEmailAddress);

        receiverEmailAddress = et_receiverEmailAddress.getText().toString();
        textToSend = et_textToSend.getText().toString();

        Log.e("receiverEmail->",receiverEmailAddress);
        Log.e("message->",textToSend);

        btn_sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendEmailNotification(receiverEmailAddress,textToSend);

                
            }
        });


        btn_sendEmailByHardCodeEMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailNotification();
            }
        });




        ///////////////
    }

    private void sendEmailNotification() {


        try {

            // Gmail
            String stringSenderEmail = "noreply.home.repair.notification@gmail.com";
            String stringPasswordSenderEmail = "ugtatxqwempgnubx";
//            String stringHost = "smtp.gmail.com";

//            String stringReceiverEmail = "home.repair.management@gmail.com";

            String stringReceiverEmail = "iwan1374@gmail.com";


            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.socketFactory.port","465");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

//            properties.put("mail.smtp.starttls.enable","true");
//            properties.put("mail.smtp.debug", "true");




            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(stringReceiverEmail));

            mimeMessage.setSubject("Subject: Android App email");
//            mimeMessage.setText("Hello Programmer, \n\nProgrammer World has sent you this 2nd email. \n\n Cheers!\nProgrammer World");

            mimeMessage.setSubject("Test");
            mimeMessage.setText("Test");

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

            Toast.makeText(getApplicationContext(), "Email Send To: " +stringReceiverEmail, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), " Fail To Send Email ", Toast.LENGTH_SHORT).show();
        }

    }



    private void sendEmailNotification(String receiverEmail,String message) {
        Log.e("receiverEmail->",receiverEmail);
        Log.e("message->",message);

        try {

            // Gmail
            String stringSenderEmail = "noreply.home.repair.notification@gmail.com";
            String stringPasswordSenderEmail = "ugtatxqwempgnubx";
            String stringHost = "smtp.gmail.com";

//            String stringReceiverEmail = "home.repair.management@gmail.com";

            String stringReceiverEmail = receiverEmail;

            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(stringReceiverEmail));

            mimeMessage.setSubject("Subject: Android App email");
            mimeMessage.setText(message);
//            mimeMessage.setText("Hello Programmer, \n\nProgrammer World has sent you this 2nd email. \n\n Cheers!\nProgrammer World");

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), " Fail To Send Email ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            thread.start();

            Toast.makeText(getApplicationContext(), "Email Send To: " +stringReceiverEmail, Toast.LENGTH_SHORT).show();

        } catch (AddressException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), " Fail To Send Email ", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), " Fail To Send Email ", Toast.LENGTH_SHORT).show();
        }

    }


    ///////////////
}