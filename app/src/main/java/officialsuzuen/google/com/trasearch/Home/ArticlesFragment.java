package officialsuzuen.google.com.trasearch.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import officialsuzuen.google.com.trasearch.R;

/**
 * Created by Del Mar on 2/7/2018.
 */

public class ArticlesFragment extends Fragment {
    private static final String TAG = "ArticlesFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_articles, container, false);

        return view;
    }
}
