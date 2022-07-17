package mobile.test.homerepair.provider;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AppointmentScheduleServiceProviderTabFragmentAdapter extends FragmentStateAdapter {
    public AppointmentScheduleServiceProviderTabFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            return new AppointmentScheduleServiceProviderTabPending();
        }

        if(position == 1){
            return new AppointmentScheduleServiceProviderTabInProgress();
        }

        return new AppointmentScheduleServiceProviderTabPending();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
