

package com.havrylyuk.privat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.havrylyuk.privat.R;
import com.havrylyuk.privat.util.ImageHelper;
import com.havrylyuk.privat.activity.DetailActivity;
import com.havrylyuk.privat.data.source.local.AcquiringContract;

import android.net.Uri;


/**
 * Simple  Adapter
 * Created by Igor Havrylyuk on 29.01.2017.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FruitViewHolder> {


    public interface OnItemSelectedListener {
        void onItemSelected(Uri uri, FruitViewHolder view);
    }

    private OnItemSelectedListener listener;

    private Cursor mCursor;
    private Context context;
    private int currentPosition = RecyclerView.NO_POSITION;

    public FavoriteAdapter(Context context) {
        this.context = context;
    }

    public void swapCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public FruitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.atm_list_row, parent, false);
        return new FruitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FruitViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.image.setImageDrawable(null);
        final long id = mCursor.getLong(DetailActivity.COL_ID);
        String type = mCursor.getString(DetailActivity.COL_TYPE);
        if (type.equalsIgnoreCase(context.getString(R.string.type_atm))) {
            ImageHelper.load("file:///android_asset/bankomat.png", holder.image);
        } else {
            ImageHelper.load("file:///android_asset/terminal.png", holder.image);
        }
        holder.name.setText(mCursor.getString(DetailActivity.COL_CITY));
        holder.type.setText(mCursor.getString(DetailActivity.COL_ADR));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(currentPosition);
                currentPosition = holder.getAdapterPosition();
                notifyItemChanged(currentPosition);
                if (listener != null) {
                    listener.onItemSelected(AcquiringContract.AcquiringEntry.buildAcquiringUri(id), holder);
                }
            }
        });
        holder.view.setSelected(currentPosition == position);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setListener(@NonNull OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public static class FruitViewHolder extends RecyclerView.ViewHolder {

        public  View view;
        public  ImageView image;
        public  TextView name;
        public  TextView type;

        public FruitViewHolder(View view) {
            super(view);
            this.view = view;
            image= (ImageView) view.findViewById(R.id.list_item_icon);
            name = (TextView) view.findViewById(R.id.list_item_name);
            type = (TextView) view.findViewById(R.id.list_item_point_type);
        }
    }
}
