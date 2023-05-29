package com.example.roommateapplication;

import
        androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.roommateapplication.Notifications.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DashboardActivity extends AppCompatActivity {



    private FirebaseAuth firebaseAuth;
    ActionBar actionBar;
    String mUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        actionBar=getSupportActionBar();
        actionBar.setTitle("Profile");


        firebaseAuth=FirebaseAuth.getInstance();

        BottomNavigationView navigationView=findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        actionBar.setTitle("Home");
        NotificationsFragment fragmentHome=new NotificationsFragment();
        FragmentTransaction ftHome= getSupportFragmentManager().beginTransaction();
        ftHome.replace(R.id.content,fragmentHome,"");
        ftHome.commit();

        //updateToken(String.valueOf(FirebaseMessaging.getInstance().getToken()));
    }

    public void updateToken(String token){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken= new Token(token);
        ref.child(mUID).setValue(mToken);
    }

    protected  void onResume(){
        checkUserStatus();
        super.onResume();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            actionBar.setTitle("Notifications");
                            NotificationsFragment fragmentHome=new NotificationsFragment();
                            FragmentTransaction ftHome= getSupportFragmentManager().beginTransaction();
                            ftHome.replace(R.id.content,fragmentHome,"");
                            ftHome.commit();
                            return true;
                        case R.id.nav_profile:
                            actionBar.setTitle("Profile");
                            ProfileFragment fragmentProfile=new ProfileFragment();
                            FragmentTransaction ftProfile= getSupportFragmentManager().beginTransaction();
                            ftProfile.replace(R.id.content,fragmentProfile,"");
                            ftProfile.commit();
                            return true;

                        case R.id.nav_users:

                            actionBar.setTitle("Users");
                            UsersFragment fragmentUser= new UsersFragment();
                            FragmentTransaction ftUser= getSupportFragmentManager().beginTransaction();
                            ftUser.replace(R.id.content,fragmentUser,"");
                            ftUser.commit();
                            return true;

                    }
                    return false;
                }
            };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void checkUserStatus(){

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user!=null){
            mUID = user.getUid();
            //save uid of currentle signed in user
/*
            SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID",mUID);
            editor.apply();

 */

        }else{
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_co,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id= item.getItemId();

        if(id==R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();

        }
        return super.onOptionsItemSelected(item);
    }
}