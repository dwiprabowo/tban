package com.aqsara.tambalban;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by dwi on 10/09/15.
 */
public class CustomAdapter extends BaseAdapter{

    int mainMenuCount;
    Context context;
    int[] imageId;
    private static LayoutInflater inflater = null;

    public CustomAdapter(MainMenuActivity mainActivity, int[] prgmImages){
        context = mainActivity;
        imageId = prgmImages;
        mainMenuCount = prgmImages.length;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mainMenuCount;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.program_list, null);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
        holder.img.setImageResource(imageId[position]);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position){
                    case MainMenuConstant.MAIN_MENU_SEARCH:
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        break;
                }
                Toast.makeText(context, "You Clicked "+position, Toast.LENGTH_SHORT).show();
            }
        });
        return rowView;
    }

    public class Holder{
        TextView tv;
        ImageView img;
    }
}
