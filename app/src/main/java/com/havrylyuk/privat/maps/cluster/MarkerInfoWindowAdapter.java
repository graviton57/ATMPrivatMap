package com.havrylyuk.privat.maps.cluster;

import android.app.Activity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.havrylyuk.privat.R;
import com.havrylyuk.privat.maps.cluster.BaseInfoWindowAdapter;

/**
 *
 * Created by Igor Havrylyuk on 26.01.2017.
 */

public class MarkerInfoWindowAdapter extends BaseInfoWindowAdapter {


    public MarkerInfoWindowAdapter(Activity context) {
        super(context);
    }

    @Override
    public   void render (Marker marker, View view) {
        ImageView markerBadge = (ImageView) view.findViewById(R.id.badge);
        TextView markerTitle = (TextView)view.findViewById(R.id.title);
        TextView markerSnippet = (TextView)view.findViewById(R.id.snippet);
        markerBadge.setImageResource(R.drawable.privatbank);
        if (!TextUtils.isEmpty(marker.getTitle())) {
            markerTitle.setText(context.getString(R.string.format_title,marker.getTitle()));
        }
        int startClickLength = context.getString(R.string.click_detail).length();
        if (!TextUtils.isEmpty(marker.getSnippet())) {
            SpannableString snippetText = new SpannableString(marker.getSnippet() +
                    "\n" + context.getString(R.string.click_detail));
            snippetText.setSpan(new URLSpan(""), snippetText.length() - startClickLength, snippetText.length(), 0);
            markerSnippet.setText(snippetText);
        } else {
            SpannableString snippetText = new SpannableString(context.getString(R.string.click_detail));
            snippetText.setSpan(new URLSpan(""), 0, startClickLength, 0);
            markerSnippet.setText(snippetText);
        }
    }

}
