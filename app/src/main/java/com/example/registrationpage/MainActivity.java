package com.example.registrationpage;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager2 viewPager = findViewById(R.id.viewpager);

        VPAdapter vpAdapter = new VPAdapter(this);
        vpAdapter.addFragment(new Fragment1(), "REGISTRATION");
        vpAdapter.addFragment(new Fragment2(), "LOGIN");
        viewPager.setAdapter(vpAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(vpAdapter.fragmentTitleList.get(position))
        ).attach();
    }
}