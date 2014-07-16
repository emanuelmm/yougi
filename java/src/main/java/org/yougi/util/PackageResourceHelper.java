package org.yougi.util;

import org.jboss.vfs.TempFileProvider;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.yougi.business.JobSchedulerBean;
import org.yougi.exception.EnvironmentResourceException;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by mendoncafilh on 16/07/2014.
 */
public enum PackageResourceHelper {

    INSTANCE;

    public List<File> getFilesFolder(String pathFolder) {
        final ClassLoader loader = JobSchedulerBean.class.getClassLoader();
        URL url = loader.getResource(pathFolder);

        if (url == null) {
            return Collections.emptyList();
        }

        VirtualFile virtualFile;
        Closeable handle = null;
        String protocol = url.getProtocol();
        List<File> files = new ArrayList<>();
        try {
            if ("vfs".equals(protocol)) {
                URLConnection conn = url.openConnection();
                virtualFile = (VirtualFile) conn.getContent();
            } else if ("file".equals(protocol)) {
                virtualFile = VFS.getChild(url.toURI());
                File archiveFile = virtualFile.getPhysicalFile();
                TempFileProvider provider = TempFileProvider.create("tmp", Executors.newScheduledThreadPool(2));
                handle = VFS.mountZip(archiveFile, virtualFile, provider);
            } else {
                throw new UnsupportedOperationException("Protocol " + protocol + " is not supported");
            }

            List<VirtualFile> virtualFiles = virtualFile.getChildren();
            for(VirtualFile vFile : virtualFiles) {
                files.add(vFile.getPhysicalFile());
            }

            if(handle != null) {
                handle.close();
            }
        } catch (IOException | URISyntaxException ioe) {
            throw new EnvironmentResourceException(ioe.getMessage(), ioe);
        }

        return files;
    }
}
