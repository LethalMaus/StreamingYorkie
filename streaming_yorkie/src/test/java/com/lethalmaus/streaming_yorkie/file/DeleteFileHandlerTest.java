package com.lethalmaus.streaming_yorkie.file;

import android.content.Context;

import com.lethalmaus.streaming_yorkie.MockContext;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeleteFileHandlerTest {

    private Context context = new MockContext().getMockContext();

    @Test
    public void shouldDeleteFilesAndDirectory() throws IOException {
        //Setup
        if (
        !new File(context.getFilesDir() + File.separator + "DELETE_FILE_TEST").mkdirs() ||
        !new File(context.getFilesDir() + File.separator + "DELETE_FILE_TEST" + File.separator + "DELETE_FILE_TEST1").createNewFile() ||
        !new File(context.getFilesDir() + File.separator + "DELETE_FILE_TEST" + File.separator + "DELETE_FILE_TEST2").createNewFile()) {
            System.out.println("Test Delete files already exist");
        }
        assertTrue(new File(context.getFilesDir() + File.separator + "DELETE_FILE_TEST" + File.separator + "DELETE_FILE_TEST1").exists());
        assertTrue(new File(context.getFilesDir() + File.separator + "DELETE_FILE_TEST" + File.separator + "DELETE_FILE_TEST2").exists());
        assertTrue(new File(context.getFilesDir() + File.separator + "DELETE_FILE_TEST").exists());
        //Test
        new DeleteFileHandler(new WeakReference<>(context), "").deleteFileOrPath("DELETE_FILE_TEST");
        assertFalse(new File(context.getFilesDir() + File.separator + "DELETE_FILE_TEST" + File.separator + "DELETE_FILE_TEST1").exists());
        assertFalse(new File(context.getFilesDir() + File.separator + "DELETE_FILE_TEST" + File.separator + "DELETE_FILE_TEST2").exists());
        assertFalse(new File(context.getFilesDir() + File.separator + "DELETE_FILE_TEST").exists());
    }
}
