package com.havrylyuk.privat.util;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 *
 * Created by Igor Havrylyuk on 20.01.2017.
 */

public class ImageHelper {

    public static void load(@NonNull String url, ImageView imageView) {
        Picasso.with(imageView.getContext())
                .load(url)
                //.placeholder(R.drawable.cherry)
                .noFade()
                .into(imageView);
    }

}
