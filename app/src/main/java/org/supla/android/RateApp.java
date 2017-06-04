package org.supla.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import org.supla.android.db.DbHelper;

import java.util.Date;


/*
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 Author: Przemyslaw Zygmunt przemek@supla.org
 */

public class RateApp {

    private final String PN_RATE_TIME = "rate_time";
    private Context context;

    private void moreTime(int days) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Date now = new Date();
        long rt = new Date(now.getTime() + (days * 0x5265C00)).getTime();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PN_RATE_TIME, rt);
        editor.commit();

    }


    RateApp(Context context) {
        this.context = context;
    }

    private void ShowAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.rate_msg);
        builder.setPositiveButton(R.string.rate_now,

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.supla.android")));

                        moreTime(36500);
                        dialog.cancel();

                    }
                });

        builder.setNeutralButton(R.string.rate_later,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        moreTime(6);
                        dialog.cancel();

                    }
                });

        builder.setNegativeButton(R.string.rate_no_thanks,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        moreTime(36500);
                        dialog.cancel();

                    }
                });

        AlertDialog alert = builder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();
    }

    boolean showDialog(int delay) {

        boolean result = false;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long rt = prefs.getLong(PN_RATE_TIME, Long.valueOf(0));

        Date now = new Date();

        if ( rt == 0 ) {

            moreTime(1);

        } else if ( now.getTime() >= rt ) {

            DbHelper DbH = new DbHelper(context);
            if ( DbH.getChannelCount() > 0 ) {

                moreTime(1);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        ShowAlertDialog();

                    }
                }, delay);

                result = true;

            }


        }


        return result;


    }


}


