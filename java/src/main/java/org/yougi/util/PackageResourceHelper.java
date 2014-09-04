/* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * There is a full copy of the GNU Lesser General Public License along with
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
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
 * Encapsulates the complexity of getting resources from the war file on runtime.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
public class PackageResourceHelper {

    private PackageResourceHelper() {}

    //public static File getFile() {

    //}

    public static List<File> getFilesFolder(String pathFolder) {
        URL url = getResourceUrl(pathFolder);

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

    private static URL getResourceUrl(String path) {
        final ClassLoader loader = PackageResourceHelper.class.getClassLoader();
        return loader.getResource(path);
    }
}