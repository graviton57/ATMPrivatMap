package com.havrylyuk.privat.maps.cluster;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.havrylyuk.privat.R;
import com.havrylyuk.privat.maps.cluster.BaseInfoWindowAdapter;

/**
 *
 * Created by Igor Havrylyuk on 28.01.2017.
 */

public class DetailInfoWindowAdapter extends BaseInfoWindowAdapter {

    public DetailInfoWindowAdapter(Activity context) {
        super(context);
    }

    @Override
    public void render(Marker marker, View view) {
        ImageView markerBadge = (ImageView) view.findViewById(R.id.badge);
        TextView markerTitle = (TextView)view.findViewById(R.id.title);
        TextView markerSnippet = (TextView)view.findViewById(R.id.snippet);
        markerBadge.setImageResource(R.drawable.privatbank);
        if (!TextUtils.isEmpty(marker.getTitle())) {
            markerTitle.setText(context.getString(R.string.format_title,marker.getTitle()));
        }
        if (!TextUtils.isEmpty(marker.getSnippet())) {
            markerSnippet.setText(marker.getSnippet());
        }
    }

}
