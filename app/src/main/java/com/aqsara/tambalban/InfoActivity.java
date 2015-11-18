package com.aqsara.tambalban;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by dwi on 17/11/15.
 */
public class InfoActivity extends Base {

    private ListView listView;

    @Override
    protected String title() {
        return "Info";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        InfoItem infoItems[] = new InfoItem[]{
                new InfoItem(R.drawable.common_google_signin_btn_icon_dark, "Google Login"),
                new InfoItem(android.R.drawable.ic_search_category_default, "Lokasi Terdekat"),
                new InfoItem(android.R.drawable.ic_menu_directions, "Lokasi TambalBan"),
                new InfoItem(android.R.drawable.ic_input_add, "Tambah Lokasi"),
                new InfoItem(android.R.drawable.ic_delete, "Laporkan Lokasi"),
        };

        InfoAdapter adapter = new InfoAdapter(this, R.layout.info_listview_header_row, infoItems);
        listView = (ListView)findViewById(R.id.info_list_view);

        View header = (View)getLayoutInflater().inflate(R.layout.info_listview_header_row, null);
        listView.addHeaderView(header);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClikInfoItem(position);
            }
        });
    }

    private void onClikInfoItem(int position){
        Class c = null;
        switch (position){
            case 1:
                c = InfoItem1Activity.class;
                break;
            case 2:
                c = InfoItem2Activity.class;
                break;
            case 3:
                c = InfoItem3Activity.class;
                break;
            case 4:
                c = InfoItem4Activity.class;
                break;
            case 5:
                c = InfoItem5Activity.class;
                break;
        }
        if(c != null){
            startActivity(new Intent(this, c));
        }
    }
}
