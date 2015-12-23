package dmitriy.com.musicshop.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import dmitriy.com.musicshop.ListActivity;
import dmitriy.com.musicshop.R;
import dmitriy.com.musicshop.db.ShopsContentProvider;

public class Utils {
    public static void dialPhoneNumber(Activity a, String number) {
        String phoneNumber = number.replaceAll("[^\\d]","");
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        if (intent.resolveActivity(a.getPackageManager()) != null) {
            try {
                a.startActivity(Intent.createChooser(intent, a.getResources().getString(R.string.dialPhone)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void visitWebite(Activity a, String website) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(website));

        if (intent.resolveActivity(a.getPackageManager()) != null) {
            try {
                a.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendMail(FragmentActivity a, String emailAddress) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { emailAddress} );
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        if (intent.resolveActivity(a.getPackageManager()) != null) {
            try {
                a.startActivity(Intent.createChooser(intent, a.getResources().getString(R.string.sendMail)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeAllRows(Activity activity) {
        ContentResolver contentResolver = activity.getContentResolver();
        int deletedRows = contentResolver.delete(ShopsContentProvider.REMOVESHOPS_URI, null, null);
        Log.i(activity.getClass().getSimpleName(), "Rows deleted: " + deletedRows);
        deletedRows = contentResolver.delete(ShopsContentProvider.REMOVEINSTRUMENTS_URI, null, null);
        Log.i(activity.getClass().getSimpleName(), "Rows deleted: " + deletedRows);

    }

    public static double decodeLocation(long location) {
        int length = (int) (Math.log10(location) + 1) - 2;
        return location / (Math.pow(10, length));
    }
}
