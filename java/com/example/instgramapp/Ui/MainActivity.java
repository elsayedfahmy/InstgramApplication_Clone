package com.example.instgramapp.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.example.instgramapp.R;
import com.example.instgramapp.fragment.HomeFragment;
import com.example.instgramapp.fragment.NotifagationFragment;
import com.example.instgramapp.fragment.ProfileFragment;
import com.example.instgramapp.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
Button btn_Signout;

    BottomNavigationView bottomNavigationView;
    Fragment selectfragment=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=findViewById(R.id.bottom_navigationview);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent=getIntent().getExtras();
        if (intent!=null){
            String profileid=intent.getString("publisherid");
            SharedPreferences.Editor editor =getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("publisherid",profileid);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }





      /* btn_Signout=findViewById(R.id.btn_signout);
        btn_Signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog  pd=new ProgressDialog(MainActivity.this);
                pd.setMessage("Please Wait..");
                pd.show();
                Toast.makeText(MainActivity.this,"signOut...",Toast.LENGTH_SHORT).show();
                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                mAuth.signOut();
                pd.dismiss();
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);



            }
        });
        */

    }

  private   BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                           selectfragment=new HomeFragment();
                            break;
                        case R.id.nav_search:
                            selectfragment=new SearchFragment();
                            break;
                        case R.id.nav_heart:
                            selectfragment=new NotifagationFragment();
                            break;
                        case R.id.nav_profile:
                            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                            editor.putString("profile_Id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectfragment=new ProfileFragment();
                            break;
                        case R.id.nav_add:
                           selectfragment=null;
                           startActivity(new Intent(MainActivity.this,PostActivity.class));
                            break;
                    }
                    if (selectfragment!=null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectfragment).commit();
                    }

                    return true;
                }
            };

}
