package com.example.instgramapp.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.instgramapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity {


    @BindView(R.id.btn_login)
    Button Login;
    @BindView(R.id.btn_Register)
    Button Register;

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);


    }


    @OnClick({R.id.btn_login, R.id.btn_Register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                //  pd = new ProgressDialog(LoginActivity.this);
                //pd.setMessage("Please Wait..");
                // pd.show();
                startActivity(new Intent(StartActivity.this, LoginActivity.class));

                break;
            case R.id.btn_Register:
                //  pd = new ProgressDialog(LoginActivity.this);
                //pd.setMessage("Please Wait..");
                // pd.show();
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
                break;
        }
    };
}
