package mobile.test.homerepair.testDemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    Button btnSaveAndPrint,btnPrint;
    EditText editTextName,editTextQty;
    Spinner spinner;
    ArrayAdapter<String> adapter;

    String[] itemList;
    double[] itemPrice;
    long invoiceNo = 0;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");


    PdfDocument myPdfDocument = new PdfDocument();
    Paint forLinePaint = new Paint();
    PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(250,350,1).create();

    PdfDocument.Page myPage=myPdfDocument.startPage(myPageInfo);
    Canvas canvas = myPage.getCanvas();
    Paint paint = new Paint();




    // For Test Demo
    String title = "Home Repair Apps";
    String address1 = "Plot No.2, Shri Bharat Marg";
    String address2 = "Ayodhya 224123";
    String customerName = null;
    String purchaseTitle = "Purchase :";
    String fuelType;
    String fuelQuantity;
    String fuelPrice;
    String tax = "Tax 5%";
    String taxPrice = "";
    String totalPrice = "";
    String date;
    String invoiceID;
    String paymentMethod = "Payment Method: Cash";
    String greeting = "Thank You";

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
//                addInvoiceToDbUsingHashMap();

//                addInvoiceToDbMethodOOP();




            }
        });




        btnPrint = findViewById(R.id.btnPrint);

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFormDb2();
//                createPDFGetDataFromDb(invoiceID,customerName,date,fuelQuantity,fuelType,totalPrice);
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



    public void getDataFormDb1(){

        String TAG = "TAG";
/*
        // For Test Demo
        String title = "Home Repair Apps";
        String address1 = "Plot No.2, Shri Bharat Marg";
        String address2 = "Ayodhya 224123";
        String customerName = null;
        String purchaseTitle = "Purchase :";
        String fuelType;
        String fuelQuantity;
        String fuelPrice;
        String tax = "Tax 5%";
        String taxPrice = "";
        String totalPrice = "";
        String date;
        String invoiceID;
        String paymentMethod = "Payment Method: Cash";
        String greeting = "Thank You";*/


/*
        // For Appointment Invoice
        String invoiceTitle = "Home Repair Apps";
        String invoiceAppointmentID = "";
        String invoiceServiceProviderName = "";
        String invoiceClientName = "";
        String invoiceDateComplete = "";
        String invoicePaymentMethod = "Payment Method: Cash";
        String invoiceGreeting = "Thank You";
        String invoiceTitleServiceCharges = "Service Charges:";
        String invoiceTitleServiceOffer = "";
        String invoiceTitleServicePrice = "";
        String invoiceServiceOffer = "";
        String invoiceServicePrice = "";
        String invoiceTitleTotalPrice = "";
        String invoiceTotalPrice = "";*/

        DocumentReference docRef = db.collection("invoice").document();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()){

                        invoiceID = document.getData().get("invoiceID").toString();
                        customerName = document.getData().get("customerName").toString();
                        date = document.getData().get("date").toString();
                        fuelQuantity = document.getData().get("fuelQty").toString();
                        fuelType = document.getData().get("fuelType").toString();
                        totalPrice = document.getData().get("amount").toString();


                    }else{
                        // No document
                        Log.e(TAG,"no document");
                    }
                }else{
                    Log.e(TAG,"get failed with",task.getException());
                }

            }
        });
    }


    public void getDataFormDb2(){

        db.collection("invoice")
                .whereEqualTo("invoiceID","invoice3535")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getDataFormDb2->", document.getId() + " => " + document.getData());

                                invoiceID = document.getData().get("invoiceID").toString();
                                customerName = document.getData().get("customerName").toString();
                                date = document.getData().get("date").toString();
                                fuelQuantity = document.getData().get("fuelQty").toString();
                                fuelType = document.getData().get("fuelType").toString();
                                totalPrice = document.getData().get("amount").toString();

                            }

                            createPDF_GetDataFromDb_UsingForLoop_ForXnY(invoiceID,customerName,date,fuelQuantity,fuelType,totalPrice);

//                            createPDFGetDataFromDb(invoiceID,customerName,date,fuelQuantity,fuelType,totalPrice);

                        } else {
                            Log.e("getDataFormDb2->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public void createPDFGetDataFromDb(String invoiceID,String customerName, String date, String fuelQuantity, String fuelType, String totalPrice){

        PdfDocument myPdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint forLinePaint = new Paint();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(250,350,1).create();

        PdfDocument.Page myPage=myPdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();


        paint.setTextSize(15.5f);
        paint.setColor(Color.rgb(0,50,250));
        canvas.drawText(title,20,20,paint); // Title -> Home Repair Apps

        paint.setTextSize(8.5f);
        canvas.drawText(address1,20,40,paint); // Address
        canvas.drawText(address2,20,55,paint); // Address

        forLinePaint.setStyle(Paint.Style.STROKE);
        forLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));
        forLinePaint.setStrokeWidth(2);

        canvas.drawLine(20,65,230,65,forLinePaint); // Stroke Line

        canvas.drawText(customerName,20,80,paint); // Customer Name

        canvas.drawLine(20,90,230,90,forLinePaint); //Stroke Line

        canvas.drawText(purchaseTitle,20,105,paint); // Purchase -> Service Charges

        canvas.drawText(fuelType,20,135,paint); // Petrol/Diesel -> Clogged Sink

        canvas.drawText(fuelQuantity + " litre",120,135,paint); // maybe no need

        double amount = Double.parseDouble(totalPrice); // order total price
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.valueOf(decimalFormat.format(amount)),230,135,paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("+",20,175,paint);

        canvas.drawText("Tax 5%",120,175,paint);
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
        canvas.drawText("Date:" + date,20,260,paint);

        canvas.drawText(invoiceID,20,275,paint);

        canvas.drawText(paymentMethod,20,290,paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(12f);
        canvas.drawText(greeting,canvas.getWidth()/2,320,paint);


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


    public void create_PDFGetDataFromDb2_InitializeXnY(String invoiceID, String customerName, String date, String fuelQuantity, String fuelType, String totalPrice){

        int x_static = 20;
        int y_static = 20;

        int x_plus = 0;
        int y_plus = 0;

        PdfDocument myPdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint forLinePaint = new Paint();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(250,350,1).create();

        PdfDocument.Page myPage=myPdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();


        y_plus += 20;
        paint.setTextSize(15.5f);
        paint.setColor(Color.rgb(0,50,250));
        canvas.drawText(title,x_static,y_plus,paint); // Title -> Home Repair Apps

        Log.e("y_plus1", String.valueOf(y_plus));

        paint.setTextSize(8.5f);
        y_plus += 20;
        canvas.drawText(address1,x_static,y_plus,paint); // Address
        y_plus += 15;
        canvas.drawText(address2,x_static,y_plus,paint); // Address

        Log.e("y_plus2", String.valueOf(y_plus));

        forLinePaint.setStyle(Paint.Style.STROKE);
        forLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));
        forLinePaint.setStrokeWidth(2);

        canvas.drawLine(20,65,230,65,forLinePaint); // Stroke Line

        y_plus += 25;
        canvas.drawText(customerName,x_static,y_plus,paint); // Customer Name 80

        Log.e("y_plus3", String.valueOf(y_plus));

        canvas.drawLine(20,90,230,90,forLinePaint); //Stroke Line

        canvas.drawText(purchaseTitle,x_static,105,paint); // Purchase -> Service Charges

        y_plus += 55;
        canvas.drawText(fuelType,x_static,y_plus,paint); // Petrol/Diesel -> Clogged Sink 135

        Log.e("y_plus4", String.valueOf(y_plus));

        canvas.drawText(fuelQuantity + " litre",120,135,paint); // maybe no need

        double amount = Double.parseDouble(totalPrice); // order total price
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.valueOf(decimalFormat.format(amount)),230,135,paint);

        paint.setTextAlign(Paint.Align.LEFT);

        y_plus += 40;
        canvas.drawText("+",x_static,y_plus,paint); // 175

        Log.e("y_plus5", String.valueOf(y_plus));

        canvas.drawText("Tax 5%",120,y_plus,paint); // 175
        paint.setTextAlign(Paint.Align.RIGHT);

        Log.e("y_plus6", String.valueOf(y_plus));

        canvas.drawText(decimalFormat.format(amount*5/100),230,y_plus,paint); // 175
        paint.setTextAlign(Paint.Align.LEFT);

        Log.e("y_plus7", String.valueOf(y_plus));

        canvas.drawLine(20,210,230,210,forLinePaint);

        paint.setTextSize(10f);

        y_plus += 50;
        canvas.drawText("Total",120,y_plus,paint); // 225

        Log.e("y_plus8", String.valueOf(y_plus));

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(decimalFormat.format((amount*5/100)+amount),230,y_plus,paint); // 225

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(8.5f);

        y_plus += 35;
        canvas.drawText("Date:" + date,x_static,y_plus,paint); // 260

        y_plus += 15;
        canvas.drawText(invoiceID,x_static,y_plus,paint); // 275

        Log.e("y_plus9", String.valueOf(y_plus));

        y_plus += 15;
        canvas.drawText(paymentMethod,x_static,y_plus,paint); // 290

        Log.e("y_plus10", String.valueOf(y_plus));

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(12f);

        y_plus += 30;
        canvas.drawText(greeting,canvas.getWidth()/2,y_plus,paint); // 320

        Log.e("y_plus11", String.valueOf(y_plus));

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


    public void createPDF_GetDataFromDb_UsingForLoop_ForXnY(String invoiceID,String customerName, String date, String fuelQuantity, String fuelType, String totalPrice){

        myPdfDocument = new PdfDocument();
        forLinePaint = new Paint();
        myPageInfo = new PdfDocument.PageInfo.Builder(250,350,1).create();

        myPage=myPdfDocument.startPage(myPageInfo);
        canvas = myPage.getCanvas();
        paint = new Paint();

        paint.setTextSize(15.5f);
        paint.setColor(Color.rgb(0,50,250));
        canvas.drawText(title,20,20,paint); // Title -> Home Repair Apps

        paint.setTextSize(8.5f);
        canvas.drawText(address1,20,40,paint); // Address
        canvas.drawText(address2,20,55,paint); // Address

        forLinePaint.setStyle(Paint.Style.STROKE);
        forLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));
        forLinePaint.setStrokeWidth(2);

        canvas.drawLine(20,65,230,65,forLinePaint); // Stroke Line

        canvas.drawText("Customer Name: "+customerName,20,80,paint); // Customer Name

        canvas.drawLine(20,90,230,90,forLinePaint); //Stroke Line

        canvas.drawText(purchaseTitle,20,105,paint); // Purchase -> Service Charges



        //////// Do Looping Here

        int i;
        int yPlus = 135;
        double amount = Double.parseDouble(totalPrice);
        double totalAmount = 0.00;

        for ( i = 0; i < 5;i++){

            Log.e("i->", String.valueOf(i));

            yPlus = yPlus + 20;
            Log.e("yPlus->", String.valueOf(yPlus));

            canvas.drawText(fuelType,20,yPlus,paint); // Petrol/Diesel -> Clogged Sink
            Log.e("fuelType2->",fuelType);

            canvas.drawText(fuelQuantity + " litre",120,yPlus,paint); // maybe no need
            Log.e("fuelQuantity2->",fuelQuantity);

//            amount = Double.parseDouble(totalPrice); // order total price
            totalAmount += amount;
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.valueOf(decimalFormat.format(amount)),230,yPlus,paint);
            Log.e("totalPrice2->",totalPrice);

            paint.setTextAlign(Paint.Align.LEFT);
        }

        Log.e("yPlusLast->", String.valueOf(yPlus));

        yPlus += 20;

        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawLine(20,yPlus,230,yPlus,forLinePaint);

        paint.setTextSize(10f);

        yPlus += 15;
        canvas.drawText("Total",120,yPlus,paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(decimalFormat.format(totalAmount),230,yPlus,paint);

        yPlus += 35;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(8.5f);
        canvas.drawText("Date:" + date,20,yPlus,paint);

        yPlus += 15;
        canvas.drawText(invoiceID,20,yPlus,paint);

        yPlus += 15;
        canvas.drawText(paymentMethod,20,yPlus,paint);

        yPlus += 30;
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(12f);
        canvas.drawText(greeting,canvas.getWidth()/2,yPlus,paint);

        /////////////////////
        /////////////////////
/*

        db.collection("invoice")
                .whereEqualTo("invoiceID","invoice3535")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getDataFormDbForLoop->", document.getId() + " => " + document.getData());


                                String fuelQuantity = document.getData().get("fuelQty").toString();
                                String fuelType = document.getData().get("fuelType").toString();
                                String totalPrice = document.getData().get("amount").toString();
                                Log.e("fuelType->",fuelType);
                                Log.e("fuelQuantity->",fuelQuantity);
                                Log.e("totalPrice->",totalPrice);

                                canvas.drawText(fuelType,20,135,paint); // Petrol/Diesel -> Clogged Sink
                                Log.e("fuelType2->",fuelType);

                                canvas.drawText(fuelQuantity + " litre",120,135,paint); // maybe no need
                                Log.e("fuelQuantity2->",fuelQuantity);

                                double amount = Double.parseDouble(totalPrice); // order total price
                                paint.setTextAlign(Paint.Align.RIGHT);
                                canvas.drawText(String.valueOf(decimalFormat.format(amount)),230,135,paint);
                                Log.e("totalPrice2->",totalPrice);

                                paint.setTextAlign(Paint.Align.LEFT);

                            }

                        } else {
                            Log.e("getDataFormDbForLoop->", "Error getting documents: ", task.getException());
                        }
                    }
                });
*/

        //////// Looping





//
//        paint.setTextAlign(Paint.Align.LEFT);
//        canvas.drawText("+",20,175,paint);
//
//        canvas.drawText("Tax 5%",120,175,paint);
//        paint.setTextAlign(Paint.Align.RIGHT);
//
////        canvas.drawText(decimalFormat.format(amount*5/100),230,175,paint);
//        paint.setTextAlign(Paint.Align.LEFT);
//
//        canvas.drawLine(20,210,230,210,forLinePaint);
//
//        paint.setTextSize(10f);
//        canvas.drawText("Total",120,225,paint);
//
//        paint.setTextAlign(Paint.Align.RIGHT);
////        canvas.drawText(decimalFormat.format((amount*5/100)+amount),230,225,paint);
//        canvas.drawText(decimalFormat.format(totalAmount),230,225,paint);
//
//        paint.setTextAlign(Paint.Align.LEFT);
//        paint.setTextSize(8.5f);
//        canvas.drawText("Date:" + date,20,260,paint);
//
//        canvas.drawText(invoiceID,20,275,paint);
//
//        canvas.drawText(paymentMethod,20,290,paint);
//
//        paint.setTextAlign(Paint.Align.CENTER);
//        paint.setTextSize(12f);
//        canvas.drawText(greeting,canvas.getWidth()/2,320,paint);
//



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