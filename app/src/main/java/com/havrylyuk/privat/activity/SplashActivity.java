package com.havrylyuk.privat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
*
* Created by Igor Havrylyuk on 26.01.2017.
*/
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

}
