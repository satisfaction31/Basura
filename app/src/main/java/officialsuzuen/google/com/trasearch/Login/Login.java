package officialsuzuen.google.com.trasearch.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import officialsuzuen.google.com.trasearch.Home.HomeActivity;
import officialsuzuen.google.com.trasearch.R;


/**
 * Created by User on 6/19/2017.
 */

public class Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final Boolean IS_VERIFIED = false;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //vars
    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: started.");

        mProgressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        mEmail = (EditText) findViewById(R.id.loginEt2);
        mPassword = (EditText) findViewById(R.id.loginEt4);
        mContext = Login.this;


        mProgressBar.setVisibility(View.GONE);

        //REDIRECT TO REGISTER WHEN CLICKED!
        Button reg = findViewById(R.id.loginBtn2);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        setupFirebaseAuth();
        initialize();

    }
    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: Setting up firebase auth.");

        //GETTING THE ENTRY OCCURENCE OF AUTHENTICATION
        mAuth = FirebaseAuth.getInstance();
        //CHECKS THE STATE OF USER
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
            }
        };
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
    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking string if null.");

        if(string.equals("")){
            return true;
        }
        else{
            return false;
        }
    }
    private void initialize(){

    Button btnLogin = (Button) findViewById(R.id.loginBtn);
    btnLogin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: Attempting to log in.");

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(isStringNull(email) && isStringNull(password)){
            Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
         }else{
            mProgressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            FirebaseUser user = mAuth.getCurrentUser();

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithEmail:failed", task.getException());

                        Toast.makeText(Login.this, "Authentication Failed",
                                Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    }
                    else{
                        try{
//                            if(IS_VERIFIED){
                                if(user.isEmailVerified()){
                                    Log.d(TAG, "onComplete: Email is verified. SUCCESS!");
                                    Intent intent = new Intent(Login.this, HomeActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(mContext, "Email is not verified \n check your email inbox.", Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                    mAuth.signOut();
                                }
//                            } else{
//                                Log.d(TAG, "onComplete:  Email is verified. SUCCESS!");
//                                Intent intent = new Intent(Login.this, HomeActivity.class);
//                                startActivity(intent);
//                            }

                        }catch (NullPointerException e){
                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                        }
                    }
                }
            });
        }

    }
});

        TextView SignUp = (TextView) findViewById(R.id.loginTx2);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to register screen");
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

   //IF USER ALREADY SIGNED IN THEN IT WILL REDIRECT TO
        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(Login.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
























