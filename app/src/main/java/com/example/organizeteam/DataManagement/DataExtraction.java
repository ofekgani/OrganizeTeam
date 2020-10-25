package com.example.organizeteam.DataManagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.example.organizeteam.Core.ActivityTransition;
import com.example.organizeteam.Core.ConstantNames;
import com.example.organizeteam.Core.Team;
import com.example.organizeteam.Core.User;
import com.example.organizeteam.Resources.Loading;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class DataExtraction
{

    /**
     * This function use to get value from firebase by key.
     * @param ds The pointer to find the value.
     * @param value The key from which you get the value
     * @return a value by his key.
     */
    private Object getValue(DataSnapshot ds, Class value)
    {
        return ds.getValue (value);
    }

    /**
     * This function use to extracts all the information out of the firebase to use.
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void getUserInformation(final ISavable iSavable)
    {
        //here we put all the data
        final Map<String,Object> userInfo = new HashMap<> (  );

        //user`s teams
        final Map<String, Team> teams = new HashMap<> (  );

        //Get email to get access to user`s data.
        final String email = FirebaseAuth.getInstance ().getCurrentUser().getEmail ();
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance ().getReference (ConstantNames.USER_PATH );

        //Get user data (name, email, logo, user`s key id) .
        mDatabase.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren ())
                {
                    if(ds.child ( ConstantNames.DATA_USER_EMAIL ).getValue ().equals ( email )){

                        //put all user information into Map.
                        User user = (User) getValue ( ds,User.class );
                        userInfo.put ( ConstantNames.USER,user );

                        //Get user`s team.
                        String teamID = null;
                        if(ds.hasChild ( ConstantNames.DATA_USER_TEAMS ))
                            teamID = ds.child ( ConstantNames.DATA_USER_TEAMS ).getValue ().toString ();

                        final String finalTeamID = teamID;

                        //Get access to all information about user`s team.
                        DatabaseReference teamDatabase =  FirebaseDatabase.getInstance ().getReference (ConstantNames.TEAM_PATH );
                        teamDatabase.addListenerForSingleValueEvent ( new ValueEventListener () {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren ())
                                {
                                    if(finalTeamID != null && ds.equals ( finalTeamID ))
                                    {
                                        //get team`s information
                                        Team team = (Team)getValue ( ds,Team.class );
                                        teams.put ( ds.child ( ConstantNames.DATA_TEAM_ID ).getValue ().toString (),team );

                                        //Save the called data.
                                        userInfo.put ( ConstantNames.USER_TEAMS,(Map)teams );
                                    }
                                }
                                //save all called data into the interface
                                iSavable.onDataRead ( userInfo );
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        } );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    /**
     * This function use to extracts all the information about the team out of the firebase to use.
     * @param hostID user`s id to get its information.
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void getTeamInformation(final String hostID,final ISavable iSavable)
    {
        //here we put all the data
        final Map<String,Object> teamInfo = new HashMap<> (  );
        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance ().getReference (ConstantNames.USER_PATH );

        //Get user data (name, email, logo, user`s key id) .
        mDatabase.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren ())
                {
                    if(ds.child ( ConstantNames.DATA_USER_ID ).getValue ().equals ( hostID )){
                        User user = (User)getValue ( ds,User.class );
                        teamInfo.put ( ConstantNames.TEAM_HOST,user );

                        //save all called data into the interface
                        iSavable.onDataRead ( teamInfo );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Get all the teams
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void getTeams(final ISavable iSavable)
    {
        //here we put all the data
        final ArrayList<Team> teamsInfo = new ArrayList<> (  );

        final DatabaseReference mDatabase =  FirebaseDatabase.getInstance ().getReference (ConstantNames.TEAM_PATH );
        mDatabase.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren ())
                {
                    Team team = (Team) getValue ( ds,Team.class );
                    teamsInfo.add(team);
                }

                //save all called data into the interface
                iSavable.onDataRead ( teamsInfo );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * This function use to upload image to firebase.
     * @param image The image url that we want to save.
     * @param context The activity which from we want to save the image.
     * @param path The path that we want to save the image url.
     * @param id The user`s id that we want to save the image url.
     * @param where The place that we want to save the image.
     * @param intent Update the new data to this intent.
     */
    public void uploadPicture(Uri image, final Context context, final String path , final String id, String where, final Intent intent, final ISavable iSavable)
    {
        //Get access to firebase storage.
        FirebaseStorage storage = FirebaseStorage.getInstance ();
        StorageReference storageReference = storage.getReference ();

        final ActivityTransition activityTransition = new ActivityTransition ();

        //Create Progress Dialog to user.
        final Loading loading = new Loading ();
        final ProgressDialog pd = loading.getProgressDialog ( context,"Upload Image... ");

        //The place that we want to save the image.
        final StorageReference riversRef = storageReference.child("userLogo/"+where);

        //Upload image to firebase.
        riversRef.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot> () {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss ();

                //get image url
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri downloadUrl)
                    {
                        //save image url into string
                        String image = downloadUrl.toString ();

                        //save the new image url into firebase
                        setData (path, id,ConstantNames.DATA_USER_LOGO,image );

                        //save the new image url into intent
                        activityTransition.setData (intent, ConstantNames.USER_LOGO,image);

                        //save image url into the interface
                        iSavable.onDataRead ( image );
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss ();
                Toast.makeText ( context,"Failed To Upload",Toast.LENGTH_LONG ).show ();
            }
        }).addOnProgressListener ( new OnProgressListener<UploadTask.TaskSnapshot> () {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                loading.calculatePercent ( taskSnapshot, pd );
            }
        });
    }

    /**
     * Set new information into firebase by path, id and key.
     * @param path The path that we want to save the value.
     * @param id the user`s id into which we want to save.
     * @param key The key where we want to save.
     * @param value The value that we want to save.
     */
    public void setData(String path,String id,String key,String value)
    {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance ().getReference ( path ).child ( id );
        mDatabase.child ( key ).setValue ( value );
    }

    /**
     * Set new information in new place by path, id and key.
     * @param path The path that we want to save the value.
     * @param id the user`s id into which we want to save.
     * @param key The key where we want to save.
     * @param value The value that we want to save.
     */
    public void setNewData(String path,String id,String key,String value)
    {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance ().getReference ( path ).child ( id );
        mDatabase.child ( key ).push ().setValue ( value );
    }

    /**
     * Set object into firebase by path and key.
     * @param path The path that we want to save the value.
     * @param key The key where we want to save.
     * @param value The value that we want to save.
     */
    public void setObject(String path,String key,Object value)
    {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance ().getReference ( path );
        mDatabase.child ( key ).setValue ( value );
    }

    /**
     * check if to path has child
     * @param path The path that we want to check.
     * @param userID The user that we want to check
     * @param child The child that we want to check if its exist.
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void hasChild(String path,final String userID , final String child, final ISavable iSavable)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(path);
        rootRef.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child (""+userID ).hasChild(""+child)) {
                    iSavable.onDataRead ( true );
                }
                else
                {
                    iSavable.onDataRead ( false );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
