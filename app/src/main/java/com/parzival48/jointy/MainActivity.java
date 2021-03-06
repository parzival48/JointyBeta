package com.parzival48.jointy;

import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText txtusername,txtpassword;
    String username,password,serverPass,serverLine,sList;
    boolean userExist;
    DatabaseReference jointyDB = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configSignUpButton();

        txtusername = (EditText)findViewById(R.id.txtUsername);
        txtpassword = (EditText)findViewById(R.id.txtPassword);

        Button signin = (Button)findViewById(R.id.btSignin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Toast tLoad = Toast.makeText(MainActivity.this,"Loading ...",Toast.LENGTH_SHORT);
                tLoad.show();

                clearGlobalVariable();

                username = txtusername.getText().toString();
                password = txtpassword.getText().toString();

                jointyDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userExist = dataSnapshot.child("userdata").child(username).exists();
                        try{
                            serverPass = dataSnapshot.child("userdata").child(username)
                                    .child("password").getValue().toString();

                            serverLine = dataSnapshot.child("userdata").child(username)
                                    .child("lineid").getValue().toString();

                            sList = dataSnapshot.child("userdata").child(username)
                                    .child("eventList").getValue().toString();

                            ActiveStatus.eventList = sList;
                            ActiveStatus.username = username;
                            ActiveStatus.lineid = serverLine;
                        }
                        catch (Exception e){
                            serverPass = "";
                            tLoad.cancel();
                            Toast.makeText(MainActivity.this,
                                    "Incorrect username or password",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this,
                                "Unstable Internet Connection",
                                Toast.LENGTH_LONG).show();
                    }
                });
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(userExist && match(password,serverPass)){
                            ActiveStatus.tempCode = "";
                            Intent i = new Intent(MainActivity.this,FeedUI.class);
                            i.setFlags(i.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                            tLoad.cancel();
                            Toast.makeText(MainActivity.this,
                                    "Welcome, "+ActiveStatus.username+" !",
                                    Toast.LENGTH_LONG).show();

                            startActivity(i);
                        }
                        else{
                            tLoad.cancel();
                            Toast.makeText(MainActivity.this,
                                    "Incorrect username or password",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },888);
            }
        });
    }

    private void configSignUpButton(){
        Button SignUpButton = (Button) findViewById(R.id.btSignup);
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpUI.class));
            }
        });
    }

    private boolean match(String client,String server){
        if(client.equals(server)){
            return true;
        }
        else{
            return false;
        }
    }

    private void clearGlobalVariable(){
        ActiveStatus.tempString = null;
        ActiveStatus.tempEventName = null;
        ActiveStatus.arrayOfEvents = null;
    }
}