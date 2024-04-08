package com.example.gidevents;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

/**
 * The Administrator browse user profile page activity
 * Displays all Users in Firebase
 */
public class AdminBrowseProfilesActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference userRef;
    private ListView listView;
    private ArrayList<User> userList = new ArrayList<>();
    private int eventCount;

    /**
     * onCreate method for the class
     * Inflates the listview, and set it onClickListener
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_profiles_page);
        listView = findViewById(R.id.browse_profiles_listview);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users");

        UserProfilesAdapter adapter = new UserProfilesAdapter(this, userList);
        listView.setAdapter(adapter);
        Button backBtn = (Button) findViewById(R.id.back_button);
        backBtn.setOnClickListener(v -> {
            finish();
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AdminBrowseProfilesActivity.this, UserProfileDetailsPage.class);
                intent.putExtra("userDetails", userList.get(position));
                startActivity(intent);
            }
        });

        userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    userList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String userID = doc.getId();
                        String username = doc.getString("Username");
                        String address = doc.getString("Address");
                        String email = doc.getString("Email");
                        String gender = doc.getString("Gender");
                        String birthday= doc.getString("Birthday");
                        String name= doc.getString("Name");
                        String phone = doc.getString("Phone");
                        Log.d("Firestore", String.format("User(%s) fetched", userID));
                        userList.add(new User(name, username, address,birthday, email, gender, userID, phone));
                    }
                    adapter.notifyDataSetChanged();

                }
            }
        });
        //Set up a filter for the search bar
        SearchView searchBar = findViewById(R.id.search_bar);
        adapter.getFilter().filter("");
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

    }
}
