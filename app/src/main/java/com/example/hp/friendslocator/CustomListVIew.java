package com.example.hp.friendslocator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListVIew extends ArrayAdapter<String> implements Filterable {

    private String[] username;
    private Bitmap[] bitmaps;
    private Activity context;


    public CustomListVIew( Activity context, String[] username, Bitmap[] bitmaps) {
        super(context, R.layout.activity_list, username);
        this.username = username;
        this.context = context;
        this.bitmaps = bitmaps;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder;
        if(r==null)
        {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.listview_layout, null,true );
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }

        else {
            viewHolder = (ViewHolder) r.getTag();
        }
        viewHolder.name.setText(username[position]);
        viewHolder.imageView.setImageBitmap(bitmaps[position]);

        return r;


    }

    class ViewHolder
    {
        ImageView imageView;
        TextView name;
        ViewHolder(View v)
        {
            name = (TextView) v.findViewById(R.id.usernameList);
            imageView = v.findViewById(R.id.imageViewList);
        }
    }


}
