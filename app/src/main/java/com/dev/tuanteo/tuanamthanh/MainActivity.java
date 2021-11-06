package com.dev.tuanteo.tuanamthanh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dev.tuanteo.tuanamthanh.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*TuanTeo: Get ActivityMainBinding */
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        /*TuanTeo: Show toolbar */
        setSupportActionBar(mBinding.toolBar);

        initComponent();

        View view = mBinding.getRoot();
        setContentView(view);
    }

    private void initComponent() {

//         new TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager, (tab, position) -> {
//            String tabName;
//            if (position == 0) {
//                tabName = "Home";
//            } else {
//                tabName = "Active";
//            }
//            tab.setText(tabName);
//        }).attach();

        // TODO: 11/6/2021 tạo fragment player controler
        mBinding.mainPlayerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(mBinding.mainFrameLayout.getId(), new Fragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.settings) {
            if (mBinding.mainPlayerControl.getVisibility() == View.GONE) {
                mBinding.mainPlayerControl.setVisibility(View.VISIBLE);
            } else {
                mBinding.mainPlayerControl.setVisibility(View.GONE);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}