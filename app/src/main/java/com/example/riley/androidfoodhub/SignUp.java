package com.example.riley.androidfoodhub;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.riley.androidfoodhub.Common.Common;
import com.example.riley.androidfoodhub.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUp extends AppCompatActivity {
    MaterialEditText edtName, edtPhone, edtPass, edtSecureCode;
    FButton btnSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName = (MaterialEditText)findViewById(R.id.edtName);
        edtPhone = (MaterialEditText)findViewById(R.id.edtPhone);
        edtPass = (MaterialEditText)findViewById(R.id.edtPass);
        edtSecureCode = (MaterialEditText)findViewById(R.id.edtSecureCode);


        btnSignUp = (FButton)findViewById(R.id.btnSignUp);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                if(Common.isConnectedToInternet(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("Please wait...");
                    mDialog.show();
                    final int length1, length2;
                    length1 = edtPhone.getText().length();
                    length2 = edtPass.getText().length();
                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                if(edtPhone.getText().length() == 11 && edtPass.getText().length() == 8 && edtSecureCode.getText().length() == 4 && !edtName.getText().toString().isEmpty()) {
                                    User user = new User(edtName.getText().toString(), edtPass.getText().toString(), edtSecureCode.getText().toString());
                                    table_user.child(edtPhone.getText().toString()).setValue(user);
                                    Toast.makeText(SignUp.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                    table_user.removeEventListener(this);
                                    Intent login = new Intent(SignUp.this, Login.class);
                                    startActivity(login);
                                }
                                else {
                                    mDialog.dismiss();
                                    Toast.makeText(SignUp.this, "Requirements not met", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else if(edtPhone.getText().toString().isEmpty()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "Please fill the form", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "Phone number is already existing", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Toast.makeText(SignUp.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
//