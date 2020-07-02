package com.andrushka.studentattendance.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andrushka.studentattendance.R;
import com.andrushka.studentattendance.model.ProfileInformation;
import com.andrushka.studentattendance.model.UserInformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;



public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<ProfileInformation> profileInfo;


    public RecyclerViewAdapter(Context context, List<ProfileInformation> profileInfoList) {
        this.context = context;
        this.profileInfo = profileInfoList;
    }

    @NonNull
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_info, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder viewHolder, int position) {
        viewHolder.title.setText(profileInfo.get(position).getTitle());
        viewHolder.content.setText(profileInfo.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return profileInfo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.title_info);
            content = itemView.findViewById(R.id.content_info);
        }
    }
}
