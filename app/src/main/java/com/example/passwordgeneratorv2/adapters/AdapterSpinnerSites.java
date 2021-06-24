package com.example.passwordgeneratorv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.models.Password;
import com.example.passwordgeneratorv2.models.WebsiteModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterSpinnerSites extends ArrayAdapter<WebsiteModel> {

    public AdapterSpinnerSites(@NonNull Context context, ArrayList<WebsiteModel> sites) {
        super(context, 0, sites);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return initView(position, convertView, parent);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_sites_layout, parent, false);
        }

        ImageView imgSiteIcon = convertView.findViewById(R.id.img_site_icon);
        TextView txtSiteName = convertView.findViewById(R.id.txt_site_name);

        WebsiteModel currentPassword = getItem(position);

        if (currentPassword != null) {
            Glide.with(convertView)
                    .load(currentPassword.getIconLink())
                    .placeholder(R.drawable.default_image)
                    .into(imgSiteIcon);
            txtSiteName.setText(currentPassword.getName());
        }


        return convertView;
    }

    ;
}
