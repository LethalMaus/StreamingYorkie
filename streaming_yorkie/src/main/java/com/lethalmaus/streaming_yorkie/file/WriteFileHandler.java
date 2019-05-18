package com.lethalmaus.streaming_yorkie.file;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Class for writing to a file or multiple filenames to a folder. Can be used as a runnable
 * @author LethalMaus
 */
public class WriteFileHandler implements Runnable {

    private WeakReference<Context> weakContext;
    private String appDirectory;
    private String fileOrPathName;
    private String data;
    private boolean append;
    private ArrayList<String> files;

    /**
     * Constructor used for setting up WriteFileHandler.
     * @param weakContext weak reference of context where WriteFileHandler was called
     * @param fileOrPathName name of file or path to be written to
     * @param files list of files to be written to a directory given
     * @param data content of a file, can be null
     * @param append bool for appending a file or not
     */
    public WriteFileHandler(WeakReference<Context> weakContext, String fileOrPathName, ArrayList<String> files, String data, boolean append) {
        this.weakContext = weakContext;
        if (weakContext != null && weakContext.get() != null) {
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
        try {
            File file = new File(appDirectory + File.separator + fileOrPathName);
            if (!file.exists()) {
                if (!new File(file.getParent()).exists() && !new File(file.getParent()).mkdirs()) {
                    if (weakContext != null && weakContext.get() != null) {
                        Toast.makeText(weakContext.get(), "Error creating directory '" + fileOrPathName + "'", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println("Error creating directory '" + fileOrPathName + "'");
                    }
                }
                if (!file.createNewFile()) {
                    if (weakContext != null && weakContext.get() != null) {
                        Toast.makeText(weakContext.get(), "Error creating file '" + fileOrPathName + "'", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println("Error creating file '" + fileOrPathName + "'");
                    }
                }
            }
            if (data != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(file, append);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, Charset.forName("UTF-8"));
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStreamWriter.close();
                fileOutputStream.close();
            }
        } catch (FileNotFoundException e) {
            if (weakContext != null && weakContext.get() != null) {
                Toast.makeText(weakContext.get(), "Error retrieving file '" + fileOrPathName + "'", Toast.LENGTH_SHORT).show();
            } else {
                System.out.println("Error retrieving file '" + fileOrPathName + "'");
            }
        } catch (IOException e) {
            if (weakContext != null && weakContext.get() != null) {
                Toast.makeText(weakContext.get(), "Error writing to file '" + fileOrPathName + "'", Toast.LENGTH_SHORT).show();
            } else {
                System.out.println("Error writing to file '" + fileOrPathName + "'");
            }
        }
    }
}
