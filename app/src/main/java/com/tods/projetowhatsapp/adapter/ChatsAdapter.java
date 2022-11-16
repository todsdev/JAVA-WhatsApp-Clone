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
import com.tods.projetowhatsapp.model.Chat;
import com.tods.projetowhatsapp.model.Group;
import com.tods.projetowhatsapp.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyViewHolder> {
    private List<Chat> chats;
    private Context context;

    public ChatsAdapter(List<Chat> chatList, Context c) {
        this.chats = chatList;
        this.context = c;
    }

    public List<Chat> recoverChats(){
        return this.chats;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_recyclerunit, parent, false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.lastMessage.setText(chat.getLastMessage());
        if (chat.getIsGroup().equals("true")){
            Group group = chat.getGroup();
            holder.name.setText(group.getName());
            if (group.getPhoto() != null) {
                Uri uri = Uri.parse(group.getPhoto());
                Glide.with(context).load(uri).timeout(6000).into(holder.photo);
            } else {
                holder.photo.setImageResource(R.drawable.standard);
            }
        } else {
            User user = chat.getUserExhibition();
            if (user != null){
                holder.name.setText(user.getName());

                if (user.getPhoto() != null){
                    Uri uri = Uri.parse(user.getPhoto());
                    Glide.with(context).load(uri).timeout(6000).into(holder.photo);
                } else {
                    holder.photo.setImageResource(R.drawable.standard);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView photo;
        TextView name, lastMessage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.imageContacts);
            name = itemView.findViewById(R.id.editTitle);
            lastMessage = itemView.findViewById(R.id.editSubtitle);
        }
    }
}
