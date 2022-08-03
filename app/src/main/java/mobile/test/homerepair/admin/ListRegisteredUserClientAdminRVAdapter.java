package mobile.test.homerepair.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Users;

public class ListRegisteredUserClientAdminRVAdapter extends RecyclerView.Adapter<ListRegisteredUserClientAdminRVAdapter.ViewHolder>{

    private ArrayList<Users> usersArrayList;
    private Context context;
    private ListRegisteredUserClientAdminRVAdapter.ItemClickListener mClickListener;


    public ListRegisteredUserClientAdminRVAdapter(ArrayList<Users> usersArrayList, Context context){
        this.usersArrayList = usersArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ListRegisteredUserClientAdminRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_list_registered_user_client_admin_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListRegisteredUserClientAdminRVAdapter.ViewHolder holder, int position) {

        Users users = usersArrayList.get(position);

        try{
            String pictureURL = null;
            pictureURL = users.getPictureURL();
            Picasso.with(context).load(pictureURL).into(holder.img_searchUserImage);

        }catch (Exception e){
            e.printStackTrace();
        }


        holder.tv_searchUserName.setText(users.getName());

        holder.tv_searchUserEmail.setText(users.getEmail());



    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public interface ItemClickListener{
        void onItemClick (View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView tv_searchUserName, tv_searchUserEmail;
        private final CircleImageView img_searchUserImage;


        public ViewHolder(@NonNull View itemView){
            super(itemView);

            tv_searchUserName = itemView.findViewById(R.id.tv_searchUserName);
            tv_searchUserEmail = itemView.findViewById(R.id.tv_searchUserEmail);
            img_searchUserImage = itemView.findViewById(R.id.img_searchUserImage);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view,getAdapterPosition());
        }
    }

    void setClickListener(ListRegisteredUserClientAdminRVAdapter.ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }


    public Users getItem(int id){
        return usersArrayList.get(id);
    }

}
