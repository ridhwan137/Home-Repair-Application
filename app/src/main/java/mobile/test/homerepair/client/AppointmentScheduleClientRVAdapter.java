package mobile.test.homerepair.client;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Appointment;

public class AppointmentScheduleClientRVAdapter extends RecyclerView.Adapter<AppointmentScheduleClientRVAdapter.ViewHolder>{

    private ArrayList<Appointment> appointmentArrayList;
    private Context context;
    private AppointmentScheduleClientRVAdapter.ItemClickListener mClickListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String pictureURL;


    public AppointmentScheduleClientRVAdapter(ArrayList<Appointment> appointmentArrayList, Context context){
        this.appointmentArrayList = appointmentArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public AppointmentScheduleClientRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_appointment_schedule_client_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentScheduleClientRVAdapter.ViewHolder holder, int position) {
        Appointment appointment = appointmentArrayList.get(position);

        String pictureURL = null;
        pictureURL = appointment.getProviderPictureURL();
        Picasso.with(context).load(pictureURL).into(holder.img_pictureCompany);

        holder.et_detailCompanyName.setText(appointment.getCompanyName());
        holder.et_detailCompanyServiceType.setText(appointment.getCompanyServiceType());
        holder.et_detailCompanyEmail.setText(appointment.getCompanyEmail());
        holder.et_detailCompanyPhone.setText(appointment.getCompanyPhone());

        // address
        String fullAddress;
        fullAddress = appointment.getCompanyAddress1() + ", ";
        fullAddress += appointment.getCompanyAddress2() + ", \n";
        fullAddress += appointment.getCompanyPostcode() + " ";
        fullAddress += appointment.getCompanyCity() + ",\n";
        fullAddress += appointment.getCompanyState();

        holder.et_detailCompanyAddress.setText(fullAddress);

        holder.tv_detailCompanyDate.setText(appointment.getDate());
        holder.tv_detailCompanyTime.setText(appointment.getTime());

        ///////
        if(appointment.getAppointmentStatus().equals("pending")){
            holder.btn_detailAppointmentStatus.setText(appointment.getAppointmentStatus());
        }else{
//            holder.btn_detailAppointmentStatus.setVisibility(View.INVISIBLE);
            holder.btn_detailAppointmentStatus.setVisibility(View.GONE);
        }
        ///////

//        holder.btn_detailAppointmentStatus.setText(appointment.getAppointmentStatus());
        holder.btn_detailAppointmentStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String appointmentStatus = appointment.getAppointmentStatus();

                if (appointmentStatus.equals("pending")){
                    Intent intent = new Intent(context, PendingAppointmentClient.class);
                    intent.putExtra("appointmentID",appointment.getAppointmentID());
                    context.startActivity(intent);
                    Log.e("testPassAppointmentID", appointment.getAppointmentID());

                    Activity activity = (Activity) context;
                    activity.finish();

                }else if (appointmentStatus.equals("in-progress")){
                    Log.e("testPassAppointmentID", appointment.getAppointmentID());

/*                    Activity activity = (Activity) context;
                    activity.finish();
                    */
                }else{
/*                    Log.e("testPassAppointmentID", appointment.getAppointmentID());

                    Activity activity = (Activity) context;
                    activity.finish();
                    */
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final EditText et_detailCompanyName, et_detailCompanyServiceType, et_detailCompanyEmail,
                et_detailCompanyPhone, et_detailCompanyAddress;

        TextView tv_detailCompanyDate,tv_detailCompanyTime;

        private final ImageView img_pictureCompany;
        private  final Button btn_detailAppointmentStatus;


        public ViewHolder(@NonNull View itemView){
            super(itemView);

            img_pictureCompany = itemView.findViewById(R.id.img_pictureCompany);

            et_detailCompanyName =  itemView.findViewById(R.id.et_detailCompanyName);
            et_detailCompanyServiceType =  itemView.findViewById(R.id.et_detailCompanyServiceType);
            et_detailCompanyEmail =  itemView.findViewById(R.id.et_detailCompanyEmail);
            et_detailCompanyPhone =  itemView.findViewById(R.id.et_detailCompanyPhone);
            et_detailCompanyAddress =  itemView.findViewById(R.id.et_detailCompanyAddress);

            tv_detailCompanyDate =  itemView.findViewById(R.id.tv_detailCompanyDate);
            tv_detailCompanyTime =  itemView.findViewById(R.id.tv_detailCompanyTime);

            btn_detailAppointmentStatus = itemView.findViewById(R.id.btn_detailAppointmentStatus);


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

    void setClickListener(AppointmentScheduleClientRVAdapter.ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }


    public Appointment getItem(int id){
        return appointmentArrayList.get(id);
    }


}
