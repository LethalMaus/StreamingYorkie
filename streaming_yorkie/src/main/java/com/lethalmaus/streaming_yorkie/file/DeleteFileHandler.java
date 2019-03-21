package com.lethalmaus.streaming_yorkie.file;

import android.content.Context;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Class to delete files, can be used as a runnable
 * @author LethalMaus
 */
public class DeleteFileHandler implements Runnable {

    //All contexts are weak referenced to avoid memory leaks
    private WeakReference<Context> weakContext;
    private String appDirectory;
    private String pathOrFileName;

    /**
     * Constructor to setup the handler. Needs a path or file (which is within the Apps directory) & a weak reference for Toasts to inform the user
     * @author LethalMaus
     * @param weakContext weak reference of the context which called this constructor
     * @param pathOrFileName path or file within apps directory
     */
    public DeleteFileHandler(WeakReference<Context> weakContext, String pathOrFileName) {
        this.weakContext = weakContext;
        if (weakContext != null && weakContext.get() != null) {
            this.appDirectory = weakContext.get().getFilesDir().toString();
        }
        this.pathOrFileName = pathOrFileName;
    }

    /**
     * This method can be used as a runnable for 'fire & forget' or for sync processes.
     * It deletes single files or complete directories
     * @author LethalMaus
     * @param pathOrFileName path or file within apps directory
     */
    public void deleteFileOrPath(String pathOrFileName) {
        File pathOrFile = new File(appDirectory + File.separator + pathOrFileName);
        if (pathOrFile.exists()) {
            if (pathOrFile.isDirectory()) {
                for (String file : pathOrFile.list()) {
                    deleteFileOrPath(pathOrFileName + File.separator + file);
                }
            }
            if (!pathOrFile.delete()) {
                new WriteFileHandler(weakContext, "ERROR", null, "Error deleting file: '" + pathOrFileName + "'\n", true).run();
            }
        }
    }

    @Override
    public void run() {
        deleteFileOrPath(pathOrFileName);
    }
}
