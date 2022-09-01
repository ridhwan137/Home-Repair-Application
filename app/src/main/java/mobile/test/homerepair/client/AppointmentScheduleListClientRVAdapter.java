package mobile.test.homerepair.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Appointment;

public class AppointmentScheduleListClientRVAdapter extends RecyclerView.Adapter<AppointmentScheduleListClientRVAdapter.ViewHolder>{

    private ArrayList<Appointment> appointmentArrayList;
    private Context context;
    private AppointmentScheduleListClientRVAdapter.ItemClickListener mClickListener;

    public AppointmentScheduleListClientRVAdapter(ArrayList<Appointment> appointmentArrayList, Context context){
        this.appointmentArrayList = appointmentArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public AppointmentScheduleListClientRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_appointment_list_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentScheduleListClientRVAdapter.ViewHolder holder, int position) {
        Appointment appointment = appointmentArrayList.get(position);

        holder.tv_appointmentID.setText(appointment.getAppointmentID());
        holder.tv_providerName.setText(appointment.getCompanyName());
        holder.tv_appointmentDate.setText(appointment.getDate());
        holder.tv_appointmentTime.setText(appointment.getTime());

        holder.tv_providerServiceTitle.setVisibility(View.VISIBLE);
        holder.tv_providerService.setVisibility(View.VISIBLE);
        holder.tv_providerService.setText(appointment.getCompanyServiceType());

        holder.tv_clientNameTitle.setVisibility(View.GONE);
        holder.tv_clientName.setVisibility(View.GONE);
        holder.tv_clientName.setText(appointment.getClientName());
    }

    @Override
    public int getItemCount() {
        return appointmentArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView tv_appointmentID, tv_clientName,tv_providerName,tv_appointmentDate,tv_appointmentTime,
        tv_clientNameTitle,tv_providerServiceTitle,tv_providerService;


        public ViewHolder(@NonNull View itemView){
            super(itemView);

            tv_appointmentID = itemView.findViewById(R.id.tv_appointmentID);
            tv_clientName = itemView.findViewById(R.id.tv_clientName);
            tv_providerName = itemView.findViewById(R.id.tv_providerName);
            tv_appointmentDate = itemView.findViewById(R.id.tv_appointmentDate);
            tv_appointmentTime = itemView.findViewById(R.id.tv_appointmentTime);
            tv_clientNameTitle = itemView.findViewById(R.id.tv_clientNameTitle);

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

    void setClickListener(AppointmentScheduleListClientRVAdapter.ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }


    public Appointment getItem(int id){
        return appointmentArrayList.get(id);
    }


}
