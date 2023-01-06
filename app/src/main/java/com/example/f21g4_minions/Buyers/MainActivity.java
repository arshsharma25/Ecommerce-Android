package com.example.f21g4_minions.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.f21g4_minions.Model.Users;
import com.example.f21g4_minions.Prevalent.Prevalent;
import com.example.f21g4_minions.R;
import com.example.f21g4_minions.Sellers.SellerHomeActivity;
import com.example.f21g4_minions.Sellers.SellerRegistrationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton, LoginButton;
    private ProgressDialog loadingBar;
    private TextView sellerBegin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowButton = findViewById(R.id.main_join_now_btn);
        LoginButton = findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);

        sellerBegin = findViewById(R.id.seller_begin);


        Paper.init(MainActivity.this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Login_Activity.class);
                startActivity(intent);
            }
        });

        sellerBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SellerRegistrationActivity.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if(UserPhoneKey!="" && UserPasswordKey!=""){
            if(!TextUtils.isEmpty(UserPhoneKey)&&!TextUtils.isEmpty(UserPasswordKey)){
                AllowAccess(UserPhoneKey,UserPasswordKey);

                loadingBar.setTitle("Already Logged in Account");
                loadingBar.setMessage("Good to see you back ^_^");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            Intent intent = new Intent(MainActivity.this, SellerHomeActivity. class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void AllowAccess(final String phone, final String password) {

        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference();

        //Now see if user is available or not
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("User").child(phone).exists()){
                    //We are getting the data and we will pass the data to the users class
                    Users usersData = snapshot.child("User").child(phone).getValue(Users.class);

                    //Here we are checking if the input data is equals to the database data
                    if(usersData.getPhone().equals(phone)){
                        if(usersData.getPassword().equals(password)){

                            Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));

                        }else{
                            Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Account with this "+phone+" do not exist", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "You have to create new account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}