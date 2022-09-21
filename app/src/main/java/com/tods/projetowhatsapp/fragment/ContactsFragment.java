package com.tods.projetowhatsapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tods.projetowhatsapp.R;
import com.tods.projetowhatsapp.activity.ChatActivity;
import com.tods.projetowhatsapp.activity.GroupActivity;
import com.tods.projetowhatsapp.adapter.ContactsAdapter;
import com.tods.projetowhatsapp.config.FirebaseSettings;
import com.tods.projetowhatsapp.helper.RecyclerItemClickListener;
import com.tods.projetowhatsapp.model.User;
import com.tods.projetowhatsapp.model.UserFirebase;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    private RecyclerView recyclerContacts;
    private ContactsAdapter adapter;
    private List<User> listContacts = new ArrayList<>();
    private DatabaseReference userRef;
    private ValueEventListener velContacts;
    private FirebaseUser actualUser;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public ContactsFragment() {
    }

    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
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
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        //CONFIGURAÇÕES INICIAIS
        recyclerContacts = view.findViewById(R.id.recyclerContacts);
        userRef = FirebaseSettings.getFirebaseDatabase().child("user");
        actualUser = UserFirebase.getCurrentUser();

        //CONFIGURAR ADAPTER
        adapter = new ContactsAdapter(listContacts, getActivity());

        //CONFIGURAR RECYCLER VIEW
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerContacts.setLayoutManager(layoutManager);
        recyclerContacts.setHasFixedSize(true);
        recyclerContacts.setAdapter(adapter);

        //CONFIGURAR EVENTO DE CLIQUE NO RECYCLERVIEW
        recyclerContacts.addOnItemTouchListener
                (new RecyclerItemClickListener(getActivity(), recyclerContacts, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        List<User> updatedListContacts = adapter.recoverContacts();
                        User userSelected = updatedListContacts.get(position);
                        boolean header = userSelected.getEmail().isEmpty();
                        if (header){
                            startActivity(new Intent(getActivity(), GroupActivity.class));
                        } else {
                            Intent i = new Intent(getActivity(), ChatActivity.class);
                            i.putExtra("chatContacts", userSelected);
                            startActivity(i);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));
        addMenuNewGroup();
        return view;
    }

    public void recoverContacts(){
        velContacts = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearContactList();
                for (DataSnapshot data: snapshot.getChildren()){

                    User user = data.getValue(User.class);

                    String actualUserEmail = actualUser.getEmail();
                    if (!actualUserEmail.equals(user.getEmail())){
                        listContacts.add(user);

                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void clearContactList(){
        listContacts.clear();
        addMenuNewGroup();
    }

    public void addMenuNewGroup(){
        //USANDO USUÁRIO MODIFICADO (SEM EMAIL) PARA DEFINIR CRIAÇÃO DE GRUPO
        User itemGroup = new User();
        itemGroup.setName("Novo grupo");
        itemGroup.setEmail("");
        listContacts.add(itemGroup);
    }

    @Override
    public void onStart() {
        super.onStart();
        recoverContacts();
    }

    @Override
    public void onStop() {
        super.onStop();
        userRef.removeEventListener(velContacts);
    }

    public void searchContacts(String text){
        List<User> searchListContacts = new ArrayList<>();
        for (User user: listContacts){
            String name = user.getName().toLowerCase();
            if (name.contains(text)){
                searchListContacts.add(user);
            }
        }
        adapter = new ContactsAdapter(searchListContacts, getActivity());
        recyclerContacts.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void reloadContacts(){
        adapter = new ContactsAdapter(listContacts, getActivity());
        recyclerContacts.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}