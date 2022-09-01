package mobile.test.homerepair.provider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Order;

public class ServiceProviderAppointmentInProgressRVAdapter extends RecyclerView.Adapter<ServiceProviderAppointmentInProgressRVAdapter.ViewHolder> {

    private ArrayList<Order> orderArrayList;
    private Context context;
    private ServiceProviderAppointmentInProgressRVAdapter.ItemClickListener mClickListener;

//    Services services;

    Order order;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String TAG = "TAG";

    // creating constructor for our adapter class
    public ServiceProviderAppointmentInProgressRVAdapter(ArrayList<Order> orderArrayList, Context context) {
        this.orderArrayList = orderArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceProviderAppointmentInProgressRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_in_progress_appointment_service_provider_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceProviderAppointmentInProgressRVAdapter.ViewHolder holder, int position) {
        order = orderArrayList.get(position);


        Log.e("serviceName", order.getServiceName());

        holder.tv_detailServiceName.setText(order.getServiceName());
        holder.tv_detailServicePrice.setText(order.getServicePrice());
        holder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("order").document(order.getOrderID())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "DocumentSnapshot successfully deleted!");
                                Intent intent = new Intent(context, ServiceProviderAppointmentInProgress.class);
                                intent.putExtra("appointmentID",order.getAppointmentID());
                                context.startActivity(intent);

                                Activity activity = (Activity) context;
                                activity.finish();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error deleting document", e);
                            }
                        });

            }
        });

//        deleteTableOrderBasedOnUserID();
//        addServiceOfferToTableOrder();

    }

    @Override
    public int getItemCount() {
//        Log.e("servicesArrayListMaxSize", String.valueOf(orderArrayList.size()));
        return orderArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tv_detailServiceName,
                tv_detailServicePrice;

        private final MaterialIconView btn_Delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_detailServiceName = itemView.findViewById(R.id.tv_detailServiceName);
            tv_detailServicePrice = itemView.findViewById(R.id.tv_detailServicePrice);
            btn_Delete = itemView.findViewById(R.id.btn_Delete);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setClickListener(ServiceProviderAppointmentInProgressRVAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    public Order getItem(int id) {
        return orderArrayList.get(id);
    }


/*

    public void getOrderTableInfoFromDB(){
            db.collection("order").whereEqualTo("providerID", services.getUserID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    try {
                                        String providerID = document.getData().get("orderID").toString() + ", ";

                                        db.collection("order").document(services.getServiceID())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                                                        Intent intent = new Intent(context, AddServices.class);
                                                        context.startActivity(intent);

                                                        Activity activity = (Activity) context;
                                                        activity.finish();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("TAG", "Error deleting document", e);
                                                    }
                                                });

                                    }catch (Exception e){
                                        e.printStackTrace();
                                        Log.e("LogDisplayUserInformation","No Data In Database");
                                    }

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        }
                    });
    }

*/

    /*
    public void deleteTableOrderBasedOnUserID(){

        db.collection("order").document(services.getServiceID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                        Intent intent = new Intent(context, AddServices.class);
                        context.startActivity(intent);

                        Activity activity = (Activity) context;
                        activity.finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error deleting document", e);
                    }
                });


    }
*/
    /*
    public void addServiceOfferToTableOrder(){
        // <-- Get data from recycleView ServiceOffer then add data to new db Order automatically

        //create random id
        Random rand = new Random();
        int randomID = rand.nextInt(99999999) + 1;

        String orderID = "order" + randomID;

        Map<String, Object> completeAppointment = new HashMap<>();

        completeAppointment.put("orderID", orderID);
        completeAppointment.put("providerID", services.getUserID());
        completeAppointment.put("serviceName", services.getServiceName());
        completeAppointment.put("servicePrice", services.getServicePrice());

        Log.e("testMapData", String.valueOf(completeAppointment));


        db.collection("order").document(orderID)
                .set(completeAppointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "successAddToOrderDB");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        // --> Get data from recycleView ServiceOffer then add data to new db Order
    }
*/

/*
    public double getMyTotalPrice() {
        double  totalPrice = 0;
        for (int i = 0; i < orderArrayList.size(); i++) {

//            getPrice += Double.parseDouble(services.getServicePrice());
//            String.format("%.2f", totalPrice);
//            Log.e("totalPrice", String.format("%.2f", totalPrice));

            totalPrice = totalPrice + Double.parseDouble(order.getServicePrice());
        }
        return totalPrice;
    }
*/


    // Ending Bracket
}


