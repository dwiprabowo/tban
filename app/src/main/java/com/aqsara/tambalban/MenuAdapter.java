package com.aqsara.tambalban;

import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * Created by dwi on 013, 10/13/15.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder>{

    private String[] mDataset;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onClick(View view, int position);
    }

    public MenuAdapter(String[] myDataset, OnItemClickListener listener){
        mDataset = myDataset;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView mTextView;

        public ViewHolder(TextView v){
            super(v);
            mTextView = v;
        }
    }
}
