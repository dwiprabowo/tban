package com.aqsara.tambalban;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoAdapter extends ArrayAdapter<InfoItem> {

    Context context;
    int layoutResourceId;
    InfoItem data[] = null;

    public InfoAdapter(Context context, int layoutResourceId, InfoItem[] data){
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        InfoHolder holder;

        if(row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new InfoHolder();
            holder.imageView = (ImageView)row.findViewById(R.id.imageView);
            holder.textView = (TextView)row.findViewById(R.id.textView);

            row.setTag(holder);
        }else{
            holder = (InfoHolder)row.getTag();
        }

        InfoItem infoItem = data[position];
        holder.textView.setText(infoItem.getTitle());
        holder.imageView.setImageResource(infoItem.getIcon());

        return row;
    }

    static class InfoHolder{
        ImageView imageView;
        TextView textView;
    }
}
