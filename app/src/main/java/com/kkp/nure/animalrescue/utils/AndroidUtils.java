package com.kkp.nure.animalrescue.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.StringRes;

import com.kkp.nure.animalrescue.activities.LoginActivity;

import java.text.DateFormat;

public class AndroidUtils {
    public static final DateFormat DATE_FMT = DateFormat.getDateTimeInstance();

    public static String getTokenOrDie(Activity ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("authToken", null);
        if(token == null) {
            ctx.startActivity(new Intent(ctx, LoginActivity.class));
            ctx.finish();
            return null;
        }

        return token;
    }

    public static Long getLongExtraOrDie(Activity ctx, String name) {
        Intent intent = ctx.getIntent();
        long result = intent.getLongExtra(name, -1);
        if(result == -1) {
            ctx.finish();
            return null;
        }

        return result;
    }
}
