package mobile.test.homerepair.client;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HistoryAppointmentClientTabFragmentAdapter extends FragmentStateAdapter {
    public HistoryAppointmentClientTabFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 1){
            return new HistoryAppointmentClientTabCancel();
        }

        if(position == 2){
            return new HistoryAppointmentClientTabReject();
        }

        return new HistoryAppointmentClientTabComplete();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
