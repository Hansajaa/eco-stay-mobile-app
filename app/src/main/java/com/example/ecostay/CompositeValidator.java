package com.example.ecostay;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import com.google.android.material.datepicker.CalendarConstraints;

/**
 * Composite validator that combines multiple date validators
 * All validators must pass for a date to be valid
 */
public class CompositeValidator implements CalendarConstraints.DateValidator {

    private final CalendarConstraints.DateValidator[] validators;

    public CompositeValidator(CalendarConstraints.DateValidator... validators) {
        this.validators = validators;
    }

    @Override
    public boolean isValid(long date) {
        for (CalendarConstraints.DateValidator validator : validators) {
            if (!validator.isValid(date)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        // Not needed for this implementation
    }
}

