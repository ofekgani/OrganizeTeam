package com.example.organizeteam.AuthorizationSystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.example.organizeteam.Core.ActivityTransition;
import com.example.organizeteam.Core.ConstantNames;
import com.example.organizeteam.Resources.Team;
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

public class UserInfo
{

    /**
     * This function use to get value from firebase by key.
     * @param ds The pointer to find the value.
     * @param key The key from which you get the value
     * @return a value by his key.
     */
    private String getUserInfo(DataSnapshot ds, String key)
    {
        return ds.child ( key ).getValue ().toString ();
    }

    /**
     * This function use to pull all the information out of the firebase into a database
     * @param iUser Is used to keep the information called so we can use that information.
     */
    public void getUserInformation(final IUser iUser)
    {
        //here we put all the data
        final Map<String,Object> userInfo = new HashMap<> (  );

        //all user`s teams id
        final ArrayList<String> teamsID = new ArrayList<> (  );

        //user`s teams
        final Map<String,Team> teams = new HashMap<> (  );

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
                        userInfo.put ( ConstantNames.USER_NAME,(String)getUserInfo ( ds, ConstantNames.DATA_USER_NAME ) );
                        userInfo.put ( ConstantNames.USER_EMAIL,(String)getUserInfo ( ds,ConstantNames.DATA_USER_EMAIL ) );
                        userInfo.put ( ConstantNames.USER_KEY_ID,(String)getUserInfo ( ds,ConstantNames.DATA_USER_ID ) );
                        userInfo.put ( ConstantNames.USER_LOGO,(String)getUserInfo ( ds, ConstantNames.DATA_USER_LOGO ) );

                        //Get access to user`s teams.
                        final DatabaseReference fuckDatabase =  FirebaseDatabase.getInstance ().getReference (ConstantNames.USER_PATH ).child (ds.getKey ()).child (ConstantNames.DATA_USER_TEAMS );
                        fuckDatabase.addListenerForSingleValueEvent ( new ValueEventListener () {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                //Add all user`s teams id to list for find later the information about the teams.
                                for(DataSnapshot ds : dataSnapshot.getChildren ())
                                {
                                    teamsID.add ( ds.getValue ().toString () ) ;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        } );

                        //Get access to all information about user`s teams.
                        DatabaseReference teamDatabase =  FirebaseDatabase.getInstance ().getReference (ConstantNames.TEAM_PATH );
                        teamDatabase.addListenerForSingleValueEvent ( new ValueEventListener () {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren ())
                                {
                                    for(int i = 0; i < teamsID.size (); i++)
                                    {
                                        String value = ds.child ( "keyID" ).getValue ().toString ();
                                        String fuck = teamsID.get ( i );
                                        if(value.equals ( fuck ) )
                                        {
                                            String name = ds.child ( "name" ).getValue ().toString ();
                                            String description = ds.child ( "description" ).getValue ().toString ();
                                            String logo = ds.child ( "logo" ).getValue ().toString ();
                                            String keyID = ds.child ( "keyID" ).getValue ().toString ();
                                            Team team = new Team(name,description,logo,keyID);
                                            teams.put ( ds.child ( "keyID" ).getValue ().toString (),team );

                                            //Save the called data.
                                            userInfo.put ( "userTeams",(Map)teams );
                                        }
                                    }
                                }
                                iUser.onDataRead ( userInfo );

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        } );

                        //Save the called data.

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
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
     * This function use to upload image to firebase.
     * @param image The image url that we want to save.
     * @param context The activity which from we want to save the image.
     * @param path The path that we want to save the image url.
     * @param id The user`s id that we want to save the image url.
     * @param where The place that we want to save the image.
     * @param intent Update the new data to this intent.
     */
    public void uploadPicture(Uri image, final Context context, final String path , final String id, String where, final Intent intent, final IPicture iPicture)
    {
        //Get access to firebase storage.
        FirebaseStorage storage = FirebaseStorage.getInstance ();
        StorageReference storageReference = storage.getReference ();

        final ActivityTransition activityTransition = new ActivityTransition ();

        //Create Progress Dialog to user.
        final ProgressDialog pd = new ProgressDialog ( context );
        pd.setCanceledOnTouchOutside(false);
        pd.setTitle ( "Upload Image..." );
        pd.show ();

        //The place that we want to save the image.
        final StorageReference riversRef = storageReference.child("userLogo/"+where);

        //Upload image to fire base.
        riversRef.putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot> () {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss ();
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                        {
                            @Override
                            public void onSuccess(Uri downloadUrl)
                            {
                                String image = downloadUrl.toString ();
                                setData (path, id,ConstantNames.DATA_USER_LOGO,image );
                                activityTransition.setData (intent, ConstantNames.USER_LOGO,image);
                                iPicture.onUploadImage ( image );
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener () {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss ();
                        Toast.makeText ( context,"Failed To Upload",Toast.LENGTH_LONG ).show ();
                    }
                }).addOnProgressListener ( new OnProgressListener<UploadTask.TaskSnapshot> () {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progressPercent = (100 * taskSnapshot.getBytesTransferred () /  taskSnapshot.getTotalByteCount ());
                pd.setMessage ( "progress: " + (int)progressPercent + "%");
            }
        } );
    }
}
