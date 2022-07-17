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

public class CompleteAppointmentScheduleServiceProviderRVAdapter extends RecyclerView.Adapter<CompleteAppointmentScheduleServiceProviderRVAdapter.ViewHolder> {

    private ArrayList<Order> orderArrayList;
    private Context context;
    private CompleteAppointmentScheduleServiceProviderRVAdapter.ItemClickListener mClickListener;

//    Services services;

    Order order;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String TAG = "TAG";

    // creating constructor for our adapter class
    public CompleteAppointmentScheduleServiceProviderRVAdapter(ArrayList<Order> orderArrayList, Context context) {
        this.orderArrayList = orderArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CompleteAppointmentScheduleServiceProviderRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_complete_appointment_schedule_service_provider_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CompleteAppointmentScheduleServiceProviderRVAdapter.ViewHolder holder, int position) {

        order = orderArrayList.get(position);


//        Log.e("serviceName", order.getServiceName());

        holder.tv_detailServiceName.setText(order.getServiceName());
        holder.tv_detailServicePrice.setText(order.getServicePrice());

    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tv_detailServiceName,
                tv_detailServicePrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_detailServiceName = itemView.findViewById(R.id.tv_detailServiceName);
            tv_detailServicePrice = itemView.findViewById(R.id.tv_detailServicePrice);

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

    void setClickListener(CompleteAppointmentScheduleServiceProviderRVAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    public Order getItem(int id) {
        return orderArrayList.get(id);
    }



    // End Bracket
}
