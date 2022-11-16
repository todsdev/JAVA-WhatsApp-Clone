package com.tods.projetowhatsapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.tods.projetowhatsapp.R;
import com.tods.projetowhatsapp.activity.ChatActivity;
import com.tods.projetowhatsapp.adapter.ChatsAdapter;
import com.tods.projetowhatsapp.config.FirebaseSettings;
import com.tods.projetowhatsapp.helper.RecyclerItemClickListener;
import com.tods.projetowhatsapp.model.Chat;
import com.tods.projetowhatsapp.model.Messages;
import com.tods.projetowhatsapp.model.User;
import com.tods.projetowhatsapp.model.UserFirebase;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private List<Chat> chatList = new ArrayList<>();
    private RecyclerView recyclerChats;
    private ChatsAdapter adapter;
    private DatabaseReference database;
    private DatabaseReference chatRef;
    private ChildEventListener celChats;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public ChatsFragment() {

    }

    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        configRecyclerView(view);
        configClickEvent();
        return configChatRef(view);
    }

    private void configRecyclerView(View view) {
        recyclerChats = view.findViewById(R.id.recyclerChatList);
        adapter = new ChatsAdapter(chatList, getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerChats.setLayoutManager(layoutManager);
        recyclerChats.setHasFixedSize(true);
        recyclerChats.setAdapter(adapter);
    }

    private View configChatRef(View view) {
        String idUser = UserFirebase.recoverUserId();
        database = FirebaseSettings.getFirebaseDatabase();
        chatRef = database.child("chats").child(idUser);
        return view;
    }

    private void configClickEvent() {
        recyclerChats.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(), recyclerChats, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Chat> updatedChatList = adapter.recoverChats();
                Chat selectedChat = updatedChatList.get(position);
                if (selectedChat.getIsGroup().equals("true")){
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatGroup", selectedChat.getGroup());
                    startActivity(i);
                } else {
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatContacts", selectedChat.getUserExhibition());
                    startActivity(i);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        }
        ));
    }

    public void recoverChats(){
        chatList.clear();
         celChats = chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Chat chat = snapshot.getValue(Chat.class);
                chatList.add(chat);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recoverChats();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatRef.removeEventListener(celChats);
    }

    public void searchChats(String text){
        List<Chat> searchListChat = new ArrayList<>();
        for (Chat chat: chatList){
            if (chat.getUserExhibition() != null){
                String textName = chat.getUserExhibition().getName().toLowerCase();
                String textLastMessage = chat.getLastMessage().toLowerCase();
                if (textName.contains(text) || textLastMessage.contains(text)){
                    searchListChat.add(chat);
                }
            } else {
                String textName = chat.getGroup().getName().toLowerCase();
                String textLastMessage = chat.getLastMessage().toLowerCase();
                if (textName.contains(text) || textLastMessage.contains(text)) {
                    searchListChat.add(chat);
                }
            }
        }
        adapter = new ChatsAdapter(searchListChat, getActivity());
        recyclerChats.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void reloadChats(){
        adapter = new ChatsAdapter(chatList, getActivity());
        recyclerChats.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}