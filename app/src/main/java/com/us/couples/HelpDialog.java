package com.us.couples;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

// the help popup (author, version, how to use the screen)
public final class HelpDialog {
    private HelpDialog() {
    }

    public static void show(Context context, CharSequence screenName, @StringRes int instructionsRes) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_help, null);
        ((TextView) view.findViewById(R.id.help_author))
                .setText(context.getString(R.string.help_author, context.getString(R.string.author_name)));
        ((TextView) view.findViewById(R.id.help_version))
                .setText(context.getString(R.string.help_version, screenName, versionName(context)));
        ((TextView) view.findViewById(R.id.help_instructions)).setText(instructionsRes);

        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.help_title)
                .setView(view)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private static String versionName(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "?";
        }
    }
}
