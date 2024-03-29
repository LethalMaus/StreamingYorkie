package com.lethalmaus.streaming_yorkie.file;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.lethalmaus.streaming_yorkie.Globals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Class for writing to a file or multiple filenames to a folder. Can be used as a runnable
 * @author LethalMaus
 */
public class WriteFileHandler implements Runnable {

    private WeakReference<Activity> weakActivity;
    private String appDirectory;
    private String fileOrPathName;
    private String data;
    private boolean append;
    private ArrayList<String> files;

    /**
     * Constructor used for setting up WriteFileHandler.
     * @param weakActivity weak reference of activity where WriteFileHandler was called
     * @param weakContext weak reference of context where WriteFileHandler was called
     * @param fileOrPathName name of file or path to be written to
     * @param files list of files to be written to a directory given
     * @param data content of a file, can be null
     * @param append bool for appending a file or not
     */
    public WriteFileHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, String fileOrPathName, ArrayList<String> files, String data, boolean append) {
        this.weakActivity = weakActivity;
        if (Globals.checkWeakReference(weakContext)) {
            this.appDirectory = weakContext.get().getFilesDir().toString();
        }
        this.fileOrPathName = fileOrPathName;
        this.files = files;
        this.data = data;
        this.append = append;
    }

    @Override
    public void run() {
        writeToFileOrPath();
    }

    /**
     * Method that writes files to a directory or writes content to a file, can differentiate between the two. Used in run
     * @author LethalMaus
     */
    public void writeToFileOrPath() {
        if (files == null || files.size() == 0) {
            writeToFile();
        } else {
            String path = fileOrPathName;
            for (int i = 0; i < files.size(); i++) {
                fileOrPathName = path + File.separator + files.get(i);
                writeToFile();
            }
        }
    }

    /**
     * Writes content(if not null) to a file, otherwise it writes an empty file
     * @author LethalMaus
     */
    private void writeToFile() {
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            File file = new File(appDirectory + File.separator + fileOrPathName);
            if (!file.exists()) {
                if (file.getParent() != null && !new File(file.getParent()).exists() && !new File(file.getParent()).mkdirs()) {
                    if (Globals.checkWeakActivity(weakActivity)) {
                        Toast.makeText(weakActivity.get(), "Error creating directories for '" + fileOrPathName + "'", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println("Error creating directory '" + fileOrPathName + "'");
                    }
                }
                if (!file.createNewFile()) {
                    if (Globals.checkWeakActivity(weakActivity)) {
                        Toast.makeText(weakActivity.get(), "Error creating file '" + fileOrPathName + "'", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println("Error creating file '" + fileOrPathName + "'");
                    }
                }
            }
            if (data != null) {
                fileOutputStream = new FileOutputStream(file, append);
                outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write(data);
                if (append) {
                    bufferedWriter.newLine();
                }
                bufferedWriter.flush();
            }
        } catch (FileNotFoundException e) {
            if (Globals.checkWeakActivity(weakActivity)) {
                Toast.makeText(weakActivity.get(), "Error retrieving file '" + fileOrPathName + "'", Toast.LENGTH_SHORT).show();
            } else {
                System.out.println("Error retrieving file '" + fileOrPathName + "'");
            }
        } catch (IOException e) {
            if (Globals.checkWeakActivity(weakActivity)) {
                Toast.makeText(weakActivity.get(), "Error writing to file '" + fileOrPathName + "'", Toast.LENGTH_SHORT).show();
            } else {
                System.out.println("Error writing to file '" + fileOrPathName + "'");
            }
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                System.out.println("Error writing to file '" + fileOrPathName + "'");
            }
        }
    }
}
