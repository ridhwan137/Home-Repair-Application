package mobile.test.homerepair.client;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HistoryAppointmentListClientTabFragmentAdapter extends FragmentStateAdapter {
    public HistoryAppointmentListClientTabFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 1){
            return new HistoryAppointmentListClientTabCancel();
        }

        if(position == 2){
            return new HistoryAppointmentListClientTabReject();
        }

        return new HistoryAppointmentListClientTabComplete();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
