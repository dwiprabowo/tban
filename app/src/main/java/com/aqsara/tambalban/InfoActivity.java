package com.aqsara.tambalban;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * Created by dwi on 17/11/15.
 */
public class InfoActivity extends Base {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        InfoItem infoItems[] = new InfoItem[]{
                new InfoItem(R.drawable.maps_icon, "Menu Nomor 1"),
                new InfoItem(R.drawable.maps_icon, "Menu Nomor 2"),
                new InfoItem(R.drawable.maps_icon, "Menu Nomor 3"),
                new InfoItem(R.drawable.maps_icon, "Menu Nomor 4"),
                new InfoItem(R.drawable.maps_icon, "Menu Nomor 5"),
        };

        InfoAdapter adapter = new InfoAdapter(this, R.layout.info_listview_header_row, infoItems);
        listView = (ListView)findViewById(R.id.info_list_view);

        View header = (View)getLayoutInflater().inflate(R.layout.info_listview_header_row, null);
        listView.addHeaderView(header);

        listView.setAdapter(adapter);
    }
}
