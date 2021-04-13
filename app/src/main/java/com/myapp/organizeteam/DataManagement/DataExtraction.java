package com.myapp.organizeteam.DataManagement;

import android.app.ProgressDialog;
import android.content.Context;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.Query;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Meeting;
import com.myapp.organizeteam.Core.Mission;
import com.myapp.organizeteam.Core.Post;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.Core.Submitter;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.MyService.Token;
import com.myapp.organizeteam.Resources.Loading;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DataExtraction
{

    /**
     * Build reference to firebase realtime by path.
     * @param path A path to get access to data.
     * @return Return reference to data by path.
     */
    private DatabaseReference getDatabaseReference(String path) {
        return FirebaseDatabase.getInstance().getReference(path);
    }

    /**
     * This function use to get value from firebase by key.
     * @param ds The pointer to find the value.
     * @param value The key from which you get the value.
     * @return Return value by a pointer.
     */
    private Object getValue(DataSnapshot ds, Class value)
    {
        return ds.getValue (value);
    }

    /**
     * Get team`s id by pointer.
     * @param ds The pointer to find the team.
     * @return Return team id if there is to pointer`s child parent that called "team" , if has not return null.
     */
    private String getTeamID(DataSnapshot ds) {
        String teamID = null;

        //Check if to pointer has child that called "team".
        if(ds.hasChild ( ConstantNames.DATA_USER_TEAMS ))
            teamID = ds.child ( ConstantNames.DATA_USER_TEAMS ).getValue ().toString ();

        return teamID;
    }

    /**
     * Get token from device and save him into interface to late use.
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    private void getToken(final ISavable iSavable) {
        FirebaseInstanceId.getInstance ().getInstanceId ().addOnCompleteListener ( new OnCompleteListener<InstanceIdResult> () {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful ()) {
                    String token = task.getResult ().getToken ();

                    Log.d ( "Token", token );

                    iSavable.onDataRead ( token );
                }
            }
        } );
    }

    /**
     * This function use to extracts all the information about the team out of the firebase to use by team`s id.
     * @param teamID team`s id to get its information.
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void getTeamDataByID(final String teamID, final ISavable iSavable) {
        DatabaseReference teamDatabase = getDatabaseReference(ConstantNames.TEAM_PATH);
        teamDatabase.addListenerForSingleValueEvent ( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren ())
                {
                    if(teamID != null && ds.child(ConstantNames.DATA_TEAM_ID).getValue().toString().equals ( teamID ))
                    {
                        //get team`s information
                        Team team = (Team)getValue ( ds,Team.class );

                        //save all called data into the interface
                        iSavable.onDataRead ( team );

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    /**
     * This function use to extracts all the information about the user out of the firebase to use.
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void getCurrentUserData(final ISavable iSavable)
    {
        //here we put all the data
        final Map<String,Object> userInfo = new HashMap<> (  );

        //Get email to get access to user`s data.
        final String email = FirebaseAuth.getInstance ().getCurrentUser().getEmail ();
        if(email == null) return;
        final DatabaseReference mDatabase = getDatabaseReference(ConstantNames.USER_PATH);

        //Get user data (name, email, logo, user`s key id) .
        mDatabase.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Find the user by email.
                for(DataSnapshot ds : dataSnapshot.getChildren ())
                {
                    if(ds.child ( ConstantNames.DATA_USER_EMAIL ).getValue ().equals ( email )){

                        //put all user information into Map.
                        final User user = (User) getValue ( ds,User.class );
                        userInfo.put ( ConstantNames.USER,user );

                        //Update user`s token and put the token into Map.
                        getToken ( new ISavable () {
                            @Override
                            public void onDataRead(Object save) {
                                setNewData ( ConstantNames.TOKEN_PATH,user.getKeyID (),(String)save);

                                //put user`s token into Map
                                Token token = new Token(user.getKeyID (),(String)save);
                                userInfo.put ( ConstantNames.TOKEN,token);
                            }
                        } );

                        //Get user`s team.
                        final String teamID = getTeamID(ds);

                        //check if to user has team
                        //if has, save team`s information.
                        if(teamID != null)
                        {
                            //Get access to all information about user`s team.
                            getTeamDataByID(teamID, new ISavable() {
                                @Override
                                public void onDataRead(Object save) {
                                    //put user`s team information into Map
                                    userInfo.put ( ConstantNames.TEAM ,save );

                                    iSavable.onDataRead(userInfo); //Save user`s information, user`s token, user`s team information.
                                }
                            });
                        }
                        else
                        {
                            iSavable.onDataRead(userInfo); //Save user`s information, user`s token.
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    /**
     * This function use to extracts all the information about the user out of the firebase to use by user`s id.
     * @param id user`s id to get its information.
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void getUserDataByID(final String id, final ISavable iSavable)
    {
        //here we put all the data
        final DatabaseReference mDatabase = getDatabaseReference(ConstantNames.USER_PATH);

        //Get user data (name, email, logo, user`s key id) .
        mDatabase.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren ())
                {
                    if(ds.child ( ConstantNames.DATA_USER_ID ).getValue ().equals ( id )){
                        User user = (User)getValue ( ds,User.class );

                        //save all called data into the interface
                        iSavable.onDataRead ( user );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Get all the teams into List to use.
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void getTeams(final ISavable iSavable)
    {
        //here we put all the data
        final ArrayList<Team> teamsInfo = new ArrayList<> (  );

        final DatabaseReference mDatabase = getDatabaseReference(ConstantNames.TEAM_PATH);
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
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void uploadPicture(Uri image, final Context context, final String path , final String id, String where, final ISavable iSavable)
    {
        //Get access to firebase storage.
        FirebaseStorage storage = FirebaseStorage.getInstance ();
        StorageReference storageReference = storage.getReference ();

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
                        setNewData (path, id,ConstantNames.DATA_USER_LOGO,image );

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
     * This function use to get user`s token by user`s id.
     * @param userID user`s id to get its information.
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void getToken(final String userID,final ISavable iSavable)
    {
        final DatabaseReference mDatabase =  getDatabaseReference(ConstantNames.TOKEN_PATH).child ( userID );
        mDatabase.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                iSavable.onDataRead ( snapshot.getValue () );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * This function use to delete token from firebase.
     * example, when the user logout from system.
     * @param userID user`s id to know which token delete.
     */
    public void deleteToken(final String userID)
    {
        //Check if to user there is exist token in firebase.
        hasChild(ConstantNames.TOKEN_PATH, userID, new ISavable() {
            @Override
            public void onDataRead(Object hasChild) {
                if((boolean)hasChild)
                {
                    //remove token from firebase
                    DatabaseReference mDatabase =  getDatabaseReference(ConstantNames.TOKEN_PATH).child ( userID );
                    mDatabase.removeValue();
                }
            }
        });
    }


    /**
     * Set new information in new place by path, id of object and parent`s key.
     * @param path The path that we want to save the value.
     * @param id The child id to find where to save the new data into it. (user`s id, team`s id, token`s id)
     * @param key The key where we want to save.
     * @param value The value that we want to save.
     */
    public void setNewData(String path,String id,String key,String value)
    {
        DatabaseReference mDatabase = getDatabaseReference(path).child ( id );
        mDatabase.child ( key ).setValue ( value );
    }

    /**
     * Set new information in new place by path and key.
     * @param path The path that we want to save the value.
     * @param key The key where we want to save.
     * @param value The value that we want to save.
     */
    public void setNewData(String path,String key,String value)
    {
        DatabaseReference mDatabase = getDatabaseReference(path);
        mDatabase.child ( key ).setValue ( value );
    }

    public void setNewData(String path, String teamID, String keyID, String key, Object value) {
        DatabaseReference mDatabase = getDatabaseReference(path);
        mDatabase.child ( teamID ).child(keyID).child(key).setValue ( value );
    }

    /**
     * Set object into firebase by path and key.
     * @param path The path that we want to save the value.
     * @param key The key where we want to save.
     * @param value An object to set by key in firebase.
     */
    public void setObject(String path,String key,Object value)
    {
        DatabaseReference mDatabase = getDatabaseReference(path);
        mDatabase.child ( key ).setValue(value);
    }

    public void setObject(String path,String keyID,String key ,Object value)
    {
        DatabaseReference mDatabase = getDatabaseReference(path);
        mDatabase.child ( keyID ).child(key).setValue(value);
    }

    public void setObject(String path, String key, Object value, final IRegister iRegister)
    {
        iRegister.onProcess();
        DatabaseReference mDatabase = getDatabaseReference(path);
        mDatabase.child ( key ).setValue(value, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if(error == null)
                {
                    iRegister.onDone(true,null);
                }
                else
                {
                    iRegister.onDone(false,error.getMessage());
                }
            }
        });
    }

    /**
     * Push new information by object`s id and key name in firebase.
     * @param path The path that we want to push a value.
     * @param id The object id to find where to save the new data into it. (user`s id, team`s id, token`s id)
     * @param key The key where we want to save.
     * @param value An object to push by key in firebase.
     */
    public void pushNewData(String path,String id, String key,Object value)
    {
        DatabaseReference mDatabase = getDatabaseReference(path);
        mDatabase.child ( id ).child ( key ).push().setValue(value);
    }

    /**
     * Delete key by object`s id.
     * @param path The path that we want to delete a value.
     * @param id The object id to find where to delete the data. (user`s id, team`s id, token`s id)
     * @param key The key that we want to delete.
     */
    public void deleteData(final String path, final String id, final String key, final DataListener dataListener)
    {
        //Check if the key that we want to delete, exist in firebase.
        hasChild(path,id ,key , new ISavable() {
            @Override
            public void onDataRead(Object hasChild) {
                if((boolean)hasChild)
                {
                    //remove data from firebase
                    DatabaseReference mDatabase = getDatabaseReference(path).child ( id ).child(key);
                    mDatabase.removeValue();
                    dataListener.onDataDelete();
                }
            }
        });
    }

    /**
     * Delete value by key.
     * @param path The path that we want to delete a value.
     * @param id The object id to find where to delete the data. (user`s id, team`s id, token`s id)
     * @param key The key that which from we want to delete a child.
     * @param value The value that we want to delete by key.
     */
    public void deleteValue(final String path, final String id, final String key,final String value,final DataListener dataListener)
    {
        //Check if the key that we want to delete, exist in firebase.
        hasChild(path,id ,key , new ISavable() {
            @Override
            public void onDataRead(Object hasChild) {
                if((boolean)hasChild)
                {
                    DatabaseReference reference;
                    reference = FirebaseDatabase.getInstance().getReference(path).child(id).child(key);

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Find the child that we want to delete
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                String child = childSnapshot.getValue().toString();
                                String parent = childSnapshot.getKey();
                                if(child.equals(value))
                                {
                                    //remove data from firebase
                                    DatabaseReference mDatabase = getDatabaseReference(path).child ( id ).child(key).child(parent);
                                    mDatabase.removeValue();
                                    dataListener.onDataDelete();
                                    //End the loop when the child found.
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }

    public void deleteValue(final String path, final String teamID, final String userID,final String key, final String value,final DataListener dataListener)
    {
        //Check if the key that we want to delete, exist in firebase.
        hasChild(path,teamID ,userID , new ISavable() {
            @Override
            public void onDataRead(Object hasChild) {
                if((boolean)hasChild)
                {
                    DatabaseReference reference;
                    reference = FirebaseDatabase.getInstance().getReference(path).child(teamID).child(userID).child(key);

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Find the child that we want to delete
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                String child = childSnapshot.getValue().toString();
                                String parent = childSnapshot.getKey();
                                if(child.equals(value))
                                {
                                    //remove data from firebase
                                    DatabaseReference mDatabase = getDatabaseReference(path).child ( teamID ).child(userID).child(key).child(parent);
                                    mDatabase.removeValue();
                                    dataListener.onDataDelete();
                                    //End the loop when the child found.
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }

    /**
     * Check if to child has child.
     * @param path The path that we want to check.
     * @param id The user that we want to check
     * @param child The child that we want to check if its exist.
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void hasChild(String path,final String id , final String child, final ISavable iSavable)
    {
        final DatabaseReference rootRef = getDatabaseReference(path);
        rootRef.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child (""+id ).hasChild(""+child)) {
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

    /**
     * Check if to Parent has child.
     * @param path The path that we want to check.
     * @param id The parent that we want to check
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void hasChild(String path,final String id ,final ISavable iSavable)
    {
        DatabaseReference rootRef = getDatabaseReference(path);
        rootRef.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(""+id)) {
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

    public void hasChild(DatabaseReference path,final String key ,final ISavable iSavable)
    {
        path.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(""+key)) {
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

    /**
     * Check if a value exist by key.
     * @param path The path that we want to check.
     * @param id The parent that we want to check.
     * @param key The key that which from we want to check if its child exist
     * @param value The value to check if its exist.
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void isValueExist(String path, String id, final String key, final String value , final ISavable iSavable)
    {
        DatabaseReference rootRef = getDatabaseReference(path).child(id).child(key);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!snapshot.exists())
                {
                    iSavable.onDataRead(false);
                }
                else if(snapshot.getValue().equals(value))
                {
                    iSavable.onDataRead(true);
                    return;
                }
                else
                {
                    iSavable.onDataRead(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Get all childs by object id and parent key.
     * @param path The path that we want to get the childs.
     * @param id The object id to find the data. (user`s id, team`s id, token`s id)
     * @param key The key that which from we want to get the data.
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void getChildsByKey(String path, String id, String key, final ISavable iSavable) {

        final ArrayList<String> childs = new ArrayList<>();

        final DatabaseReference mDatabase =  getDatabaseReference(path).child ( id ).child(key);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    childs.add(ds.getValue().toString());
                }
                iSavable.onDataRead(childs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Get all the users by their id and put all into array list.
     * @param id Array list of users`s id.
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void getUsersByKeys(final ArrayList<String> id, final ISavable iSavable)
    {
        //here we put all the data
        final DatabaseReference mDatabase = getDatabaseReference(ConstantNames.USER_PATH);
        final ArrayList<User> users = new ArrayList<>();

        //Get user data (name, email, logo, user`s key id) .
        mDatabase.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(String userID : id)
                {
                    for(DataSnapshot ds : dataSnapshot.getChildren ())
                    {
                        if(ds.child ( ConstantNames.DATA_USER_ID ).getValue ().equals ( userID )){
                            User user = (User)getValue ( ds,User.class );
                            users.add(user);
                        }
                    }
                }
                //save all called data into the interface
                iSavable.onDataRead ( users );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Get all users from a team by team id.
     * @param id the team`s id to find all the users at this team.
     * @param iSavable Is used to keep the information called so we can use that information.
     */
    public void getAllUsersByTeam(String id, String key, final ISavable iSavable) {
        getChildsByKey(ConstantNames.TEAM_PATH,id,key, new ISavable() {
            @Override
            public void onDataRead(Object save) {
                ArrayList<String> usersID = (ArrayList<String>)save;

                getUsersByKeys(usersID, new ISavable() {
                    @Override
                    public void onDataRead(Object save) {
                        iSavable.onDataRead(save);
                    }
                });


            }
        });
    }

    public void getAllMeetingsByTeam(String teamID, final ISavable iSavable)
    {
        final ArrayList<Meeting> meetings = new ArrayList<>();

        final DatabaseReference mDatabase =  getDatabaseReference(ConstantNames.MEETINGS_PATH).child ( teamID );
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Meeting meeting = (Meeting) getValue ( ds,Meeting.class );
                    meetings.add(meeting);
                }
                iSavable.onDataRead(meetings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getAllRolesByTeam(String teamID, final ISavable iSavable)
    {
        final ArrayList<Role> roles = new ArrayList<>();

        final DatabaseReference mDatabase =  getDatabaseReference(ConstantNames.ROLE_PATH).child ( teamID );
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Role role = (Role) getValue ( ds,Role.class );
                    roles.add(role);
                }
                iSavable.onDataRead(roles);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getAllRolesByUser(String userID, final String teamID, final ISavable iSavable)
    {
        final ArrayList<Role> roles = new ArrayList<>();
        final ArrayList<String> rolesID = new ArrayList<>();

        final DatabaseReference mDatabase =  getDatabaseReference(ConstantNames.USER_ACTIVITY_PATH).child(teamID).child ( userID ).child(ConstantNames.DATA_USER_ROLES);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    rolesID.add(ds.getValue().toString());
                }

                final DatabaseReference mDatabase = getDatabaseReference(ConstantNames.ROLE_PATH).child ( teamID );
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            for(String id : rolesID)
                            {
                                if(ds.getKey().equals(id))
                                {
                                    Role role = (Role) getValue ( ds,Role.class );
                                    roles.add(role);
                                }
                            }
                        }
                        iSavable.onDataRead(roles);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getPermissions(final String teamID, final ArrayList<Role> roles, final ISavable iSavable)
    {
        final Map<String,Object> permissions = new HashMap<>();

        final ArrayList<String> meetingPermissions = new ArrayList<>();
        final ArrayList<String> taskPermissions = new ArrayList<>();
        final ArrayList<String> postPermissions = new ArrayList<>();

        final DatabaseReference mDatabase =  getDatabaseReference(ConstantNames.ROLE_PATH).child ( teamID );

        final TaskCompletionSource<ArrayList<Role>> dbSourceMeetingAll = new TaskCompletionSource<>();
        final Task dbPerMetingAll = dbSourceMeetingAll.getTask();
        final TaskCompletionSource<ArrayList<Role>> dbSourceTaskAll = new TaskCompletionSource<>();
        final Task dbPerTaskAll = dbSourceTaskAll.getTask();
        final TaskCompletionSource<ArrayList<Role>> dbSourcePostAll = new TaskCompletionSource<>();
        final Task dbPerPostAll = dbSourceTaskAll.getTask();

        getListPermissions(teamID, roles, meetingPermissions, mDatabase, dbSourceMeetingAll, dbPerMetingAll, ConstantNames.DATA_ROLE_MEETING_PERMISSION);
        getListPermissions(teamID, roles, taskPermissions, mDatabase, dbSourceTaskAll, dbPerTaskAll, ConstantNames.DATA_ROLE_TASK_PERMISSION);
        getListPermissions(teamID, roles, postPermissions, mDatabase, dbSourcePostAll, dbPerPostAll, ConstantNames.DATA_ROLE_POST_PERMISSION);

        Tasks.whenAll(dbPerMetingAll,dbPerTaskAll,dbPerPostAll).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(dbPerMetingAll.getResult() != null)
                {
                    permissions.put(ConstantNames.USER_PERMISSIONS_MEETING,dbPerMetingAll.getResult());
                }
                if(dbPerTaskAll.getResult() != null)
                {
                    permissions.put(ConstantNames.USER_PERMISSIONS_TASK,dbPerTaskAll.getResult());
                }
                if(dbPerPostAll.getResult() != null)
                {
                    permissions.put(ConstantNames.USER_PERMISSIONS_POST,dbPerPostAll.getResult());
                }
                if((dbPerMetingAll.getResult() != null && dbPerTaskAll.getResult() != null) && dbPerPostAll.getResult() != null)
                {
                    iSavable.onDataRead(permissions);
                }

                final TaskCompletionSource<ArrayList<Role>> dbSourceMeeting = new TaskCompletionSource<>();
                final Task dbMeeting = dbSourceMeeting.getTask();
                getRolesResult(dbSourceMeeting, meetingPermissions, teamID);

                final TaskCompletionSource<ArrayList<Role>> dbSourceTask = new TaskCompletionSource<>();
                final Task dbTask = dbSourceTask.getTask();
                getRolesResult(dbSourceTask, taskPermissions, teamID);

                final TaskCompletionSource<ArrayList<Role>> dbSourcePost = new TaskCompletionSource<>();
                final Task dbPost = dbSourcePost.getTask();
                getRolesResult(dbSourcePost, postPermissions, teamID);

                Task<Void> allTask = Tasks.whenAll(dbTask,dbMeeting);
                allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(dbMeeting.getResult() != null)
                        {
                            permissions.put(ConstantNames.USER_PERMISSIONS_MEETING,dbMeeting.getResult());
                        }
                        if(dbTask.getResult() != null)
                        {
                            permissions.put(ConstantNames.USER_PERMISSIONS_TASK,dbTask.getResult());
                        }
                        if(dbPost.getResult() != null)
                        {
                            permissions.put(ConstantNames.USER_PERMISSIONS_POST,dbPost.getResult());
                        }
                        iSavable.onDataRead(permissions);
                    }
                });
                allTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // apologize profusely to the user!
                    }
                });

            }
        });

    }

    private void getRolesResult(final TaskCompletionSource<ArrayList<Role>> dbSource, final ArrayList<String> permissions, final String teamID) {
        if (permissions != null && !permissions.isEmpty()) {
            DatabaseReference refMeeting = FirebaseDatabase.getInstance().getReference(ConstantNames.ROLE_PATH).child(teamID);
            refMeeting.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Collections.sort(permissions);
                    ArrayList<String> nRolesID = removeDuplicateFromList(permissions);

                    getRolesByID(teamID, nRolesID, new ISavable() {
                        @Override
                        public void onDataRead(Object save) {
                            dbSource.setResult((ArrayList<Role>) save);
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    dbSource.setException(databaseError.toException());
                }
            });
        } else {
            dbSource.setResult(null);
        }
    }

    private void getListPermissions(final String teamID, final ArrayList<Role> roles, final ArrayList<String> permissions, DatabaseReference mDatabase, final TaskCompletionSource<ArrayList<Role>> dbSource, final Task dbPer, final String order) {
        Query queryT = mDatabase.orderByChild(order).startAt("All");
        queryT.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean wait = false;
                outerLoop:
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (Role r : roles) {
                        if (r.getKeyID().equals(ds.getKey())) {
                            if (ds.hasChild(order) && ds.child(order).getValue().equals("All")) {
                                wait = true;
                                getAllRolesByTeam(teamID, new ISavable() {
                                    @Override
                                    public void onDataRead(Object save) {
                                        ArrayList<Role> taskPermissions = (ArrayList<Role>) save;
                                        dbSource.setResult(taskPermissions);
                                    }
                                });
                                break outerLoop;
                            } else if (ds.hasChild(order)) {
                                for (DataSnapshot id : ds.child(order).getChildren()) {
                                    permissions.add(id.getValue().toString());
                                }
                            }
                            break;
                        }
                    }
                }
                if (!dbPer.isComplete() && wait == false) {
                    dbSource.setResult(null);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRolesByID(String teamID, final ArrayList<String> rolesID, final ISavable iSavable) {
        final ArrayList<Role> roles = new ArrayList<>();
        final DatabaseReference mDatabase = getDatabaseReference(ConstantNames.ROLE_PATH).child ( teamID );

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    for(String id : rolesID)
                    {
                        if(ds.getKey().equals(id))
                        {
                            Role role = (Role) getValue ( ds,Role.class );
                            roles.add(role);
                            break;
                        }
                    }
                }
                iSavable.onDataRead(roles);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList<String> removeDuplicateFromList(ArrayList<String> list) {
        for (int i = 0; i < list.size(); i++)
        {
            if(i != list.size()-1 && list.get(i).equals(list.get(i+1)))
            {
                list.remove(i);
                return removeDuplicateFromList(list);
            }
        }
        return list;
    }

    public void getUsersByRoles(final ArrayList<String> rolesID, String teamID, final ISavable iSavable)
    {
        final ArrayList<String> usersID = new ArrayList<>();

        final DatabaseReference mDatabase =  getDatabaseReference(ConstantNames.ROLE_PATH).child ( teamID );
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    for(String r : rolesID)
                    {
                        if(r.equals(ds.getKey()))
                        {
                            for(DataSnapshot userID : ds.child(ConstantNames.DATA_USERS_LIST).getChildren())
                            {
                                usersID.add(userID.getValue().toString());
                            }
                        }
                    }
                }
                Collections.sort(usersID);
                iSavable.onDataRead(removeDuplicateFromList(usersID));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getMeetingsByID(final ArrayList<String> meetingsID, final String teamID, final String userID, final ISavable iSavable)
    {
        final ArrayList<Meeting> meetings = new ArrayList<>();

        final DatabaseReference mDatabase =  getDatabaseReference(ConstantNames.MEETINGS_PATH).child ( teamID );
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    for(String id : meetingsID)
                    {
                        if(ds.getKey().equals(id))
                        {
                            Meeting meeting = (Meeting)getValue(ds,Meeting.class);
                            meetings.add(meeting);
                            meetingsID.remove(id);
                            break;
                        }
                    }
                }
                if(meetingsID.size() > 0)
                {
                    for(String id : meetingsID)
                    {
                        deleteValue(ConstantNames.USER_ACTIVITY_PATH, teamID, userID, ConstantNames.DATA_USER_MEETINGS, id, new DataListener() {
                            @Override
                            public void onDataDelete() {

                            }
                        });
                    }
                }
                iSavable.onDataRead(meetings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getTasksByID(final ArrayList<String> tasksID, final String teamID, final String userID, final ISavable iSavable)
    {
        final ArrayList<Mission> tasks = new ArrayList<>();

        final DatabaseReference mDatabase =  getDatabaseReference(ConstantNames.TASK_PATH).child ( teamID );
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    for(String id : tasksID)
                    {
                        if(ds.getKey().equals(id))
                        {
                            Mission task = (Mission) getValue(ds,Mission.class);
                            tasks.add(task);
                            tasksID.remove(id);
                            break;
                        }
                    }
                }
                if(tasksID.size() > 0)
                {
                    for(String id : tasksID)
                    {
                        deleteValue(ConstantNames.USER_ACTIVITY_PATH, teamID, userID, ConstantNames.DATA_USER_TASKS, id, new DataListener() {
                            @Override
                            public void onDataDelete() {

                            }
                        });
                    }
                }
                iSavable.onDataRead(tasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getMeetingsByUser(String userID, String teamID, final ISavable iSavable) {
        final ArrayList<String> meetings = new ArrayList<>();

        final DatabaseReference mDatabase = getDatabaseReference(ConstantNames.USER_ACTIVITY_PATH).child(teamID).child(userID).child(ConstantNames.DATA_USER_MEETINGS);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    meetings.add(ds.getValue().toString());
                }
                iSavable.onDataRead(meetings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getAllTasksByTeam(String keyID, final ISavable iSavable) {
        final ArrayList<Mission> tasks = new ArrayList<>();

        final DatabaseReference mDatabase =  getDatabaseReference(ConstantNames.TASK_PATH).child ( keyID );
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Mission task = (Mission) getValue ( ds,Mission.class );
                    tasks.add(task);
                }
                iSavable.onDataRead(tasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getTasksByUser(String userID, String teamID, final ISavable iSavable) {
        final ArrayList<String> tasks = new ArrayList<>();

        final DatabaseReference mDatabase = getDatabaseReference(ConstantNames.USER_ACTIVITY_PATH).child(teamID).child(userID).child(ConstantNames.DATA_USER_TASKS);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(ds.hasChildren())
                    {
                        Submitter task = ds.getValue(Submitter.class);
                        tasks.add(task.getTaskID());
                    }
                    else
                    {
                        tasks.add(ds.getValue().toString());
                    }
                }
                iSavable.onDataRead(tasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getSubmitters(String teamID, String taskID, final ISavable iSavable)
    {
        final ArrayList<String> usersID = new ArrayList<>();

        final DatabaseReference mDatabase = getDatabaseReference(ConstantNames.TASK_PATH).child(teamID).child(taskID);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(ConstantNames.DATA_USERS_LIST))
                {
                    for (DataSnapshot ds : snapshot.child(ConstantNames.DATA_USERS_LIST).getChildren())
                    {
                        if(ds.hasChild(ConstantNames.DATA_TASK_TITLE))
                            usersID.add(ds.getKey().toString());
                    }
                    getUsersByKeys(usersID, new ISavable() {
                        @Override
                        public void onDataRead(Object save) {
                            iSavable.onDataRead(save);
                        }
                    });
                }
                else
                {
                    iSavable.onDataRead(null);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getSubmitterTask(String teamID, String taskID, String userID, final ISavable iSavable) {
        final DatabaseReference mDatabase = getDatabaseReference(ConstantNames.TASK_PATH).child(teamID).child(taskID).child(ConstantNames.DATA_USERS_LIST).child(userID);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Submitter submitter = (Submitter) getValue(snapshot,Submitter.class);
                iSavable.onDataRead(submitter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getResponse(String teamID, String taskID, String userID, String response, final ISavable iSavable) {
        final DatabaseReference mDatabase = getDatabaseReference(ConstantNames.TASK_PATH).child(teamID).child(taskID).child(ConstantNames.DATA_USERS_LIST).child(userID).child(ConstantNames.DATA_TASK_REPLIES).child(response);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Submitter submitter = (Submitter) getValue(snapshot,Submitter.class);
                iSavable.onDataRead(submitter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getResponsesOfSubmitter(String teamID, String taskID, String userID, final ISavable iSavable) {

        final ArrayList<String> usersID = new ArrayList<>();

        final DatabaseReference mDatabase = getDatabaseReference(ConstantNames.TASK_PATH).child(teamID).child(taskID).child(ConstantNames.DATA_USERS_LIST).child(userID);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(ConstantNames.DATA_SUBMITTER_RESPONSES))
                {
                    for (DataSnapshot ds : snapshot.child(ConstantNames.DATA_SUBMITTER_RESPONSES).getChildren())
                    {
                        usersID.add(ds.getKey());
                    }
                    getUsersByKeys(usersID, new ISavable() {
                        @Override
                        public void onDataRead(Object save) {
                            iSavable.onDataRead(save);
                        }
                    });
                }
                else
                {
                    iSavable.onDataRead(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getAllPostsByTeam(String keyID, final ISavable iSavable) {
        final ArrayList<Post> tasks = new ArrayList<>();

        final DatabaseReference mDatabase =  getDatabaseReference(ConstantNames.POST_PATH).child ( keyID );
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Post post = (Post) getValue ( ds,Post.class );
                    tasks.add(post);
                }
                iSavable.onDataRead(tasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getPostsByUser(String userID, String teamID, final ISavable iSavable) {
        final ArrayList<String> posts = new ArrayList<>();

        final DatabaseReference mDatabase = getDatabaseReference(ConstantNames.USER_ACTIVITY_PATH).child(teamID).child(userID).child(ConstantNames.DATA_USER_POSTS);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(ds.hasChildren())
                    {
                        Post post = ds.getValue(Post.class);
                        posts.add(post.getKeyID());
                    }
                    else
                    {
                        posts.add(ds.getValue().toString());
                    }
                }
                iSavable.onDataRead(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getPostsByID(final ArrayList<String> postsID, final String teamID, final String userID, final ISavable iSavable)
    {
        final ArrayList<Post> posts = new ArrayList<>();

        final DatabaseReference mDatabase =  getDatabaseReference(ConstantNames.POST_PATH).child ( teamID );
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    for(String id : postsID)
                    {
                        if(ds.getKey().equals(id))
                        {
                            Post post = (Post)getValue(ds,Post.class);
                            posts.add(post);
                            postsID.remove(id);
                            break;
                        }
                    }
                }
                if(postsID.size() > 0)
                {
                    for(String id : postsID)
                    {
                        deleteValue(ConstantNames.USER_ACTIVITY_PATH, teamID, userID, ConstantNames.DATA_USER_POSTS, id, new DataListener() {
                            @Override
                            public void onDataDelete() {

                            }
                        });
                    }
                }
                iSavable.onDataRead(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getRejects(String teamID, String taskID, final ISavable iSavable) {
        final ArrayList<String> usersID = new ArrayList<>();

        final DatabaseReference mDatabase = getDatabaseReference(ConstantNames.TASK_PATH).child(teamID).child(taskID);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(ConstantNames.DATA_USERS_LIST))
                {
                    for (DataSnapshot ds : snapshot.child(ConstantNames.DATA_USERS_LIST).getChildren())
                    {
                        Submitter submitter = (Submitter) getValue(ds,Submitter.class);
                        if(submitter.getTitle() == null || submitter.getConfirmStatus() == Submitter.STATUS_UNSUBMITTED)
                            usersID.add(ds.getKey().toString());
                    }
                    getUsersByKeys(usersID, new ISavable() {
                        @Override
                        public void onDataRead(Object save) {
                            iSavable.onDataRead(save);
                        }
                    });
                }
                else
                {
                    iSavable.onDataRead(null);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
