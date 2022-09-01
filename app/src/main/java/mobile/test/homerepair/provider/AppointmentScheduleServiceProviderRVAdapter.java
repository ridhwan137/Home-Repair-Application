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
import mobile.test.homerepair.model.Appointment;

public class AppointmentScheduleServiceProviderRVAdapter extends RecyclerView.Adapter<AppointmentScheduleServiceProviderRVAdapter.ViewHolder>{

    private ArrayList<Appointment> appointmentArrayList;
//    private ArrayList<Users> usersArrayList;
    private Context context;
    private AppointmentScheduleServiceProviderRVAdapter.ItemClickListener mClickListener;


    public AppointmentScheduleServiceProviderRVAdapter(ArrayList<Appointment> appointmentArrayList, Context context){
        this.appointmentArrayList = appointmentArrayList;
        this.context = context;
    }

/*
    public AppointmentScheduleServiceProviderRVAdapter(ArrayList<Appointment> appointmentArrayList,ArrayList<Users> usersArrayList, Context context){
        this.appointmentArrayList = appointmentArrayList;
        this.usersArrayList = usersArrayList;
        this.context = context;
    }
*/


    @NonNull
    @Override
    public AppointmentScheduleServiceProviderRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_appointment_schedule_service_provider_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentScheduleServiceProviderRVAdapter.ViewHolder holder, int position) {



        Appointment appointment = appointmentArrayList.get(position);

        // Get User Info from table Appointment
        String pictureURL = null;
        pictureURL = appointment.getClientPictureURL();
        Picasso.with(context).load(pictureURL).into(holder.img_pictureClient);

        holder.et_detailClientName.setText(appointment.getClientName());
        holder.et_detailClientEmail.setText(appointment.getClientEmail());
        holder.et_detailClientPhone.setText(appointment.getClientPhone());


        // address
        String fullAddress;
        fullAddress = appointment.getClientAddress1() + ", ";
        fullAddress += appointment.getClientAddress2() + ",\n";
        fullAddress += appointment.getClientPostcode() + " ";
        fullAddress += appointment.getClientCity() + ",\n";
        fullAddress += appointment.getClientState();

        holder.et_detailClientAddress.setText(fullAddress);

        holder.tv_detailClientDate.setText(appointment.getDate());
        holder.tv_detailClientTime.setText(appointment.getTime());

        holder.btn_detailAppointmentStatus.setText(appointment.getAppointmentStatus());
        holder.btn_detailAppointmentStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String appointmentStatus = appointment.getAppointmentStatus();

                if (appointmentStatus.equals("pending")){
                    Intent intent = new Intent(context, ServiceProviderAppointmentPending.class);
                    intent.putExtra("appointmentID",appointment.getAppointmentID());
                    context.startActivity(intent);
                    Log.e("testPassAppointmentID", appointment.getAppointmentID());

                    Activity activity = (Activity) context;
                    activity.finish();

                }else if (appointmentStatus.equals("in-progress")){
                    Intent intent = new Intent(context, ServiceProviderAppointmentInProgress.class);
                    intent.putExtra("appointmentID",appointment.getAppointmentID());
                    context.startActivity(intent);
                    Log.e("testPassAppointmentID", appointment.getAppointmentID());

                    Activity activity = (Activity) context;
                    activity.finish();
                }else{
                    //nothing to set
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return appointmentArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final EditText et_detailClientName, et_detailClientEmail,
                et_detailClientPhone, et_detailClientAddress;

        private final TextView tv_detailClientDate, tv_detailClientTime;

        private final ImageView img_pictureClient;
        private  final Button btn_detailAppointmentStatus;


        public ViewHolder(@NonNull View itemView){
            super(itemView);

            img_pictureClient = itemView.findViewById(R.id.img_pictureClient);

            et_detailClientName =  itemView.findViewById(R.id.et_detailClientName);
            et_detailClientEmail =  itemView.findViewById(R.id.et_detailClientEmail);
            et_detailClientPhone =  itemView.findViewById(R.id.et_detailClientPhone);
            et_detailClientAddress =  itemView.findViewById(R.id.et_detailClientAddress);

            tv_detailClientDate =  itemView.findViewById(R.id.tv_detailClientDate);
            tv_detailClientTime =  itemView.findViewById(R.id.tv_detailClientTime);

            btn_detailAppointmentStatus = itemView.findViewById(R.id.btn_detailAppointmentStatus);


//            itemView.setOnClickListener(this);
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

    void setClickListener(AppointmentScheduleServiceProviderRVAdapter.ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }


    public Appointment getItem(int id){
        return appointmentArrayList.get(id);
    }

}
