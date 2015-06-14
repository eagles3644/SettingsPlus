package com.dugan.settingsplus;

/**
 * Created by Todd on 12/13/2014.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class ProfileFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static ProfileCursorAdapter profileCursorAdapter;
    private static Cursor profileCursor;
    private static ListView profileListView;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        ImageButton addButton = (ImageButton) rootView.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewProfile(v.getContext());
            }
        });
        profileListView = (ListView) rootView.findViewById(R.id.profListView);
        new Handler().post(new Runnable(){
            @Override
            public void run(){
                MySQLHelper db = new MySQLHelper(rootView.getContext());
                profileCursor = db.getProfiles();
                profileCursorAdapter = new ProfileCursorAdapter(
                    rootView.getContext(),
                        profileCursor,
                        0);
                profileListView.setAdapter(profileCursorAdapter);
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void createNewProfile(final Context context){
        final MySQLHelper db = new MySQLHelper(context);
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Create Profile");
        alert.setMessage("Enter New Profile Name");
        final EditText input = new EditText(context);
        input.setWidth(20);
        input.setTextColor(Color.BLACK);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (hasFocus) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                } else {
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                }
            }
        });
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                String value = input.getText().toString();
                Boolean exists = db.profileExists(value);
                if (value.isEmpty()) {
                    Toast.makeText(context, "Profile name cannot be blank.", Toast.LENGTH_LONG).show();
                } else if (exists) {
                    Toast.makeText(context, "Profile titled '" + value + "' already exists.", Toast.LENGTH_LONG).show();
                } else {
                    db.addProfile(value);
                    refreshProfCursor(context);
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });
        alert.setCancelable(false);
        alert.show();
        input.requestFocus();
    }

    public static void refreshProfCursor(final Context context){
        new Handler().post(new Runnable(){
            @Override
            public void run(){
                MySQLHelper db = new MySQLHelper(context);
                Cursor newCursor = db.getProfiles();
                profileCursorAdapter.swapCursor(newCursor);
            }
        });
    }
}