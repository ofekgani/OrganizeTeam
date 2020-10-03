package com.example.organizeteam.AuthorizationSystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.example.organizeteam.Core.ConstantNames;
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

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class UserInfo
{
    private String getUserInfo(DataSnapshot ds, String key) {
        return ds.child ( key ).getValue ().toString ();
    }

    public void getUserInformation(final IUser iUser)
    {
        final Map<String,Object> userInfo = new HashMap<> (  );
        final String email = FirebaseAuth.getInstance ().getCurrentUser().getEmail ();
        DatabaseReference mDatabase =  FirebaseDatabase.getInstance ().getReference (ConstantNames.USER_PATH );

        mDatabase.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren ())
                {
                    if(ds.child ( "email" ).getValue ().equals ( email )){
                        userInfo.put ( ConstantNames.USER_NAME,(String)getUserInfo ( ds, ConstantNames.DATA_USER_NAME ) );
                        userInfo.put ( ConstantNames.USER_EMAIL,(String)getUserInfo ( ds,ConstantNames.DATA_USER_EMAIL ) );
                        userInfo.put ( ConstantNames.USER_KEY_ID,(String)getUserInfo ( ds,ConstantNames.DATA_USER_ID ) );
                        userInfo.put ( ConstantNames.USER_LOGO,(String)getUserInfo ( ds, ConstantNames.DATA_USER_LOGO ) );
                        iUser.onDataRead ( userInfo );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    public void setUserInfo(String userID,String key, String value) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance ().getReference ( ConstantNames.USER_PATH ).child ( userID );
        mDatabase.child ( key ).setValue ( value );

    }

    public void uploadPicture(Uri image, final Context context, final String userID, String where)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance ();
        StorageReference storageReference = storage.getReference ();

        final ProgressDialog pd = new ProgressDialog ( context );
        pd.setCanceledOnTouchOutside(false);
        pd.setTitle ( "Upload Image..." );
        pd.show ();

        final StorageReference riversRef = storageReference.child("userLogo/"+where);

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
                                setUserInfo ( userID,ConstantNames.DATA_USER_LOGO,image );
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
