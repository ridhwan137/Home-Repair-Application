package mobile.test.homerepair.client;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Services;

public class RequestAppointmentRVAdapter extends RecyclerView.Adapter<RequestAppointmentRVAdapter.ViewHolder> {

    private ArrayList<Services> servicesArrayList;
    private Context context;
    private RequestAppointmentRVAdapter.ItemClickListener mClickListener;


    public RequestAppointmentRVAdapter(ArrayList<Services> servicesArrayList, Context context){
        this.servicesArrayList = servicesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RequestAppointmentRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_request_appointment_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAppointmentRVAdapter.ViewHolder holder, int position) {

        Services services = servicesArrayList.get(position);

        Log.e("serviceName",services.getServiceName());

        holder.tv_detailServiceName.setText(services.getServiceName());
        holder.tv_detailServicePrice.setText(services.getServicePrice());



    }


    @Override
    public int getItemCount() {
        return servicesArrayList.size();
    }

    public interface ItemClickListener{
        void onItemClick (View view, int position);
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView tv_detailServiceName,tv_detailServicePrice;


        public ViewHolder(@NonNull View itemView){
            super(itemView);

            tv_detailServiceName = itemView.findViewById(R.id.tv_detailServiceName);
            tv_detailServicePrice = itemView.findViewById(R.id.tv_detailServicePrice);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view,getAdapterPosition());
        }
    }

    void setClickListener(RequestAppointmentRVAdapter.ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }


    public Services getItem(int id){
        return servicesArrayList.get(id);
    }
}
