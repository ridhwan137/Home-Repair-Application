package mobile.test.homerepair.provider;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class HistoryAppointmentServiceProviderTabFragmentAdapter extends FragmentStateAdapter {
    public HistoryAppointmentServiceProviderTabFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            return new HistoryAppointmentServiceProviderTabComplete();
        }

//        if(position == 1){
//            return new HistoryAppointmentServiceProviderTabCancel();
//        }

        if(position == 1){
            return new HistoryAppointmentServiceProviderTabReject();
        }

        return new HistoryAppointmentServiceProviderTabComplete();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
