package com.tods.projetowhatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tods.projetowhatsapp.R;
import com.tods.projetowhatsapp.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedGroupAdapter extends RecyclerView.Adapter<SelectedGroupAdapter.MyViewHolder> {
    private List<User> contacts;
    private Context context;

    public SelectedGroupAdapter(List<User> listContacts, Context c) {
        this.contacts = listContacts;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from
                (parent.getContext()).inflate(R.layout.adapter_selected_group, parent, false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = contacts.get(position);
        holder.name.setText(user.getName());
        if (user.getPhoto() != null){
            Uri uri = Uri.parse(user.getPhoto());
            Glide.with(context).load(uri).timeout(6000).into(holder.photo);
        } else {
            holder.photo.setImageResource(R.drawable.standard);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView photo;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.imageViewSelectedMember);
            name = itemView.findViewById(R.id.editSelectedMemberName);
        }
    }
}
