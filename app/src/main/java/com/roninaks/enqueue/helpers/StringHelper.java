package com.roninaks.enqueue.helpers;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.roninaks.enqueue.models.UserModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * String formatting helper
 */

public class StringHelper {
    public static String SHARED_PREFERENCE_USER_ID = "id";
    public static String SHARED_PREFERENCE_LAST_QUEUE_ID = "last_queue_id";
    public static String SHARED_PREFRENCE_SERVICE_REFRESH = "service_refresh";
    public static String SHARED_PREFRENCE_SERVICE_REFRESH_TIMEOUT = "service_refresh_timeout";
    public static String SHARED_PREFERENCE_KEY = "enqueue";


    /***
     * Formats Integers. Eg: 20,000 to 20k. 43,744 to 43.74k
     * @param count - The integer to format
     * @return - Returns the formatted string
     */
    public static String formatTextCount(int count) throws Exception {
        String formattedCount = "";
        if (count >= 10000) {
            if (count % 1000 == 0) {
                formattedCount = "" + count / 1000 + "k";
            } else {
                double temp = ((double) count) / 1000.0;
                formattedCount = "" + (Math.round(temp * 100.0) % 100 == 0 ? Math.round(temp) : Math.round(temp * 100.0) / 100.0) + "k";
            }
        } else
            formattedCount = "" + count;
        return formattedCount;
    }

    /***
     * Converts string to Sentence case
     * @param s- String to be converted
     * @return Sentence cased string
     */
    public static String toSentenceCase(String s) {
        if (s != null && !s.isEmpty()) {
            int length = s.length();
            String temp = "";
            int position = 0;
            for (int i = 0; i < length; i++) {
                if (s.charAt(i) == '.') {
                    temp = temp.concat(s.substring(position, position + 1).toUpperCase() + s.substring(position + 1, i + 1));
                    position = i + 1;
                }
            }
            if (position < length)
                temp = temp.concat(s.substring(position, position + 1).toUpperCase() + (position >= length - 1 ? "" : s.substring(position + 1)));
            return temp;
        } else
            return "";
    }

    /***
     * Converts string to Title case
     * @param s- String to be converted
     * @return Title cased string
     */
    public static String toTitleCase(String s) {
        if (s != null && !s.isEmpty()) {
            int length = s.length();
            String str = "";
            String strTemp = "";
            for (int i = 0; i < length; i++) {
                char ch = s.charAt(i);
                if (ch == ' ') {
                    strTemp += ch;
                    str = str.concat(toSentenceCase(strTemp));
                    strTemp = "";
                } else {
                    strTemp += ch;
                }
            }
            str = str.concat(toSentenceCase(strTemp));
            return str;
        } else
            return "";
    }

    /***
     * Changes toolbar font
     * @param toolbar
     * @param context
     * @throws Exception
     */
    public static void changeToolbarFont(Toolbar toolbar, Activity context) throws Exception {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    applyFont(tv, context);
                    break;
                }
            }
        }
    }


    public static void applyFont(TextView tv, Activity context) throws Exception {
        tv.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Semibold.otf"));
    }

    /***
     * Convert waiting time to human readable form
     * @param avgWaitingTime - Waiting time in seconds
     * @return
     */
    public static String setAvgWaitingTimeFormatted(long avgWaitingTime) {
        String time = "";
        time = time + (avgWaitingTime / (60 * 1000)) + ":" + new DecimalFormat("00").format(avgWaitingTime % (60));
        return time;
    }

    /***
     * Generates a token number using the service name and token number
     * @param serviceName - Name of service
     * @param tokenNumber - Token number
     * @return - Alpha numeric token number
     */
    public static String generateTokenNumber(String serviceName, int tokenNumber) {
        if (serviceName != null && !serviceName.isEmpty()) {
            return serviceName.toUpperCase().charAt(0) + String.valueOf(tokenNumber);
        } else
            return String.valueOf(tokenNumber);
    }

    /***
     * Format date to specified format
     * @param datetime
     * @param format
     * @return
     */
    public static Date getDate(String datetime, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        try {
            Date date = formatter.parse(datetime);
            return date;
        } catch (Exception e) {

        }
        try {
            return formatter.parse(formatter.format(Calendar.getInstance().getTime()));
        } catch (ParseException e) {

        }
        return new Date();
    }

    /***
     * Get today's date in default format
     * @return - Date String in default format
     */
    public static String getToday() {
        return getToday("yyyy-MM-dd HH:mm:ss");
    }

    /***
     * Get today's date in specified format
     * @param format - Date format string
     * @return Date String in specified format
     */
    public static String getToday(String format) {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(c);
    }

    /***
     * Compares two dates
     * @param date1 Date String 1
     * @param date2 Date String 2
     * @return 0 if date1 = date2 <br> less than 0 if date1 < date2 <br> greater than 0 if date1 > date2
     * @throws ParseException
     * @throws NullPointerException
     */
    public static int dateCompare(String date1, String date2) throws ParseException, NullPointerException{
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date dateObj1 = curFormater.parse(date1);
        Date dateObj2 = curFormater.parse(date2);
        return dateObj1.compareTo(dateObj2);
    }

    /***
     * Returns a well formatted string for Servce Status Info
     * @param status - Input status
     * @return Formatted string
     */
    public static String getServiceStatusInfoText(String status) {
        if (status != null && !status.isEmpty()) {
            switch (status) {
                case "active":
                    return "Service is " + toTitleCase(status);
                case "inactive":
                    return "Service is " + toTitleCase(status);
                case "break":
                    return "Service is on" + toTitleCase(status);
            }
            return toTitleCase(status);
        } else return "";
    }

    /***
     * Round of floating point to specified precision points
     * @param number
     * @param precision
     * @return rounded float
     */
    public static float roundFloat(float number, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (float) Math.round(number * scale) / scale;
    }

    /***
     * Converts exception stacktrace to String object
     * @param e- Exception object
     * @return stacktrace as a string
     */
    public static String convertStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /***
     * Serialize the object
     *
     */
    public static String convertObjectToString(Object obj) {
        String serializedObject = "";
        try {

            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(obj);
            so.flush();
            serializedObject = Base64.encodeToString(bo.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
//            System.out.println(e);
        }
        return serializedObject;
    }

    /***
     * Serialize the object
     *
     */
    public static Object convertStringToObject(String serializedObject) {

        UserModel obj = new UserModel();
        try {
            byte b[] = Base64.decode(serializedObject.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            obj = (UserModel) si.readObject();
        } catch (Exception e) {
//            System.out.println(e);
        }
        return obj;
    }

    public static String convertSaltToString(byte[] salt) {
        int size = salt.length;
        String converted = "";
        for (int i = 0; i < size; i++) {
            if (i == 0)
                converted = converted.concat("" + salt[i]);
            else
                converted = converted.concat("!@" + salt[i]);
        }
        return converted;
    }

    /***
     * Encrypt password using new Salt
     * @param password
     * @return encrypted password String
     * @throws Exception
     */
    public static String encryptPassword(String password) throws Exception {
        byte[] salt = getSalt();
        String encryptedPassword = encryptPassword(password, salt);
        return encryptedPassword;//.concat("!~" + convertSaltToString(salt));
    }

    /***
     * Encrypt password using known salt
     * @param password
     * @param salt
     * @return encrypted password String
     * @throws Exception
     */
    public static String encryptPassword(String password, byte[] salt) throws Exception {
        String encryptedPassword = generateStrongPassword(password, salt);
        return encryptedPassword;
    }

    public static byte[] convertSaltToByte(String saltString) {
        byte[] salt = new byte[16];
        if (!saltString.isEmpty()) {
            String saltStringArray[] = saltString.split("!@");
            for (int i = 0; i < 16; i++) {
                salt[i] = Byte.parseByte(saltStringArray[i]);
            }
        }
        return salt;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }

    private static String generateStrongPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return toHex(salt) + ":" + toHex(hash);
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    /***
     * Generates secure Salt
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

}

