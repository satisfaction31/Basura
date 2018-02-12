package officialsuzuen.google.com.trasearch.Profile;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;
import officialsuzuen.google.com.trasearch.Camera.CameraActivity;
import officialsuzuen.google.com.trasearch.Models.User;
import officialsuzuen.google.com.trasearch.Models.UserAccountSettings;
import officialsuzuen.google.com.trasearch.Models.UserSettings;
import officialsuzuen.google.com.trasearch.R;
import officialsuzuen.google.com.trasearch.Utils.ConfirmPasswordDialog;
import officialsuzuen.google.com.trasearch.Utils.FirebaseMethods;
import officialsuzuen.google.com.trasearch.Utils.UniversalImageLoader;

/**
 * Created by Edward on 08/02/2018.
 */

public class EditProfileFragment extends AppCompatActivity implements
        ConfirmPasswordDialog.OnConfirmPasswordListener {

    private static final String TAG = "EditProfileFragment";

    //firebase
    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private static FirebaseMethods mFirebaseMethods;

    //EditProfile Fragment widgets
    private static EditText mDisplayName, mUsername, mDescription, mEmail;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;
    private static Context mContext;
    String userID;

    //vars
    private UserSettings mUserSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_editprofile);

        mContext = EditProfileFragment.this;
        //widgets
        mProfilePhoto = (CircleImageView) findViewById(R.id.profile_photo);
        mDisplayName = (EditText) findViewById(R.id.display_name);
        mUsername = (EditText) findViewById(R.id.username);
        mDescription = (EditText) findViewById(R.id.description);
        mEmail = (EditText) findViewById(R.id.email);
        mChangeProfilePhoto = (TextView) findViewById(R.id.changeProfilePhoto);
        mFirebaseMethods = new FirebaseMethods(mContext);

        setupFirebaseAuth();
//        setProfileImage();

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: edit profilee baccccccccccckkkkkk!");
                finish();
            }
        });
        ImageView checkmark = (ImageView) findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               saveProfileSettings();
            }
        });
        getIncomingIntent();

    }
//    private void initImgLoader(){
//        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
//        ImageLoader.getInstance().init(universalImageLoader.getConfig());
//    }
//    private void setProfileImage(){
//        String defaultPic = String.valueOf(R.drawable.ic_android);
//        UniversalImageLoader.setImage(defaultPic, mProfilePhoto, null, "");
//    }

    //GIKUHA ANG DATA SA FIREBASE THEN GE GAMIT TO DISPLAY
    private void setProfileWidgets(UserSettings userSettings) {
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());
        mUserSettings = userSettings;
        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");

        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mEmail.setText(userSettings.getUser().getEmail());
        mDescription.setText(settings.getDescription());
       // mProgressBar.setVisibility(View.GONE);

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(mContext, CameraActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                mContext.startActivity(intent);
                finish();
            }
        });
    }
    //KUHAON ANG INPUT! THEN E COMPARE SA  DATABAse IF NI EXIST OR WALA
    private void saveProfileSettings(){

        final String holdisplayName = mDisplayName.getText().toString();
        final String holdUsername = mUsername.getText().toString();
        final String holdDescription = mDescription.getText().toString();
        final String holdEmail = mEmail.getText().toString();

        //case1: if the user made a change to their username
        if(!mUserSettings.getUser().getUsername().equals(holdUsername)){

            checkIfUsernameExists(holdUsername);
        }
        //case2: if the user made a change to their email
        if(!mUserSettings.getUser().getEmail().equals(holdEmail)){

            // step1) Reauthenticate
            //          -Confirm the password and email
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                ConfirmPasswordDialog moodDialogFragment = new ConfirmPasswordDialog();
                moodDialogFragment.show(fm, "ConfirmPasswordDialog");


            // step2) check if the email already is registered
            //          -'fetchProvidersForEmail(String email)'
            // step3) change the email
            //          -submit the new email to the database and authentication
        }

        /**
         * change the rest of the settings that do not require uniqueness
         */
        if(!mUserSettings.getSettings().getDisplay_name().equals(holdisplayName)){
            //update displayname
            mFirebaseMethods.updateUserAccountSettings(holdisplayName, null, null, 0);
        }
        if(!mUserSettings.getSettings().getDescription().equals(holdDescription)){
            //update description
            mFirebaseMethods.updateUserAccountSettings(null, null, holdDescription, 0);
        }
    }
    /**
     * Check is @param username already exists in teh database
     * @param username
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Checking if  " + username + " already exists.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    //add the username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(mContext, "saved username.", Toast.LENGTH_SHORT).show();

                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(mContext, "That username already exists.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getIncomingIntent() {
        Intent intent = getIntent();

        if (intent.hasExtra(getString(R.string.selected_image))
                || intent.hasExtra(getString(R.string.selected_bitmap))) {

            //if there is an imageUrl attached as an extra, then it was chosen from the gallery/photo fragment
            Log.d(TAG, "getIncomingIntent: New incoming imgUrl");
            if (intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile_fragment))) {

                if (intent.hasExtra(getString(R.string.selected_image))) {
                    //set the new profile picture
                    FirebaseMethods firebaseMethods = new FirebaseMethods(EditProfileFragment.this);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                            intent.getStringExtra(getString(R.string.selected_image)), null);
                } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
                    //set the new profile picture
                    FirebaseMethods firebaseMethods = new FirebaseMethods(EditProfileFragment.this);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                            null, (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)));
                }

            }

        }
    }
    //----------------- F I R E B A S E -------------------
    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieve user information from the database
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));

                //retrieve images for the user in question

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConfirmPassword(String password) {
        Log.d(TAG, "onConfirmPassword: got the password: " + password);

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

        ///////////////////// Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "User re-authenticated.");

                            ///////////////////////check to see if the email is not already present in the database
                            mAuth.fetchProvidersForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    if(task.isSuccessful()){
                                        try{
                                            if(task.getResult().getProviders().size() == 1){
                                                Log.d(TAG, "onComplete: that email is already in use.");
                                                Toast.makeText(mContext, "That email is already in use", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Log.d(TAG, "onComplete: That email is available.");

                                                //////////////////////the email is available so update it
                                                mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "User email address updated.");
                                                                    Toast.makeText(mContext, "email updated", Toast.LENGTH_SHORT).show();
                                                                    mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                                }
                                                            }
                                                        });
                                            }
                                        }catch (NullPointerException e){
                                            Log.e(TAG, "onComplete: NullPointerException: "  +e.getMessage() );
                                        }
                                    }
                                }
                            });
                         }else{
                            Log.d(TAG, "onComplete: re-authentication failed.");
                        }

                    }
                });
    }
}
