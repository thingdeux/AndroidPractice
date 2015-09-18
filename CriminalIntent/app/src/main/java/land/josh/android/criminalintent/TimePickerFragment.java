package land.josh.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by josh on 9/7/15.
 */
public class TimePickerFragment extends DialogFragment {
    private static String ARG_DATE = "land.josh.android.criminalintent.timepickerfragment.date";
    public static String EXTRA_HOUR = "land.josh.android.criminalintent.timepickerfragment.extra_hour";
    public static String EXTRA_MINUTE = "land.josh.android.criminalintent.timepickerfragment.extra_minute";
    public static String EXTRA_DATE = "land.josh.android.criminalintent.timepickerfragment.extra_date";
    private Calendar mCalendar;

    private TimePicker mTimePicker;

    // Create a new instance of timePickerFragment passing date as a param
    static TimePickerFragment newInstance(Date date) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();

        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        mCalendar = Calendar.getInstance();
        if (date != null) {
            mCalendar.setTime(date);
        } else {
            mCalendar.setTime(new Date());
        }

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_time_picker);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int hour = mTimePicker.getCurrentHour();
                                int minute = mTimePicker.getCurrentMinute();
                                int month = mCalendar.get(Calendar.DAY_OF_MONTH);
                                int day = mCalendar.get(Calendar.DAY_OF_WEEK);
                                int year = mCalendar.get(Calendar.YEAR);
                                Calendar date = new GregorianCalendar(year, month, day, hour, minute, 0);

                                sendResult(Activity.RESULT_OK, date.getTime());
                            }
                        })
                .create();

    }

}
