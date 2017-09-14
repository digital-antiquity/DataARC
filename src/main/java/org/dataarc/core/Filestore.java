package org.dataarc.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Filestore {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final String S_S_S = "%s-%s.%s";
    private static final String SCHEMA_S_S = "schema-%s.%s";
    @Value("${filestore.path}") 
    public String filestorePath = System.getProperty("java.io.tmpdir");
    
    public File getBaseDir() {
        File root = new File(filestorePath);
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
        File outfile = new File(schemaDir, String.format(SCHEMA_S_S,System.currentTimeMillis(), FilenameUtils.getExtension(file.getName())));
        torefile(file, outfile);
        return outfile;
    }

    private void torefile(File file, File outfile) throws IOException, FileNotFoundException {
        logger.debug("storing: {} --> {}", file, file.getAbsolutePath());
        IOUtils.copy(new FileInputStream(file), new FileOutputStream(outfile));
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
        File outfile = new File(schemaDir, String.format(S_S_S,originalFilename, System.currentTimeMillis(), FilenameUtils.getExtension(originalFilename)));
        IOUtils.copy(inputStream, new FileOutputStream(outfile));
        return outfile;
    }

    public File store(String schema, InputStream inputStream, String originalFilename) throws FileNotFoundException, IOException {
        File schemaDir = mkdir(new File(getBaseDir(), schema));
        File outfile = new File(schemaDir, String.format(SCHEMA_S_S,System.currentTimeMillis(), FilenameUtils.getExtension(originalFilename)));
        IOUtils.copy(inputStream, new FileOutputStream(outfile));
        return outfile;
    }
}
