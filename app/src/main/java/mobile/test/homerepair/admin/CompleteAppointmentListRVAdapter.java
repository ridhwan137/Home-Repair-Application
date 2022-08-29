package mobile.test.homerepair.admin;

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

public class CompleteAppointmentListRVAdapter extends RecyclerView.Adapter<CompleteAppointmentListRVAdapter.ViewHolder> {

//    private ArrayList<Appointment> appointmentArrayList;

    protected ArrayList<Appointment> appointmentArrayList;
    protected Context context;
    protected CompleteAppointmentListRVAdapter.ItemClickListener mClickListener;


    public CompleteAppointmentListRVAdapter(ArrayList<Appointment> appointmentArrayList, Context context){
        this.appointmentArrayList = appointmentArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CompleteAppointmentListRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_complete_appointment_list_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CompleteAppointmentListRVAdapter.ViewHolder holder, int position) {

        Appointment appointment = appointmentArrayList.get(position);

        holder.tv_appointmentID.setText(appointment.getAppointmentID());
        holder.tv_clientName.setText(appointment.getClientName());
        holder.tv_providerName.setText(appointment.getCompanyName());
        holder.tv_appointmentDate.setText(appointment.getDate());
        holder.tv_appointmentTime.setText(appointment.getTime());
        holder.tv_totalPrice.setText(appointment.getTotalPrice());

        holder.tv_dateComplete.setText(appointment.getDateCompleteAppointment());

    }

    @Override
    public int getItemCount() {
        return appointmentArrayList.size();
    }

    public interface ItemClickListener{
        void onItemClick (View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView tv_appointmentID, tv_clientName,tv_providerName,tv_appointmentDate,
                tv_totalPrice,tv_appointmentTime,tv_dateComplete;


        public ViewHolder(@NonNull View itemView){
            super(itemView);

            tv_appointmentID = itemView.findViewById(R.id.tv_appointmentID);
            tv_clientName = itemView.findViewById(R.id.tv_clientName);
            tv_providerName = itemView.findViewById(R.id.tv_providerName);
            tv_appointmentDate = itemView.findViewById(R.id.tv_appointmentDate);
            tv_appointmentTime = itemView.findViewById(R.id.tv_appointmentTime);
            tv_totalPrice = itemView.findViewById(R.id.tv_totalPrice);
            tv_dateComplete = itemView.findViewById(R.id.tv_dateComplete);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view,getAdapterPosition());
        }
    }

    public void setClickListener(CompleteAppointmentListRVAdapter.ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }


    public Appointment getItem(int id){
        return appointmentArrayList.get(id);
    }


}
