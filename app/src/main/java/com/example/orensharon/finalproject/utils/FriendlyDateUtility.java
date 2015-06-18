package com.example.orensharon.finalproject.utils;

import android.content.Context;

import com.example.orensharon.finalproject.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by orensharon on 6/18/15.
 */
public class FriendlyDateUtility {


        static String formatDate(String dateString) {
            Date date = getDateFromDb(dateString);
            return DateFormat.getDateInstance().format(date);
        }

        // Format used for storing dates in the database.  ALso used for converting those strings
        // back into date objects for comparison/processing.
        public static final String DATE_FORMAT = "yyyyMMdd";

        /**
         * Helper method to convert the database representation of the date into something to display
         * to users.  As classy and polished a user experience as "20140102" is, we can do better.
         *
         * @param context Context to use for resource localization
         * @param dateStr The db formatted date string, expected to be of the form specified
         *                in Utility.DATE_FORMAT
         * @return a user-friendly representation of the date.
         */
        public static String getFriendlyDayString(Context context, String dateStr) {
            // The day string for forecast uses the following logic:
            // For today: "Today, June 8"
            // For tomorrow:  "Tomorrow"
            // For the next 5 days: "Wednesday" (just the day name)
            // For all days after that: "Mon Jun 8"

            Date todayDate = new Date();
            String todayStr = getDbDateString(todayDate);
            Date inputDate = getDateFromDb(dateStr);

            // If the date we're building the String for is today's date, the format
            // is "Today, June 24"
            if (todayStr.equals(dateStr)) {
                String today = "Today";
                String format = context.getResources().getString(R.string.format_full_friendly_date);
                return String.format(format, today,getFormattedMonthDay(context, dateStr));
                //return String.format(context.getString(
                //        formatId,
                //        today,
                 //       getFormattedMonthDay(context, dateStr)));
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(todayDate);
                cal.add(Calendar.DATE, 7);
                String weekFutureString = getDbDateString(cal.getTime());

                if (dateStr.compareTo(weekFutureString) < 0) {
                    // If the input date is less than a week in the future, just return the day name.
                    return getDayName(context, dateStr);
                } else {
                    // Otherwise, use the form "Mon Jun 3"
                    SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                    return shortenedDateFormat.format(inputDate);
                }
            }
        }

        /**
         * Given a day, returns just the name to use for that day.
         * E.g "today", "tomorrow", "wednesday".
         *
         * @param context Context to use for resource localization
         * @param dateStr The db formatted date string, expected to be of the form specified
         *                in Utility.DATE_FORMAT
         * @return
         */
        public static String getDayName(Context context, String dateStr) {
            SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
            try {
                Date inputDate = dbDateFormat.parse(dateStr);
                Date todayDate = new Date();
                // If the date is today, return the localized version of "Today" instead of the actual
                // day name.
                if (getDbDateString(todayDate).equals(dateStr)) {
                    return "Today";
                } else {
                    // If the date is set for tomorrow, the format is "Tomorrow".
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(todayDate);
                    cal.add(Calendar.DATE, 1);
                    Date tomorrowDate = cal.getTime();
                    if (getDbDateString(tomorrowDate).equals(
                            dateStr)) {
                        return "Tomorrow";
                    } else {
                        // Otherwise, the format is just the day of the week (e.g "Wednesday".
                        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                        return dayFormat.format(inputDate);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                // It couldn't process the date correctly.
                return "";
            }
        }

        /**
         * Converts db date format to the format "Month day", e.g "June 24".
         * @param context Context to use for resource localization
         * @param dateStr The db formatted date string, expected to be of the form specified
         *                in Utility.DATE_FORMAT
         * @return The day in the form of a string formatted "December 6"
         */
        public static String getFormattedMonthDay(Context context, String dateStr) {
            SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
            try {
                Date inputDate = dbDateFormat.parse(dateStr);
                SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
                String monthDayString = monthDayFormat.format(inputDate);
                return monthDayString;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Converts a dateText to a long Unix time representation
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }

}
