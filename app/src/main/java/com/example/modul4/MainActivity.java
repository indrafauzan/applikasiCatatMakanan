package com.example.modul4;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fb_add;
    private Button button_load;
    CardView loading_bar;

    private static final String TAG = "MyActivity";

    RecyclerView rv_menu;
    MenuAdapter adapter;
    ArrayList<MenuData2> menuArrayList;

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        //initialize
        fb_add = findViewById(R.id.fb_add);
        button_load = findViewById(R.id.button_load);
        rv_menu = findViewById(R.id.rv_menu);
        loading_bar = findViewById(R.id.loading_bar);
        firebaseFirestore = FirebaseFirestore.getInstance();

        //onclick
        fb_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InputMenuActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        button_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataAsycktask show = new showDataAsycktask();
                show.execute();
            }
        });

        //check user login
        checkUserLogin();






    }
    public void checkUserLogin(){
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
            finish();
        }
    }

    public void do_logout(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Ketuk Ya untuk logout")
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        loading_bar.setVisibility(View. VISIBLE);
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        loading_bar.setVisibility(View.GONE);
                        MainActivity.this.startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    public class showDataAsycktask extends AsyncTask<Void, Void, Void>{

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference menu = rootRef.child("menu");

        ArrayList<MenuData2> menuArray;
        String getnama, getharga, getdeskripsi, getimgUrl;

        @Override
        protected Void doInBackground(Void... voids) {
            menuArray = new ArrayList<>();
            firebaseFirestore.collection("menu")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                loading_bar.setVisibility(View.VISIBLE);
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    getnama = document.getData().get("name").toString();
                                    getharga = document.getData().get("harga").toString();
                                    getdeskripsi = document.getData().get("deskripsi").toString();
                                    getimgUrl = document.getData().get("imgUrl").toString();

                                    Log.i(TAG, "nama " + getnama + " harga " + getharga + " deskripsi " + getdeskripsi + " imgUrl " + getimgUrl);
                                    menuArray.add(new MenuData2(getnama, getharga, getimgUrl, getdeskripsi));

                                    adapter = new MenuAdapter(MainActivity.this, menuArray);
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                                    rv_menu.setLayoutManager(layoutManager);
                                    rv_menu.setAdapter(adapter);
                                    loading_bar.setVisibility(View.GONE);

                                }
                            } else {

                            }
                        }
                    });

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading_bar.setVisibility(View.VISIBLE);
        }




    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                    do_logout();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().getCurrentUser();
        showDataAsycktask show = new showDataAsycktask();
        show.execute();
    }


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().getCurrentUser();
    }

}
