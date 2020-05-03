package com.example.instgramapp.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instgramapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    Button login;
    TextView txt_sighup;
    ProgressDialog pd;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.edt_email);
        password=findViewById(R.id.edt_password);
        login=findViewById(R.id.btn_login);
        txt_sighup=findViewById(R.id.txt_Signup);

        mAuth=FirebaseAuth.getInstance();
        txt_sighup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd=new ProgressDialog(LoginActivity.this);
                pd.setMessage("Please Wait..");
                pd.show();

                String st_email=email.getText().toString();
                String st_password=password.getText().toString();
                if (st_email.isEmpty()||st_password.isEmpty())
                {
                    Toast.makeText(LoginActivity.this,"Please Enter all Data ",Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }else if (st_password.length()<6){
                    Toast.makeText(LoginActivity.this,"Password must be more than 6 charcter",Toast.LENGTH_SHORT)
                            .show();
                    pd.dismiss();}
                else {Login(st_email,st_password);}

            }
        });


    }

    void Login(String email,String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                           // FirebaseUser user = mAuth.getCurrentUser();

                            DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(mAuth.getCurrentUser().getUid());
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    pd.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    pd.dismiss();

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            pd.dismiss();

                        }

                        // ...
                    }
                });

        //----------------

    }


}
