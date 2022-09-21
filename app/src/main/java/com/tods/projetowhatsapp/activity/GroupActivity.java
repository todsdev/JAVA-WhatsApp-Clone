package com.tods.projetowhatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tods.projetowhatsapp.R;
import com.tods.projetowhatsapp.adapter.ContactsAdapter;
import com.tods.projetowhatsapp.adapter.SelectedGroupAdapter;
import com.tods.projetowhatsapp.config.FirebaseSettings;
import com.tods.projetowhatsapp.helper.RecyclerItemClickListener;
import com.tods.projetowhatsapp.model.User;
import com.tods.projetowhatsapp.model.UserFirebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private RecyclerView recyclerMembers, recyclerSelectedMember;
    private ContactsAdapter adapter;
    private SelectedGroupAdapter adapter_selected;
    private List<User> listMembers = new ArrayList<>();
    private List<User> listSelectedMembers = new ArrayList<>();
    private ValueEventListener velMembers;
    private DatabaseReference memberRef;
    private FirebaseUser actualUser;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = findViewById(R.id.fab);
        recyclerMembers = findViewById(R.id.recyclerMembers);
        recyclerSelectedMember = findViewById(R.id.recyclerSelectedMembers);

        memberRef = FirebaseSettings.getFirebaseDatabase().child("user");
        actualUser = UserFirebase.getCurrentUser();

        //CONFIGURAR ADAPTER
        adapter = new ContactsAdapter(listMembers, getApplicationContext());

        //CONFIGURAR RECYCLERVIEW
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMembers.setLayoutManager(layoutManager);
        recyclerMembers.setHasFixedSize(true);
        recyclerMembers.setAdapter(adapter);
        recyclerMembers.addOnItemTouchListener
                (new RecyclerItemClickListener(getApplicationContext(), recyclerMembers, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        User selectedUser = listMembers.get(position);

                        //REMOVER USUÁRIO SELECIONADO DA LISTA
                        listMembers.remove(selectedUser);
                        adapter.notifyDataSetChanged();

                        //ADICIONANDO OS MEMBROS SELECIONADOS AO NOVO ARRAY
                        listSelectedMembers.add(selectedUser);
                        adapter_selected.notifyDataSetChanged();

                        updateMembersToolbar();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));

        //CONFIGURAR RECYCLER PARA MEMBROS SELECIONADOS
        adapter_selected = new SelectedGroupAdapter(listSelectedMembers, getApplicationContext());
        RecyclerView.LayoutManager layoutManagerHorizont = new LinearLayoutManager
                (getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerSelectedMember.setLayoutManager(layoutManagerHorizont);
        recyclerSelectedMember.setHasFixedSize(true);
        recyclerSelectedMember.setAdapter(adapter_selected);
        recyclerSelectedMember.addOnItemTouchListener
                (new RecyclerItemClickListener(getApplicationContext(), recyclerSelectedMember, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        User selectedUser = listSelectedMembers.get(position);

                        //REMOVENDO DA LISTAGEM
                        listSelectedMembers.remove(selectedUser);
                        adapter_selected.notifyDataSetChanged();

                        //ADICIONANDO LISTAGEM DE MEMBROS
                        listMembers.add(selectedUser);
                        adapter.notifyDataSetChanged();

                        updateMembersToolbar();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));

        //CLICK DO FLOATING ACTION BUTTON
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GroupActivity.this, RegisterGroupActivity.class);
                i.putExtra("members", (Serializable) listSelectedMembers);
                startActivity(i);
            }
        });
    }

    public void recoverContacts(){
        velMembers = memberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren()){
                    User user = data.getValue(User.class);
                    String actualUserEmail = actualUser.getEmail();
                    if(!actualUserEmail.equals(user.getEmail())) {
                        listMembers.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
                updateMembersToolbar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recoverContacts();
    }

    @Override
    protected void onStop() {
        super.onStop();
        memberRef.removeEventListener(velMembers);
    }

    public void updateMembersToolbar(){
        int totalSelected = listSelectedMembers.size();
        int total = listMembers.size() + listSelectedMembers.size();
        toolbar.setSubtitle(totalSelected + " de " + total + " selecionados");
    }
}