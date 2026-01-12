package com.octal.fsm.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Base64Utils;

import javax.swing.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TextUtils {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int LENGTH = 8;
    private static final SecureRandom randomTextOnly = new SecureRandom();
    public static final Random random = new Random();

    private TextUtils() {

    }

    public static String generateRandomString() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int index = randomTextOnly.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }


    public static String extractPathAfterAssets(String url) {
        String keyword = "assets/";
        int startIndex = url.indexOf(keyword);

        if (startIndex != -1) {
            return url.substring(startIndex + keyword.length()); // Extract after "assets/"
        }

        return ""; // Return empty if "assets/" is not found
    }


    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = randomTextOnly.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }

    public static String generate4DigitOTP() {
        int number = random.nextInt(9999);
        return String.format("%04d", number);
    }


    public static String randomUserName() {
        String[] adjectives = {"Happy", "Sunny", "Cool", "Charming", "Lucky", "Awesome", "Brilliant", "Vibrant", "Mysterious", "Magical"};
        String[] nouns = {"Explorer", "Adventurer", "Dreamer", "Star", "Artist", "Ninja", "Wizard", "Journey", "Pioneer", "Guru"};

        String adjective = adjectives[random.nextInt(adjectives.length)];
        String noun = nouns[random.nextInt(nouns.length)];

        return adjective + noun + random.nextInt(100);
    }

    public static long getRemainDays(LocalDateTime futureObject) {
        // Calculate the duration between now and the futureObject
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, futureObject);
        // Extract the number of days from the duration
        long daysRemaining = duration.toDays();
        return daysRemaining;
    }

    public static String convertAndFormat(long value) {
        double result;
        if (value >= 1_000_000_000) {
            result = (double) value / 1_000_000_000;
            return String.format("%.1fB", result);
        } else if (value >= 1_000_000) {
            result = (double) value / 1_000_000;
            return String.format("%.1fM", result);
        } else if (value >= 1_000) {
            result = (double) value / 1_000;
            return String.format("%.1fK", result);
        } else {
            return String.valueOf(value);
        }
    }


    public static String base64Decode(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes);
    }

    public static String base64Encode(String text) {
        return Base64Utils.encodeToString(text.getBytes());
    }


    public static boolean isEmpty(String string) {
        if (null == string) return true;
        return string.length() == 0;
    }

    public static boolean isEmptyWithOutZero(Double value) {
        if (null == value) return true;
        return value < 0;
    }

    public static boolean isEmpty(Long value) {
        if (null == value) return true;
        return value <= 0;
    }

    public static boolean isEmpty(Integer value) {
        if (null == value) return true;
        return value <= 0;
    }

    public static boolean isEmpty(Double value) {
        if (null == value) return true;
        return value <= 0;
    }

    public static String getEncodedPassword(String password) {
        if (isEmpty(password)) {
            return null;
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    public static String generate6DigitNumber() {
        int number = random.nextInt(999999);
        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    public static String formatMoneyAmount(double moneyAmount) {
        DecimalFormat format = new DecimalFormat("##.00");
        return format.format(moneyAmount);
    }

    public static String md5encryption(String text) {
        if (isEmpty(text)) {
            return null;
        }
        String hashtext = null;
        try {
            String plaintext = text;
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            hashtext = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, e1.getClass().getName() + ": " + e1.getMessage());
        }
        return hashtext;
    }

    public static String getRandomPassword() {
        return randomUserName().toLowerCase(Locale.ROOT);
    }

    public static boolean isDateInBetweenIncludingEndPoints(final Date min, final Date max) {
        Calendar calendarMin = Calendar.getInstance();
        calendarMin.setTime(min);
        calendarMin.set(Calendar.DATE, 1);

        Calendar calendarMax = Calendar.getInstance();
        calendarMax.setTime(max);
        calendarMax.set(Calendar.DATE, 1);

        Calendar calendarNow = Calendar.getInstance();
        calendarNow.setTimeInMillis(0L);
        calendarNow.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        calendarNow.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
        calendarNow.set(Calendar.DATE, 1);

        return !(calendarNow.getTime().before(calendarMin.getTime()) || calendarNow.getTime().after(calendarMax.getTime()));
    }

    public static String convertNumberTo10Digit(Long number) {
        return String.format("%010d", number);
    }

    public static String getPaymentRequestTransactionId(Long number) {
        return "TXN-" + convertNumberTo10Digit(number);
    }

    public static boolean isNumber(String data) {
        try {
            Long.parseLong(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String get10CharCouponCode() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString.toUpperCase();
    }

    public static String getFacebookUserImage(String facebookUserId) {
        return "http://graph.facebook.com/" + facebookUserId + "/picture?type=" + "large";
    }

    public static String getStringDate(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }


    public static long getTimeDifference(Date d1, Date d2) {
        // Calculate time difference
        // in milliseconds
        long differenceInTime
                = d2.getTime() - d1.getTime();

        // Calculate time difference in seconds,
        // minutes, hours, years, and days

        // Print the date difference in
        // years, in days, in hours, in
        // minutes, and in seconds
        // Print result

        return TimeUnit
                .MILLISECONDS
                .toDays(differenceInTime)
                % 365;

    }

    public static int calculatePages(int totalCount, int requestLimit) {
        return (int) Math.ceil((double) totalCount / requestLimit);
    }
}
