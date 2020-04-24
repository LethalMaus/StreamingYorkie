package com.lethalmaus.streaming_yorkie.file;

import android.app.Activity;
import android.content.Context;

import com.lethalmaus.streaming_yorkie.Globals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class to read files as list within a folder or a file itself
 * @author LethalMaus
 */
public class ReadFileHandler {

    //All contexts are weak referenced to avoid memory leaks
    private WeakReference<Activity> weakActivity;
    private WeakReference<Context> weakContext;
    private String appDirectory;
    private String filename;

    /**
     * Constructor for Read File Handler, filename can be null
     * @author LethalMaus
     * @param weakActivity weak reference activity
     * @param weakContext weak reference context
     * @param filename name of file to be read
     */
    public ReadFileHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, String filename) {
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        if (weakContext != null && weakContext.get() != null) {
            this.appDirectory = weakContext.get().getFilesDir().toString();
        }
        this.filename = filename;
    }

    /**
     * Method to read a files content
     * @author LethalMaus
     * @return File content or empty string
     */
    public String readFile() {
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            File file = new File(appDirectory + File.separator + filename);
            if (file.exists() && !file.isDirectory()) {
                fileInputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Appends new lines to error, supporter & subscriber files
                String newLine = "";
                if (filename.contains(Globals.FILE_ERROR) || filename.contains(Globals.FILE_SUPPORTER) || filename.contains(Globals.FILE_SUBSCRIBER)) {
                    newLine = "\n";
                }
                String temp;
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp);
                    stringBuilder.append(newLine);
                }
                return stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null, "Error finding file '" + filename + "' | " + e.toString(), true).run();
        } catch (IOException e) {
            new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null, "Errors reading from file '" + filename + "' | " + e.toString(), true).run();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null, "Error closing file '" + filename + "' | " + e.toString(), true).run();
            }
        }
        return "";
    }

    /**
     * Method for reading file names within directory
     * @author LethalMaus
     * @return List of files
     */
    ArrayList<String> readFileNames() {
        ArrayList<String> files = new ArrayList<>();
        String[] fileArray = new File(appDirectory + File.separator + filename).list();
        if (fileArray != null && fileArray.length > 0) {
            files = new ArrayList<>(Arrays.asList(fileArray));
        }
        return files;
    }

    /**
     * Method for counting files within a directory
     * @author LethalMaus
     * @return file amount or 0
     */
    public int countFiles() {
        String[] files = new File(appDirectory + File.separator + filename).list();
        if (files != null) {
            return files.length;
        }
        return 0;
    }
}
