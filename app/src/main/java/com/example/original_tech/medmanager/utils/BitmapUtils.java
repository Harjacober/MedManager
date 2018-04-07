package com.example.original_tech.medmanager.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by Original-Tech on 4/4/2018.
 */

public class BitmapUtils {
    public static Bitmap getThumbnail(Uri uri, Context context) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 96) ? (originalSize / 96) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }

    public static Bitmap getIntentimage(ImageView imageView){
        imageView.buildDrawingCache();
        Bitmap imageBitmap=imageView.getDrawingCache();
        return imageBitmap;
    }

    public static String saveImageToSDCard(Bitmap bitmap){

        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CFI" + "/Images";
        String fileName;
        try {
            File dirs = new File(filePath);
            if (!dirs.exists()){
                dirs.mkdirs();
            }
            File file = new File(filePath, generateRandomName() + ".jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            fileName = filePath + "/" + file.getName();
            return fileName;
        }catch (IOException e){
            Log.i("mmmmmm", e.toString());
            return null;
        }
    }

    private static String generateRandomName(){
        Random random = new Random();
        StringBuilder name = new StringBuilder();
        for (int i =0; i < 10; i++){
            int numbers = random.nextInt(9);
            name.append(numbers);
        }
        return name.toString();
    }


}
