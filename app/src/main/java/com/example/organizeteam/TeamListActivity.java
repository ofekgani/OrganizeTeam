package com.example.organizeteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class TeamListActivity extends AppCompatActivity {

    ListView listView;

    TextView tv_name, tv_email;

    FirebaseDatabase fd;
    DatabaseReference userRef;
    private static final String USER = "user";
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_team_list );

        Intent intent = getIntent();
        email = intent.getStringExtra ( "email" );

        listView = findViewById ( R.id.lv_teams );

        tv_name = findViewById ( R.id.tv_userName );
        tv_email = findViewById ( R.id.tv_userEmail );

        fd = FirebaseDatabase.getInstance ();
        userRef =  fd.getReference (USER );

        userRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren ())
                {
                    if(ds.child ( "email" ).getValue ().equals ( email )){
                        tv_name.setText ( ds.child ( "fullName" ).getValue ().toString () );
                        tv_email.setText ( ds.child ( "email" ).getValue ().toString () );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        Team electroBunny =new Team ( "electro bunny", "the best team ever" );
        Team desertEagles = new Team ( "Desert Eagles", "the worst team ever" );

        ArrayList<Team> teamList = new ArrayList<> (  );
        teamList.add ( electroBunny );
        teamList.add ( desertEagles );

        TeamListAdapter adapter = new TeamListAdapter(this,R.layout.team_adapter_view_layout,teamList);
        listView.setAdapter ( adapter );
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_singOut(View view) {
        //sign out
        FirebaseAuth.getInstance ().signOut ();
        startActivity ( new Intent ( getApplicationContext (),LoginActivity.class ) );
        finish ();
    }
}