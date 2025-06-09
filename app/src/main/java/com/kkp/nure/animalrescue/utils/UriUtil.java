package com.kkp.nure.animalrescue.utils;

import android.content.Context;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class UriUtil {
    public static byte[] readUriToByteArray(Context context, Uri uri) {
        final InputStream inputStream;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            return null;
        }

        if(inputStream == null) {
            return null;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try(inputStream) {
            int read;
            byte[] buf = new byte[16384];

            while ((read = inputStream.read(buf, 0, buf.length)) != -1) {
                buffer.write(buf, 0, read);
            }
        } catch (IOException e) {
            return null;
        }

        return buffer.toByteArray();
    }
}
