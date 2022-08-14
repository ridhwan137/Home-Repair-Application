package mobile.test.homerepair.testDemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Services;

public class TestProviderDisplayServiceOfferRVAdapter extends RecyclerView.Adapter<TestProviderDisplayServiceOfferRVAdapter.ViewHolder>{

    private ArrayList<Services> servicesArrayList;
    private Context context;
    private TestProviderDisplayServiceOfferRVAdapter.ItemClickListener mClickListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // creating constructor for our adapter class
    public TestProviderDisplayServiceOfferRVAdapter(ArrayList<Services> servicesArrayList, Context context) {
        this.servicesArrayList = servicesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TestProviderDisplayServiceOfferRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_request_appointment_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TestProviderDisplayServiceOfferRVAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Services services = servicesArrayList.get(position);

        holder.tv_detailServiceName.setText(services.getServiceName());
        holder.tv_detailServicePrice.setText(services.getServicePrice());

/*
        holder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("serviceOffer").document(services.getServiceID())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "DocumentSnapshot successfully deleted!");
                                Intent intent = new Intent(context, TestAdminAddServiceOffer.class);
                                intent.putExtra("serviceTypeID",services.getServiceTypeID());
                                intent.putExtra("serviceType", services.getServiceType());
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
        });*/

    }

    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return servicesArrayList.size();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // creating variables for our text views.

        private final TextView tv_detailServiceName;
        private final TextView tv_detailServicePrice;
//        private final MaterialIconView btn_Delete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.

            tv_detailServiceName = itemView.findViewById(R.id.tv_detailServiceName);
            tv_detailServicePrice = itemView.findViewById(R.id.tv_detailServicePrice);
//            btn_Delete = itemView.findViewById(R.id.btn_Delete);

            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // allows clicks events to be caught
    void setClickListener(TestProviderDisplayServiceOfferRVAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // convenience method for getting data at click position
    public Services getItem(int id) {
        return servicesArrayList.get(id);
    }


}