package com.tods.projetowhatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.tods.projetowhatsapp.R;
import com.tods.projetowhatsapp.model.Messages;
import com.tods.projetowhatsapp.model.UserFirebase;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder>{
    private List<Messages> messages;
    private Context context;
    private static final int TYPE_SENDER = 0;
    private static final int TYPE_RECEIVER = 1;

    public MessagesAdapter(List<Messages> list, Context c) {
        this.messages = list;
        this.context = c;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = null;
        if (viewType == TYPE_SENDER){
            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_message_sender, parent, false);
        } else if (viewType == TYPE_RECEIVER) {
            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_message_receiver, parent, false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Messages message = messages.get(position);
        String msg = message.getMessage();
        String img = message.getImage();
        if (img != null){
            holder.image.setVisibility(View.VISIBLE);
            Uri url = Uri.parse(img);
            Glide.with(context).load(url).timeout(6000).into(holder.image);
            String name = message.getName();
            if (!name.isEmpty()){
                holder.name.setText(name);
            } else {
                holder.name.setVisibility(View.GONE);
            }
            //ESCONDER TEXTO
            holder.message.setVisibility(View.GONE);
        } else {
            holder.message.setVisibility(View.VISIBLE);
            String name = message.getName();
            if (!name.isEmpty()){
                holder.name.setText(name);
            } else {
                holder.name.setVisibility(View.GONE);
            }
            holder.message.setText(msg);
            //ESCONDER A IMAGEM
            holder.image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages message = messages.get(position);
        String idUser = UserFirebase.recoverUserId();
        if (idUser.equals(message.getIdUser())){
            return TYPE_SENDER;
        }
        return TYPE_RECEIVER;
    }

    //INNER CLASS
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        ImageView image;
        TextView name;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                message = itemView.findViewById(R.id.textMessageChat);
                image = itemView.findViewById(R.id.imagePhotoChat);
                name = itemView.findViewById(R.id.textUserNameExhibition);
            }
        }

    }
