package com.dugan.settingsplus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.text.InputType;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Todd on 1/4/2015.
 */
public class ProfileCursorAdapter extends CursorAdapter {

    private SparseBooleanArray mSelectedItemsIds;

    public ProfileCursorAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_list_row, null);
        ViewHolder holder = new ViewHolder();
        holder.titleView = (RelativeLayout) view.findViewById(R.id.profTitleView);
        holder.contentView = (RelativeLayout) view.findViewById(R.id.profContentView);
        holder.titleName = (TextView) view.findViewById(R.id.profTitleName);
        holder.autoBright = (Switch) view.findViewById(R.id.profAutoBright);
        holder.contentShow = (ImageView) view.findViewById(R.id.profContentShow);
        holder.manBrightView = (RelativeLayout) view.findViewById(R.id.profManBrightView);
        holder.manBrightSeek = (SeekBar) view.findViewById(R.id.profManBrightSeek);
        holder.manBrightPercent = (TextView) view.findViewById(R.id.profManBrightPercent);
        holder.ringtone = (TextView) view.findViewById(R.id.profRingText);
        holder.notifTone = (TextView) view.findViewById(R.id.profAlertText);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final View convertView, final Context context, final Cursor cursor) {
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final MySQLHelper db = new MySQLHelper(context);
        final int id = cursor.getInt(cursor.getColumnIndex(MySQLHelper.PROFILE_ID));
        Boolean bolAutoBright = true;
        if(cursor.getString(cursor.getColumnIndex(MySQLHelper.PROFILE_AUTO_BRIGHT)).equals("N")){
            bolAutoBright = false;
            holder.manBrightView.setVisibility(View.VISIBLE);
        }
        holder.autoBright.setChecked(bolAutoBright);
        holder.autoBright.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    CustomAnimations.expand(context, holder.manBrightView);
                } else {
                    CustomAnimations.collapse(context, holder.manBrightView);
                }
                db.upProfAutoBright(isChecked, id);
            }
        });
        holder.manBrightPercent.setText(cursor.getInt(cursor.getColumnIndex(MySQLHelper.PROFILE_MANUAL_BRIGHT)) + "%");
        holder.manBrightSeek.setProgress(cursor.getInt(cursor.getColumnIndex(MySQLHelper.PROFILE_MANUAL_BRIGHT)));
        holder.manBrightSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int intProgress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                intProgress = progress;
                holder.manBrightPercent.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                db.upProfManBright(intProgress, id);
                ProfileFragment.refreshProfCursor(context);
            }
        });
        holder.ringtone.setText(cursor.getString(cursor.getColumnIndex(MySQLHelper.PROFILE_RINGTONE)));
        holder.ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRingtonePicker(context, id);
            }
        });
        holder.notifTone.setText(cursor.getString(cursor.getColumnIndex(MySQLHelper.PROFILE_NOTIFICATION_TONE)));
        holder.notifTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertTonePicker(context, id);
            }
        });
        holder.titleName.setText(cursor.getString(cursor.getColumnIndex(MySQLHelper.PROFILE_NAME)));
        holder.titleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Rename Profile");
                alert.setMessage("Enter New Name");
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
                            db.upProfName(value, id);
                            ProfileFragment.refreshProfCursor(context);
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
        });
        holder.contentShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.contentView.getVisibility() == View.VISIBLE) {
                    CustomAnimations.collapse(context, holder.contentView);
                    CustomAnimations.rotate180(context, holder.contentShow, "p");
                    db.upProfExpandInd("N", id);
                } else {
                    CustomAnimations.expand(context, holder.contentView);
                    CustomAnimations.rotate180(context, holder.contentShow, "p");
                    db.upProfExpandInd("Y", id);
                }
            }
        });

        if(cursor.getString(cursor.getColumnIndex(MySQLHelper.PROFILE_EXPAND_IND)).equals("Y")){
            holder.contentView.setVisibility(View.VISIBLE);
            holder.contentShow.setRotation(180);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return IGNORE_ITEM_VIEW_TYPE;
    }

    private void showRingtonePicker(Context context, int id){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        ((Activity) context).startActivityForResult(intent, id);
    }

    private void showAlertTonePicker(Context context, int id){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        ((Activity) context).startActivityForResult(intent, id);
    }

    static class ViewHolder{
        RelativeLayout contentView;
        RelativeLayout titleView;
        TextView titleName;
        ImageView contentShow;
        Switch autoBright;
        RelativeLayout manBrightView;
        SeekBar manBrightSeek;
        TextView manBrightPercent;
        TextView ringtone;
        TextView notifTone;
    }
}
