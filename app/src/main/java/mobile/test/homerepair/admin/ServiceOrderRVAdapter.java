package mobile.test.homerepair.admin;

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

public class ServiceOrderRVAdapter extends RecyclerView.Adapter<ServiceOrderRVAdapter.ViewHolder>{
    private ArrayList<Order> orderArrayList;
    private Context context;
    private ServiceOrderRVAdapter.ItemClickListener mClickListener;

//    Services services;

    Order order;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String TAG = "TAG";

    // creating constructor for our adapter class
    public ServiceOrderRVAdapter(ArrayList<Order> orderArrayList, Context context) {
        this.orderArrayList = orderArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceOrderRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_in_progress_appointment_service_provider_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceOrderRVAdapter.ViewHolder holder, int position) {
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
                                Intent intent = new Intent(context, InProgressAppointmentDetail.class);
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

    public void setClickListener(ServiceOrderRVAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    public Order getItem(int id) {
        return orderArrayList.get(id);
    }


    // Ending Bracket
}
