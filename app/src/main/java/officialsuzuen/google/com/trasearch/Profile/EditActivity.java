package officialsuzuen.google.com.trasearch.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import officialsuzuen.google.com.trasearch.R;

/**
 * Created by Del Mar on 2/8/2018.
 */

public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_editprofile);
        Log.d(TAG, "onCreate: started.");
    }
}
