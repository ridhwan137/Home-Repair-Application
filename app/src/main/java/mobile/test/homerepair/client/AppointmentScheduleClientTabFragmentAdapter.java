package mobile.test.homerepair.client;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AppointmentScheduleClientTabFragmentAdapter extends FragmentStateAdapter {
    public AppointmentScheduleClientTabFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            return new AppointmentScheduleClientTabPending();
        }

        if(position == 1){
            return new AppointmentScheduleClientTabInProgress();
        }

        return new AppointmentScheduleClientTabPending();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
