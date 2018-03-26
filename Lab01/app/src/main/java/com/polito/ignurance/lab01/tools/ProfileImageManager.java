package com.polito.ignurance.lab01.tools;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileImageManager {

    private static final String filepath = "imageDir";

    public String saveToInternalStorage(Bitmap bitmapImage, String filename, Context applicationContext){
        ContextWrapper contextWrapper = new ContextWrapper(applicationContext);
        File directory = contextWrapper.getDir(filepath, Context.MODE_PRIVATE);
        File imagePath = new File(directory, filename);

        FileOutputStream fileOutputStream = null;
        try{
            fileOutputStream = new FileOutputStream(imagePath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 75, fileOutputStream);
        } catch (Exception e){
            e.getMessage();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.getMessage();
            }
        }

        return directory.getAbsolutePath();
    }

    public Bitmap loadImageFromInternalStorage(String path, String filename){
        try{
            File file = new File(path, filename);
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e){
            e.getMessage();
        }
        return null;
    }

}
