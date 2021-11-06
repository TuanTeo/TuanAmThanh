package com.dev.tuanteo.tuanamthanh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.dev.tuanteo.tuanamthanh.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*TuanTeo: Get ActivityMainBinding */
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        /*TuanTeo: Show toolbar */
        setSupportActionBar(mBinding.toolBar);

        View view = mBinding.getRoot();
        setContentView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
}