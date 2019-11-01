package com.example.riley.androidfoodhub;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.riley.androidfoodhub.Common.Common;
import com.example.riley.androidfoodhub.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import org.w3c.dom.Text;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Login extends AppCompatActivity implements Animation.AnimationListener {
    MaterialEditText edtPhone, edtPass;
    Button btnSignIn,btnSignUp;
    ImageView logoView;
//    CheckBox ckbRemember;
    TextView txtForgotPs;

    FirebaseDatabase database;
    DatabaseReference table_user;

    private Boolean ANIMATION_ENDED = false;
    private Boolean START_ANIMATION = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPhone = (MaterialEditText)findViewById(R.id.edtPhone);
        edtPass = (MaterialEditText)findViewById(R.id.edtPass);
        txtForgotPs = (TextView) findViewById(R.id.txtForgotPs);
        logoView = findViewById(R.id.logoView);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);

        loadAnimation();



        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        txtForgotPs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPsDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Common.isConnectedToInternet(getBaseContext())) {

                    final ProgressDialog mDialog = new ProgressDialog(Login.this);
                    mDialog.setMessage("Please wait...");
                    mDialog.show();
                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                    User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                    user.setPhone(edtPhone.getText().toString());

                                    try {

                                        if (user.getPass().equals(edtPass.getText().toString())) {


                                            Intent homeIntent = new Intent(Login.this, Home.class);
                                            Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                                            Paper.book().write(Common.PS_KEY, edtPass.getText().toString());
                                            Common.currentUser = user;
                                            startActivity(homeIntent);
                                            finish();
                                            table_user.removeEventListener(this);
                                        } else if (edtPhone.getText().toString().isEmpty() || edtPass.getText().toString().isEmpty()) {
                                            mDialog.dismiss();
                                            Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                                        } else {
                                            mDialog.dismiss();
                                            Toast.makeText(Login.this, "Invalid phone number or password", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    catch (Exception e) {
                                        mDialog.dismiss();
                                        Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    mDialog.dismiss();
                                    Toast.makeText(Login.this, "Invalid phone number or password", Toast.LENGTH_SHORT).show();
                                }
                            }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Toast.makeText(Login.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,SignUp.class));
                finish();
            }
        });
    }

    private void loadAnimation() {
        edtPhone.setVisibility(View.GONE);
        edtPass.setVisibility(View.GONE);
        txtForgotPs.setVisibility(View.GONE);
        btnSignIn.setVisibility(View.GONE);
        btnSignUp.setVisibility(View.GONE);

        Animation moveFBLogoAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_animation);
        moveFBLogoAnimation.setFillAfter(true);
        moveFBLogoAnimation.setAnimationListener(this);
        logoView.startAnimation(moveFBLogoAnimation);
    }

    private void showForgotPsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your security code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_password_layout,null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhone = (MaterialEditText)forgot_view.findViewById(R.id.edtPhone);
        final MaterialEditText edtSecureCode = (MaterialEditText)forgot_view.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                        if(user.getSecureCode().equals(edtSecureCode.getText().toString()))
                            Toast.makeText(Login.this, "Your password is: "+user.getPass(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(Login.this, "Wrong security code", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        edtPhone.setAlpha(0f);
        edtPhone.setVisibility(View.VISIBLE);
        edtPass.setAlpha(0f);
        edtPass.setVisibility(View.VISIBLE);
        txtForgotPs.setAlpha(0f);
        txtForgotPs.setVisibility(View.VISIBLE);
        btnSignIn.setAlpha(0f);
        btnSignIn.setVisibility(View.VISIBLE);
        btnSignUp.setAlpha(0f);
        btnSignUp.setVisibility(View.VISIBLE);

        int mediumAnimationTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        edtPhone.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);

        edtPass.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);

        txtForgotPs.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);

        btnSignIn.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);

        btnSignUp.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
