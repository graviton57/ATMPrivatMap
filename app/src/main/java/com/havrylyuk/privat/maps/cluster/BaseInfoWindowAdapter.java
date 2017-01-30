package com.havrylyuk.privat.maps.cluster;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.havrylyuk.privat.R;

/**
 *
 * Created by Igor Havrylyuk on 28.01.2017.
 */

public abstract class BaseInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    protected final View window;
    protected Context context;

    public BaseInfoWindowAdapter(Activity context) {
        this.context = context;
        window = context.getLayoutInflater().inflate(R.layout.maps_custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        render(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        return null;
    }

    public abstract  void render (Marker marker, View view);


    }
