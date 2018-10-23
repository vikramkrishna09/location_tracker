package com.example.vikra.location;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.R.attr.name;

/**
 * Created by vikra on 11/10/2017.
 */

public class CustomAdapter extends ArrayAdapter<LocationDB.CheckIn> {


    private List<LocationDB.CheckIn> data;
    private LayoutInflater inflater = null;
    private int resource;
    public CustomAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<LocationDB.CheckIn> data) {
        super(context, resource, data);
        this.data = data;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public LocationDB.CheckIn getItem(int position) {

        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private class Viewholder {
        TextView name;
        TextView address;
        TextView latandlong;
        TextView time;
    }

    @Override
    public View getView(int i, View V, ViewGroup Parent) {
        //keep this line
        final int vee = i;
       TextView name;
        TextView address;
        TextView time;
        TextView latandlong;

        CheckBox check;
        Viewholder holder;
        if (V == null) {
            V = inflater.inflate(resource, null);


            name = (TextView) V.findViewById(R.id.customname);
            address = (TextView) V.findViewById(R.id.address);

             latandlong = (TextView) V.findViewById(R.id.latlng);
           time = (TextView) V.findViewById(R.id.time);
            holder = new Viewholder();

            holder.name = name;
            holder.address = address;
            holder.latandlong = latandlong;
            holder.time = time;
            V.setTag(holder);

        } else {
            holder = (Viewholder) V.getTag();
            //check = holder.cb;
           name = holder.name;
            latandlong = holder.latandlong;
            time = holder.time;
            address = holder.address;
        }

       // check.setTag(data1.get(i));
       // check.setChecked(false);
        name.setText(data.get(i).returnName());
       double lng = data.get(i).returnLng();
      double  lat = data.get(i).returnLat();
       latandlong.setText("Longtitude: " + String.format("%.6f",lng) + " Latitude: " + String.format("%.6f",lat));
        address.setText(data.get(i).returnAddress());
       Date date = new Date(data.get(i).returnepochtime());
        time.setText(date.toString());




        return V;
    }
}
