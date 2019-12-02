package com.lethalmaus.streaming_yorkie.file;

import android.content.Context;

import com.lethalmaus.streaming_yorkie.MockContext;

import org.junit.Test;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class WriteFileHandlerTest {

    private Context context = new MockContext().getMockContext();

    private WriteFileHandler writeFileHandler;

    @Test
    public void shouldWriteSingleFileWithData() {
        shouldWriteSingleFile("TEST");
    }

    @Test
    public void shouldWriteSingleFileWithNoData() {
        shouldWriteSingleFile(null);
    }

    @Test
    public void shouldWriteMultipleFilesWithNoData() {
        //Setup
        ArrayList<String> files = new ArrayList<>();
        files.add("WRITE_FILE_TEST2");
        files.add("WRITE_FILE_TEST3");
        //Test
        writeFileHandler = new WriteFileHandler(null, new WeakReference<>(context), "", files, null, false);
        writeFileHandler.writeToFileOrPath();
        assertTrue(new File(context.getFilesDir() + File.separator + "WRITE_FILE_TEST2").exists());
        assertTrue(new File(context.getFilesDir() + File.separator + "WRITE_FILE_TEST3").exists());
        //Cleanup
        assertTrue(new File(context.getFilesDir() + File.separator + "WRITE_FILE_TEST2").delete());
        assertTrue(new File(context.getFilesDir() + File.separator + "WRITE_FILE_TEST3").delete());
    }

    private void shouldWriteSingleFile(String data) {
        //Test
        writeFileHandler = new WriteFileHandler(null, new WeakReference<>(context), "WRITE_FILE_TEST1", null, data, false);
        writeFileHandler.writeToFileOrPath();
        assertTrue(new File(context.getFilesDir() + File.separator + "WRITE_FILE_TEST1").exists());
        //Cleanup
        assertTrue(new File(context.getFilesDir() + File.separator + "WRITE_FILE_TEST1").delete());
    }
}
