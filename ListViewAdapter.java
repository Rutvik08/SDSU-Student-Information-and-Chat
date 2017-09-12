package com.example.rutvik.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ListViewAdapter extends ArrayAdapter<User>{

    private ArrayList<User> dataSet;
    Context context;
    private int lastPosition = -1;
    private static class ViewContainer {
    TextView txtNName,txtYear,txtCountry,txtState,txtCity;    
    }

    public ListViewAdapter(ArrayList<User> data, Context context) {
        super(context, R.layout.custom_listview, data);
        this.dataSet = data;
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        User User = getItem(position);
        ViewContainer viewContainer;
        final View result;
        if (convertView == null) {
            viewContainer = new ViewContainer();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_listview, parent, false);
            viewContainer.txtNName = (TextView) convertView.findViewById(R.id.nickNameL);
            viewContainer.txtYear = (TextView) convertView.findViewById(R.id.yearL);
            viewContainer.txtCountry = (TextView) convertView.findViewById(R.id.countryL);
            viewContainer.txtState = (TextView) convertView.findViewById(R.id.stateL);
            viewContainer.txtCity = (TextView) convertView.findViewById(R.id.cityL);
            result=convertView;
            convertView.setTag(viewContainer);
        } else {
            viewContainer = (ViewContainer) convertView.getTag();
            result=convertView;
        }
        lastPosition = position;
        viewContainer.txtNName.setText(User.getNickname());
        viewContainer.txtYear.setText(String.valueOf(User.getYear()));
        viewContainer.txtCountry.setText(User.getCountry());
        viewContainer.txtState.setText(User.getState());
        viewContainer.txtCity.setText(User.getCity());
        return convertView;
    }
}
