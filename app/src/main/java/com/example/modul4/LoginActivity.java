package com.example.modul4;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private TextView button_register;
    private EditText email, password;
    private Button button_login;
    CardView loading_bar;

    //deklarasi Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize xml component
        button_register = findViewById(R.id.button_register);
        button_login = findViewById(R.id.button_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loading_bar = findViewById(R.id.loading_bar);


        //setOnClickListener
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                do_register();
            }
        });
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                do_login();
            }
        });


    }
    public void do_register(){
        Intent intent = new Intent(this, RegisterActivity.class);
        this.startActivity(intent);
    }
    public void do_login(){
        loading_bar.setVisibility(View.VISIBLE);
        String emails = email.getText().toString();
        String passwords = password.getText().toString();

        firebaseAuth = FirebaseAuth.getInstance();

        if(!emails.equals("") && !passwords.equals("")) {

            firebaseAuth.signInWithEmailAndPassword(emails, passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        loading_bar.setVisibility(View.GONE);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getApplicationContext().startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }

}
