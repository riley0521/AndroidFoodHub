package com.example.riley.androidfoodhub;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.riley.androidfoodhub.Common.Common;
import com.example.riley.androidfoodhub.Model.User;
import com.facebook.FacebookSdk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    Button btnSignIn;
    TextView txtSlogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        printKeyHash();

        btnSignIn = (FButton)findViewById(R.id.btnSignIn);

        txtSlogan = (TextView)findViewById(R.id.txtSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/Nabila.otf");
        txtSlogan.setTypeface(face);

        Paper.init(this);



        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIn = new Intent(MainActivity.this,Login.class);
                startActivity(signIn);
            }
        });


        String user = Paper.book().read(Common.USER_KEY);
        String psw = Paper.book().read(Common.PS_KEY);
        if(user != null && psw != null)
        {
            if(!user.isEmpty() && !psw.isEmpty()) {
                login(user,psw);
            }
        }





    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.riley.androidfoodhub",
                    PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login(final String phone, final String psw) {
        if(Common.isConnectedToInternet(getBaseContext())) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference table_user = database.getReference("User");

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please wait...");
            mDialog.show();
            table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(phone).exists()) {

                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getPass().equals(psw)) {


                            Intent homeIntent = new Intent(MainActivity.this, RestaurantList.class);
                            Common.currentUser = user;
                            startActivity(homeIntent);
                            finish();
                            table_user.removeEventListener(this);
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Invalid phone number or password", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Please do not leave empty space", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
