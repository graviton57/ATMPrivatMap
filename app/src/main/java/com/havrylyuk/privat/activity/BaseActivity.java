package com.havrylyuk.privat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;

import com.havrylyuk.privat.R;

/**
 *  Base Activity Class
 * Created by Igor Havrylyuk on 26.01.2017.
 */

public  abstract class BaseActivity extends AppCompatActivity {

    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        initToolbar();
    }

    protected abstract int getLayout();

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (null != toolbar) {
            setSupportActionBar(toolbar);
        }

    }
}
