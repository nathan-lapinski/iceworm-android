package com.example.nate.socialqs;

/**
 * Created by nate on 6/27/15.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
public class QuestionAdapter extends ArrayAdapter {
    private LayoutInflater inflater;
    public QuestionAdapter(Activity activity, String[] items){
        super(activity, R.layout.row_question, items);
        inflater = activity.getWindow().getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        return inflater.inflate(R.layout.row_question, parent, false);
    }
}
