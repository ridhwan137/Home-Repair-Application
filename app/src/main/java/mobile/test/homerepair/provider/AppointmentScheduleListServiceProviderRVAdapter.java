package mobile.test.homerepair.provider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mobile.test.homerepair.R;
import mobile.test.homerepair.client.AppointmentScheduleListClientRVAdapter;
import mobile.test.homerepair.model.Appointment;

public class AppointmentScheduleListServiceProviderRVAdapter extends RecyclerView.Adapter<AppointmentScheduleListServiceProviderRVAdapter.ViewHolder>{

    private ArrayList<Appointment> appointmentArrayList;
    private Context context;
    private AppointmentScheduleListServiceProviderRVAdapter.ItemClickListener mClickListener;


    public AppointmentScheduleListServiceProviderRVAdapter(ArrayList<Appointment> appointmentArrayList, Context context){
        this.appointmentArrayList = appointmentArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public AppointmentScheduleListServiceProviderRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_appointment_list_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentScheduleListServiceProviderRVAdapter.ViewHolder holder, int position) {


        Appointment appointment = appointmentArrayList.get(position);

        // Show appointment detail
        holder.tv_appointmentID.setText(appointment.getAppointmentID());
        holder.tv_appointmentDate.setText(appointment.getDate());
        holder.tv_appointmentTime.setText(appointment.getTime());

        // Show client name Title and client name
        holder.tv_clientNameTitle.setVisibility(View.VISIBLE);
        holder.tv_clientName.setVisibility(View.VISIBLE);
        holder.tv_clientName.setText(appointment.getClientName());


        // Hide provider service title and service name
        holder.tv_providerServiceTitle.setVisibility(View.GONE);
        holder.tv_providerService.setVisibility(View.GONE);
        holder.tv_providerService.setText(appointment.getCompanyServiceType());


        // Hide provider name title and provider name
        holder.tv_providerNameTitle.setVisibility(View.GONE);
        holder.tv_providerName.setVisibility(View.GONE);
        holder.tv_providerName.setText(appointment.getCompanyName());

    }

    @Override
    public int getItemCount() {
        return appointmentArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView tv_appointmentID, tv_clientName,tv_providerName,tv_appointmentDate,tv_appointmentTime,
                tv_clientNameTitle,tv_providerServiceTitle,tv_providerService,tv_providerNameTitle;


        public ViewHolder(@NonNull View itemView){
            super(itemView);

            tv_appointmentID = itemView.findViewById(R.id.tv_appointmentID);
            tv_appointmentDate = itemView.findViewById(R.id.tv_appointmentDate);
            tv_appointmentTime = itemView.findViewById(R.id.tv_appointmentTime);

            tv_clientNameTitle = itemView.findViewById(R.id.tv_clientNameTitle);
            tv_clientName = itemView.findViewById(R.id.tv_clientName);

            tv_providerNameTitle = itemView.findViewById(R.id.tv_providerNameTitle);
            tv_providerName = itemView.findViewById(R.id.tv_providerName);



            tv_providerServiceTitle = itemView.findViewById(R.id.tv_providerServiceTitle);
            tv_providerService = itemView.findViewById(R.id.tv_providerService);


            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view,getAdapterPosition());
        }
    }


    public interface ItemClickListener{
        void onItemClick (View view, int position);
    }

    void setClickListener(AppointmentScheduleListServiceProviderRVAdapter.ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }


    public Appointment getItem(int id){
        return appointmentArrayList.get(id);
    }

}
