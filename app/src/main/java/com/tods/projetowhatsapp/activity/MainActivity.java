package com.tods.projetowhatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.tods.projetowhatsapp.R;
import com.tods.projetowhatsapp.config.FirebaseSettings;
import com.tods.projetowhatsapp.fragment.ChatsFragment;
import com.tods.projetowhatsapp.fragment.ContactsFragment;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private MaterialSearchView searchView;
    private FragmentPagerItemAdapter adapterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configToolbar();
        configInitialSettings();
        configTabs();
    }

    private void configInitialSettings() {
        auth = FirebaseSettings.getFirebaseAuth();
        searchView = findViewById(R.id.search_view);
    }

    private void configToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsApp");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    private void configTabs() {
        adapterFragment = configAdapterFragment();
        final ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapterFragment);
        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);
        configSearchListener(adapterFragment);
        configSearch(adapterFragment, viewPager);
    }

    @NonNull
    private FragmentPagerItemAdapter configAdapterFragment() {
        return new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems
                        .with(this)
                        .add("Chats", ChatsFragment.class)
                        .add("Contacts", ContactsFragment.class)
                        .create());
    }

    private void configSearchListener(FragmentPagerItemAdapter adapter) {
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                ChatsFragment fragment = (ChatsFragment) adapter.getPage(0);
                fragment.reloadChats();
            }
        });
    }

    private void configSearch(FragmentPagerItemAdapter adapter, ViewPager viewPager) {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                switch (viewPager.getCurrentItem()){
                    case 0:
                        ChatsFragment chatsFragment = (ChatsFragment) adapter.getPage(0);
                        if (newText != null && !newText.isEmpty()){
                            chatsFragment.searchChats(newText.toLowerCase());
                        } else {
                            chatsFragment.reloadChats();
                        }
                        break;
                    case 1:
                        ContactsFragment contactsFragment = (ContactsFragment) adapter.getPage(1);
                        if (newText != null && !newText.isEmpty()){
                            contactsFragment.searchContacts(newText.toLowerCase());
                        } else {
                            contactsFragment.reloadContacts();
                        }
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogOut:
                logOutUser();
                finish();
                break;
            case R.id.menuProfile:
                openProfile();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logOutUser(){
        try {
            auth.signOut();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void openProfile(){
        startActivity(new Intent(this, ProfileActivity.class));
    }
}