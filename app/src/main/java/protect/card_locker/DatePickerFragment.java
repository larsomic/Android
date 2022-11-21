package protect.card_locker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    final Context context;
    final EditText expiryFieldEdit;

    DatePickerFragment(Context context, EditText expiryFieldEdit) {
        this.context = context;
        this.expiryFieldEdit = expiryFieldEdit;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();

        Date date = (Date) expiryFieldEdit.getTag();
        if (date != null) {
            c.setTime(date);
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(getMinDateOfDatePicker());
        return datePickerDialog;
    }

    private long getMinDateOfDatePicker() {
        Calendar minDateCalendar = Calendar.getInstance();
        minDateCalendar.set(1970, 0, 1);
        return minDateCalendar.getTimeInMillis();
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long unixTime = c.getTimeInMillis();

        Date date = new Date(unixTime);

        LoyaltyCardEditActivity.formatExpiryField(context, expiryFieldEdit, date);
    }
}
