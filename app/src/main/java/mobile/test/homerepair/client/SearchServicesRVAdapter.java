package mobile.test.homerepair.client;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Users;

public class SearchServicesRVAdapter extends RecyclerView.Adapter<SearchServicesRVAdapter.ViewHolder> {

    private ArrayList<Users> usersArrayList;
    private Context context;
    private ItemClickListener mClickListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SearchServicesRVAdapter(ArrayList<Users> usersArrayList, Context context){
        this.usersArrayList = usersArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchServicesRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_search_sevices_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchServicesRVAdapter.ViewHolder holder, int position) {
        Users users = usersArrayList.get(position);

        try{
            String pictureURL = null;
            pictureURL = users.getPictureURL();
            Picasso.with(context).load(pictureURL).into(holder.img_searchCompany);
        }catch (Exception e){
            holder.img_searchCompany.setImageResource(R.drawable.profilepicturenoimage);
//            holder.img_searchCompany.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.profilepicturenoimage));
            e.printStackTrace();
        }


//        float serviceRating = Float.parseFloat(users.getUserServiceRating());

        holder.tv_searchCompanyName.setText(users.getCompanyName());
        holder.tv_searchCompanyServiceType.setText(users.getServiceType());
//        holder.tv_userRating.setText(String.format("%.1f", users.getUserServiceRating()));
//        holder.tv_userRating.setText(users.getUserServiceRating());

        try{
            holder.ratingBar_user.setRating(Float.parseFloat(users.getUserServiceRating()));
            holder.tv_userRating.setText(users.getUserServiceRating());
            System.out.println(users.getUserServiceRating());
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(users.getUserServiceRating());
            holder.ratingBar_user.setRating(0.0F);
//            holder.tv_userRating.setText(0);
        }



    }


    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public interface ItemClickListener{
        void onItemClick (View view, int position);
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_searchCompanyName,tv_searchCompanyServiceType;
        MaterialIconView btn_searchMoreDetail;
        CircleImageView img_searchCompany;
        RatingBar ratingBar_user;
        TextView tv_userRating;


        public ViewHolder(@NonNull View itemView){
            super(itemView);

            tv_searchCompanyName = itemView.findViewById(R.id.tv_searchCompanyName);
            tv_searchCompanyServiceType = itemView.findViewById(R.id.tv_searchCompanyServiceType);
            img_searchCompany = itemView.findViewById(R.id.img_searchCompany);
            btn_searchMoreDetail = itemView.findViewById(R.id.btn_searchMoreDetail);
            ratingBar_user = itemView.findViewById(R.id.ratingBar_user);
            tv_userRating = itemView.findViewById(R.id.tv_userRating);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view,getAdapterPosition());
        }
    }

    void setClickListener(SearchServicesRVAdapter.ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }


    public Users getItem(int id){
        return usersArrayList.get(id);
    }


}
