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
import mobile.test.homerepair.model.Services;
import mobile.test.homerepair.provider.AddServices;
import mobile.test.homerepair.provider.AddServicesRVAdapter;

public class UpdateServiceOfferRVAdapter extends RecyclerView.Adapter<UpdateServiceOfferRVAdapter.ViewHolder>{

    private ArrayList<Services> servicesArrayList;
    private Context context;
    private UpdateServiceOfferRVAdapter.ItemClickListener mClickListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // creating constructor for our adapter class
    public UpdateServiceOfferRVAdapter(ArrayList<Services> servicesArrayList, Context context) {
        this.servicesArrayList = servicesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public UpdateServiceOfferRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.add_service_offer_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateServiceOfferRVAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Services services = servicesArrayList.get(position);

        holder.tv_ServiceName.setText(services.getServiceName());
        holder.tv_ServicePrice.setText(services.getServicePrice());

        holder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("serviceOffer").document(services.getServiceID())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "DocumentSnapshot successfully deleted!");
                                Intent intent = new Intent(context, UpdateServiceOfferOfProvider.class);
                                intent.putExtra("userID",services.getUserID());
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

        private final TextView tv_ServiceName;
        private final TextView tv_ServicePrice;
        private final MaterialIconView btn_Delete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.

            tv_ServiceName = itemView.findViewById(R.id.tv_ServiceName);
            tv_ServicePrice = itemView.findViewById(R.id.tv_ServicePrice);
            btn_Delete = itemView.findViewById(R.id.btn_Delete);

            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // allows clicks events to be caught
    void setClickListener(UpdateServiceOfferRVAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // convenience method for getting data at click position
    public Services getItem(int id) {
        return servicesArrayList.get(id);
    }


}

