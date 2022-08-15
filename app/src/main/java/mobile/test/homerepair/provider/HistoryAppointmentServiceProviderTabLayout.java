package mobile.test.homerepair.provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import mobile.test.homerepair.R;
import mobile.test.homerepair.client.HistoryAppointmentClientTabFragmentAdapter;

public class HistoryAppointmentServiceProviderTabLayout extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private HistoryAppointmentServiceProviderTabFragmentAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_appointment_service_provider_tab_layout);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);

        //Iniatialize and Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Menu Selected
        bottomNavigationView.setSelectedItemId(R.id.menu_historyAppointmentSchedule);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_serviceOffer:
                        startActivity(new Intent(getApplicationContext(),ProviderDisplayServiceOffer.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_appointmentSchedule:
                        startActivity(new Intent(getApplicationContext(),AppointmentScheduleServiceProviderTabLayout.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_historyAppointmentSchedule:
//                        startActivity(new Intent(getApplicationContext(),HistoryAppointmentServiceProvider.class));
//                        overridePendingTransition(0,0);
//                        Toast.makeText(getApplicationContext(), "nothing yet", Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.menu_profile:
                        startActivity(new Intent(getApplicationContext(),ProfileServiceProvider.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });


        tabLayout.addTab(tabLayout.newTab().setText("Complete"));
//        tabLayout.addTab(tabLayout.newTab().setText("Cancel"));
        tabLayout.addTab(tabLayout.newTab().setText("Reject"));


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new HistoryAppointmentServiceProviderTabFragmentAdapter(fragmentManager,getLifecycle());
        viewPager2.setAdapter(fragmentAdapter);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}