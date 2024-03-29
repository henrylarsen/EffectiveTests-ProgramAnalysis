package org.effective.tests.modifier;

import java.io.FileWriter;
import java.io.IOException;

public class FileWriterWrapper {
    FileWriter writer;


    public FileWriterWrapper(String filePath) throws IOException {
        this.writer = new FileWriter(filePath, false);
    }

    public void write(String fileContents) throws IOException {
        writer.write(fileContents);
        writer.close();
    }
}
