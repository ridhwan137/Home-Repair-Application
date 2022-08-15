package mobile.test.homerepair.provider;

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
import mobile.test.homerepair.testDemo.TestProviderDisplayServiceOfferRVAdapter;

public class ProviderDisplayServiceOfferRVAdapter extends RecyclerView.Adapter<ProviderDisplayServiceOfferRVAdapter.ViewHolder>{
    private ArrayList<Services> servicesArrayList;
    private Context context;
    private ProviderDisplayServiceOfferRVAdapter.ItemClickListener mClickListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // creating constructor for our adapter class
    public ProviderDisplayServiceOfferRVAdapter(ArrayList<Services> servicesArrayList, Context context) {
        this.servicesArrayList = servicesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProviderDisplayServiceOfferRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_request_appointment_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderDisplayServiceOfferRVAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Services services = servicesArrayList.get(position);

        holder.tv_detailServiceName.setText(services.getServiceName());
        holder.tv_detailServicePrice.setText(services.getServicePrice());

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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.

            tv_detailServiceName = itemView.findViewById(R.id.tv_detailServiceName);
            tv_detailServicePrice = itemView.findViewById(R.id.tv_detailServicePrice);

            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // allows clicks events to be caught
    void setClickListener(ProviderDisplayServiceOfferRVAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // convenience method for getting data at click position
    public Services getItem(int id) {
        return servicesArrayList.get(id);
    }


}
