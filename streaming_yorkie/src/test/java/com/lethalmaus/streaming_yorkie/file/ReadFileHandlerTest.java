package com.lethalmaus.streaming_yorkie.file;

import android.content.Context;

import com.lethalmaus.streaming_yorkie.MockContext;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ReadFileHandlerTest {

    private Context context = new MockContext().getMockContext();

    private ReadFileHandler readFileHandler;

    @Test
    public void shouldReadFile() {
        //Setup
        String fileContent = "TEST";
        new WriteFileHandler(null, new WeakReference<>(context), "READ_FILE_TEST1", null, fileContent, false).writeToFileOrPath();
        assertTrue(new File(context.getFilesDir() + File.separator + "READ_FILE_TEST1").exists());
        //Test
        readFileHandler = new ReadFileHandler(null, new WeakReference<>(context), "READ_FILE_TEST1");
        assertTrue(readFileHandler.readFile().contentEquals(fileContent));
        //Cleanup
        assertTrue(new File(context.getFilesDir() + File.separator + "READ_FILE_TEST1").delete());
    }

    @Test
    public void shouldReadFilenames() {
        //Setup
        ArrayList<String> files = new ArrayList<>();
        files.add("READ_FILE_TEST" + File.separator + "READ_FILE_TEST2");
        files.add("READ_FILE_TEST" + File.separator + "READ_FILE_TEST3");
        new WriteFileHandler(null, new WeakReference<>(context), "", files, null, false).writeToFileOrPath();
        //Test
        readFileHandler = new ReadFileHandler(null, new WeakReference<>(context), "READ_FILE_TEST");
        files = readFileHandler.readFileNames();
        assertEquals(files.size(), 2);
        assertTrue(files.get(0).contains("READ_FILE_TEST"));
        assertTrue(files.get(1).contains("READ_FILE_TEST"));
        //Cleanup
        new DeleteFileHandler(null, new WeakReference<>(context), "").deleteFileOrPath("READ_FILE_TEST");
        assertFalse(new File(context.getFilesDir() + File.separator + "READ_FILE_TEST" + File.separator + "READ_FILE_TEST2").exists());
        assertFalse(new File(context.getFilesDir() + File.separator + "READ_FILE_TEST" + File.separator + "READ_FILE_TEST3").exists());
        assertFalse(new File(context.getFilesDir() + File.separator + "READ_FILE_TEST").exists());
    }

    @Test
    public void shouldCountFiles() {
        ArrayList<String> files = new ArrayList<>();
        files.add("READ_FILE_TEST" + File.separator + "READ_FILE_TEST4");
        files.add("READ_FILE_TEST" + File.separator + "READ_FILE_TEST5");
        new WriteFileHandler(null, new WeakReference<>(context), "", files, null, false).writeToFileOrPath();
        //Test
        readFileHandler = new ReadFileHandler(null, new WeakReference<>(context), "READ_FILE_TEST");
        assertEquals(readFileHandler.countFiles(), 2);
        //Cleanup
        new DeleteFileHandler(null, new WeakReference<>(context), "").deleteFileOrPath("READ_FILE_TEST");
        assertFalse(new File(context.getFilesDir() + File.separator + "READ_FILE_TEST" + File.separator + "READ_FILE_TEST4").exists());
        assertFalse(new File(context.getFilesDir() + File.separator + "READ_FILE_TEST" + File.separator + "READ_FILE_TEST5").exists());
        assertFalse(new File(context.getFilesDir() + File.separator + "READ_FILE_TEST").exists());
    }
}
