package mobile.test.homerepair.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mobile.test.homerepair.R;
import mobile.test.homerepair.client.SearchServicesRVAdapter;
import mobile.test.homerepair.model.Users;

public class ApproveRegistrationAdminRVAdapter extends RecyclerView.Adapter<ApproveRegistrationAdminRVAdapter.ViewHolder>{

    private ArrayList<Users> usersArrayList;
    private Context context;
    private ApproveRegistrationAdminRVAdapter.ItemClickListener mClickListener;


    public ApproveRegistrationAdminRVAdapter(ArrayList<Users> usersArrayList, Context context){
        this.usersArrayList = usersArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public ApproveRegistrationAdminRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_approve_registration_admin_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ApproveRegistrationAdminRVAdapter.ViewHolder holder, int position) {
        Users users = usersArrayList.get(position);

//        String pictureURL = null;
//        pictureURL = users.getPictureURL();
//        Picasso.with(context).load(pictureURL).into(holder.img_searchCompany);

        holder.tv_searchCompanyName.setText(users.getCompanyName());
        holder.tv_searchCompanyServiceType.setText(users.getServiceType());
        holder.tv_searchCompanyDateApply.setText(users.getDateRegistration());
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public interface ItemClickListener{
        void onItemClick (View view, int position);
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView tv_searchCompanyName,tv_searchCompanyServiceType,tv_searchCompanyDateApply;
//        private final CircleImageView img_searchCompany;


        public ViewHolder(@NonNull View itemView){
            super(itemView);

            tv_searchCompanyName = itemView.findViewById(R.id.tv_searchCompanyName);
            tv_searchCompanyServiceType = itemView.findViewById(R.id.tv_searchCompanyServiceType);
            tv_searchCompanyDateApply = itemView.findViewById(R.id.tv_searchCompanyDateApply);
//            img_searchCompany = itemView.findViewById(R.id.img_searchCompany);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view,getAdapterPosition());
        }
    }

    void setClickListener(ApproveRegistrationAdminRVAdapter.ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }


    public Users getItem(int id){
        return usersArrayList.get(id);
    }


}
