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

public class HistoryAppointmentClientRVAdapter extends RecyclerView.Adapter<HistoryAppointmentClientRVAdapter.ViewHolder> {

    private ArrayList<Appointment> appointmentArrayList;
    private Context context;
    private HistoryAppointmentClientRVAdapter.ItemClickListener mClickListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String pictureURL;


    public HistoryAppointmentClientRVAdapter(ArrayList<Appointment> appointmentArrayList, Context context){
        this.appointmentArrayList = appointmentArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryAppointmentClientRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_history_appointment_client_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAppointmentClientRVAdapter.ViewHolder holder, int position) {
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
        if(appointment.getAppointmentStatus().equals("complete")){
            holder.btn_detailAppointmentStatus.setText(appointment.getAppointmentStatus());
            holder.tv_totalPrice.setText(appointment.getTotalPrice());
        }else{
            holder.btn_detailAppointmentStatus.setVisibility(View.GONE);
            holder.tv_totalPriceView.setVisibility(View.GONE);
            holder.tv_totalPrice.setVisibility(View.GONE);
        }
        ///////


//        holder.btn_detailAppointmentStatus.setText(appointment.getAppointmentStatus());

        holder.btn_detailAppointmentStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String appointmentStatus = appointment.getAppointmentStatus();

                if (appointmentStatus.equals("complete")){
                    Intent intent = new Intent(context, CompleteAppointmentScheduleClient.class);
                    intent.putExtra("appointmentID",appointment.getAppointmentID());
                    context.startActivity(intent);
                    Log.e("testPassAppointmentID", appointment.getAppointmentID());

                    Activity activity = (Activity) context;
                    activity.finish();

                }else if (appointmentStatus.equals("reject")){

                    Log.e("testPassAppointmentID", appointment.getAppointmentID());

                }else{
                    Log.e("testPassAppointmentID", appointment.getAppointmentID());

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

        TextView tv_detailCompanyDate,tv_detailCompanyTime,tv_totalPriceView,tv_totalPrice;

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

            tv_totalPriceView =  itemView.findViewById(R.id.tv_totalPriceView);
            tv_totalPrice =  itemView.findViewById(R.id.tv_totalPrice);

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

    void setClickListener(HistoryAppointmentClientRVAdapter.ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }


    public Appointment getItem(int id){
        return appointmentArrayList.get(id);
    }


    /// Ending Bracket
}
