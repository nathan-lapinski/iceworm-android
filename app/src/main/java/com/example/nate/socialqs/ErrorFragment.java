package com.example.nate.socialqs;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by nate on 10/11/15.
 */
public class ErrorFragment extends DialogFragment /*implements TextView.OnEditorActionListener*/ {
    private EditText mEditText;


    public interface UserNameListener {
        void onFinishUserDialog(String user);
    }

    // Empty constructor required for DialogFragment
    public ErrorFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialoginfo, container);
       // mEditText = (EditText) view.findViewById(R.id.username);

        // set this instance as callback for editor action
       // mEditText.setOnEditorActionListener(this);
       // mEditText.requestFocus();
        //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getDialog().setTitle("There has been an accident...");

        Button closeDialog = (Button) view.findViewById(R.id.closeButton);
        closeDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDialog().dismiss();
                        Intent intent = new Intent(getActivity(), AskQuestionActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
        return view;
    }

    /*
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // Return input text to activity
        UserNameListener activity = (UserNameListener) getActivity();
        activity.onFinishUserDialog(mEditText.getText().toString());
        this.dismiss();
        return true;
    }*/
}
