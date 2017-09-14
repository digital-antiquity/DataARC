package org.dataarc.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class Filestore {

    public static Filestore instance;
    
    public static Filestore getInstance() {
        if (instance == null) {
            instance = new Filestore();
        }
        return instance;
    }
    
    public File getBaseDir() {
        File root = new File(System.getProperty("java.io.tmpdir"));
        File store = mkdir(new File(root, "data-arc"));
        return store;
    }

    private File mkdir(File store) {
        if (!store.exists()) {
            store.mkdirs();
        }
        return store;
    }
    
    public File store(String schema, File file) throws FileNotFoundException, IOException {
        File schemaDir = mkdir(new File(getBaseDir(), schema));
        File outfile = new File(schemaDir, String.format("schmea-%s.%s",System.currentTimeMillis(), FilenameUtils.getExtension(file.getName())));
        IOUtils.copy(new FileInputStream(file), new FileOutputStream(outfile));
        return outfile;
    }

    public File retrieve(String schema) throws FileNotFoundException, IOException {
        File schemaDir = mkdir(new File(getBaseDir(), schema));
        return getLatestFilefromDir(schemaDir);
    }
    
    private File getLatestFilefromDir(File dir){
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
           if (lastModifiedFile.lastModified() < files[i].lastModified()) {
               lastModifiedFile = files[i];
           }
        }
        return lastModifiedFile;
    }

    public File storeFile(InputStream inputStream, String originalFilename) throws FileNotFoundException, IOException {
        File schemaDir = mkdir(new File(getBaseDir(), "files"));
        File outfile = new File(schemaDir, String.format("%s-%s.%s",originalFilename, System.currentTimeMillis(), FilenameUtils.getExtension(originalFilename)));
        IOUtils.copy(inputStream, new FileOutputStream(outfile));
        return outfile;
    }

    public File store(String schema, InputStream inputStream, String originalFilename) throws FileNotFoundException, IOException {
        File schemaDir = mkdir(new File(getBaseDir(), schema));
        File outfile = new File(schemaDir, String.format("schema-%s.%s",System.currentTimeMillis(), FilenameUtils.getExtension(originalFilename)));
        IOUtils.copy(inputStream, new FileOutputStream(outfile));
        return outfile;
    }
}
