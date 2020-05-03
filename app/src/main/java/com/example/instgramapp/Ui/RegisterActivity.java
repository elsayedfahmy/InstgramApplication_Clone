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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username ,fullname,email,password;
    Button register;
    TextView txt_login;

FirebaseAuth mAuth;
DatabaseReference reference;
ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username=findViewById(R.id.edt_username);
        fullname=findViewById(R.id.edt_fullname);
        email=findViewById(R.id.edt_email);
        password=findViewById(R.id.edt_password);
        register=findViewById(R.id.btn_Register);
        txt_login=findViewById(R.id.txtlogin);

        mAuth=FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd=new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please Wait..");
                pd.show();

                String st_username=username.getText().toString();
                String st_fullname=fullname.getText().toString();
                String st_email=email.getText().toString();
                String st_password=password.getText().toString();
                if (st_username.isEmpty()||st_fullname.isEmpty()||st_email.isEmpty()||st_password.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this,"Please Enter all Data",Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }else if (st_password.length()<6){
                    Toast.makeText(RegisterActivity.this,"Password must be more than 6 charcter",Toast.LENGTH_SHORT).show();
                pd.dismiss();
                }
                else {
                    Register(st_username,st_fullname,st_email,st_password);}

            }
        });



    }

    private  void Register(final String username, final String fullname, final String email, final String password)
    {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String uid=currentUser.getUid();
                            reference= FirebaseDatabase.getInstance().getReference("Users").child(uid);
                            HashMap<String,String>hashMap=new HashMap<String, String>();
                            hashMap.put("Id",uid);
                            hashMap.put("username",username.toLowerCase());
                            hashMap.put("fullname",fullname);
                            hashMap.put("bio","");
                            hashMap.put("ImageUrl","https://firebasestorage.googleapis.com/v0/b/instagramapp-bfdb9.appspot.com/o/usernameHolder.jpg?alt=media&token=767054d3-b4a3-44be-874a-6dfefce07099");
                             reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);


                                    }
                                }
                            });

                        } else {
                            pd.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "you cant Register with this Email And password", Toast.LENGTH_LONG).show();

                        }

                        // ...
                    }
                });

        //--------------------------

    }

}
