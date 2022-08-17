package mobile.test.homerepair.testDemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mobile.test.homerepair.R;

public class TestCreatePDF extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TestCreatePDFDataObject dataObj = new TestCreatePDFDataObject();

    Button btnSaveAndPrint;
    EditText editTextName,editTextQty;
    Spinner spinner;
    ArrayAdapter<String> adapter;

    String[] itemList;
    double[] itemPrice;
    long invoiceNo = 0;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_create_pdf);

        btnSaveAndPrint = findViewById(R.id.btnSaveAndPrint);
        editTextName = findViewById(R.id.editTextName);
        editTextQty = findViewById(R.id.editTextQty);
        spinner = findViewById(R.id.spinner);

        itemList = new String[]{"Petrol","Diesel"};

        itemPrice = new double[]{72.56,36.56};

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,itemList);
        spinner.setAdapter(adapter);


        btnSaveAndPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInvoiceToDbUsingHashMap();

//                addInvoiceToDbMethodOOP();


            }
        });

        ///////
    }


    public void addInvoiceToDbMethodOOP(){
        dataObj.invoiceNo = invoiceNo + 1;
        dataObj.customerName = String.valueOf(editTextName.getText());
        dataObj.date = new Date().getTime();
        dataObj.fuelType = spinner.getSelectedItem().toString();
        dataObj.fuelQty = Double.parseDouble(String.valueOf(editTextQty.getText()));
        dataObj.amount = Double.valueOf(decimalFormat.format(dataObj.getFuelQty()*itemPrice[spinner.getSelectedItemPosition()]));


        db.collection("invoice").document(String.valueOf(dataObj.invoiceNo))
                .set(dataObj)
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


    public void addInvoiceToDbUsingHashMap(){

        // Create random invoice ID
        Random rand = new Random();
        int randomID = rand.nextInt(9999)+1;

        String invoiceID;
        invoiceID = "invoice" + randomID;

        String customerName = editTextName.getText().toString();
        long date = new Date().getTime();
        String fuelType = spinner.getSelectedItem().toString();
        Double fuelQty = Double.parseDouble(editTextQty.getText().toString());
        Double amount = Double.valueOf(decimalFormat.format(fuelQty*itemPrice[spinner.getSelectedItemPosition()]));

        // Put all data to hash map
        Map<String, Object> data = new HashMap<>();
        data.put("invoiceID", invoiceID);
        data.put("customerName", customerName);
        data.put("date", date);
        data.put("fuelType", fuelType);
        data.put("fuelQty", fuelQty);
        data.put("amount", amount);

        db.collection("invoice").document(invoiceID)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

//                        Toast.makeText(getApplicationContext(), "Added successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error writing document->", "Error writing document");
                    }
                });

        createPDF();

    }


    public void createPDF(){


        PdfDocument myPdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint forLinePaint = new Paint();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(250,350,1).create();

        PdfDocument.Page myPage=myPdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();


        paint.setTextSize(15.5f);
        paint.setColor(Color.rgb(0,50,250));
        canvas.drawText("Home Repair Apps",20,20,paint); // Title -> Home Repair Apps

        paint.setTextSize(8.5f);
        canvas.drawText("Plot No.2,Shri Bharat Marg",20,40,paint); // Address
        canvas.drawText("Ayodhya 224123",20,55,paint); // Address

        forLinePaint.setStyle(Paint.Style.STROKE);
        forLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));
        forLinePaint.setStrokeWidth(2);

        canvas.drawLine(20,65,230,65,forLinePaint); // Stroke Line

        canvas.drawText("Customer Name:" + editTextName.getText(),20,80,paint); // Customer Name

        canvas.drawLine(20,90,230,90,forLinePaint); //Stroke Line

        canvas.drawText("Purchase:",20,105,paint); // Purchase -> Service Charges

        canvas.drawText(spinner.getSelectedItem().toString(),20,135,paint); // Petrol/Diesel -> Clogged Sink

        canvas.drawText(editTextQty.getText() + "litre",120,135,paint); // maybe no need

        double amount = itemPrice[spinner.getSelectedItemPosition()] * Double.parseDouble(editTextQty.getText().toString()); // order total price
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.valueOf(decimalFormat.format(amount)),230,135,paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("+",20,175,paint);

        canvas.drawText("Tax5%",120,175,paint);
        paint.setTextAlign(Paint.Align.RIGHT);

        canvas.drawText(decimalFormat.format(amount*5/100),230,175,paint);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawLine(20,210,230,210,forLinePaint);

        paint.setTextSize(10f);
        canvas.drawText("Total",120,225,paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(decimalFormat.format((amount*5/100)+amount),230,225,paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(8.5f);
        canvas.drawText("Date:"+datePatternFormat.format(new Date().getTime()),20,260,paint);

        canvas.drawText(String.valueOf(invoiceNo+1),20,275,paint);

        canvas.drawText("Payment Method:Cash",20,290,paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(12f);
        canvas.drawText("Thank you!",canvas.getWidth()/2,320,paint);
        myPdfDocument.finishPage(myPage);


        String pdfDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File pdfFile = new File(pdfDirectory, "AppointmentInvoice" + ".pdf");

        try{

            myPdfDocument.writeTo(new FileOutputStream(pdfFile));

        }catch(IOException e){
            e.printStackTrace();
        }finally {
            myPdfDocument.close();

            DownloadManager downloadManager = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
            downloadManager.addCompletedDownload(pdfFile.getName(), pdfFile.getName(), true, "application/pdf",pdfFile.getAbsolutePath(),pdfFile.length(),true);

            Toast.makeText(getApplicationContext(), "Saved to Downloads.", Toast.LENGTH_LONG).show();
        }




    }



    ///////
}