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
package org.yougi.business;

import org.jboss.vfs.TempFileProvider;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.yougi.entity.DatabaseChangeLog;
import org.yougi.exception.EnvironmentResourceException;
import org.yougi.util.PackageResourceHelper;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class DatabaseChangeLogBean extends AbstractBean<DatabaseChangeLog> {

    private static final Logger LOGGER = Logger.getLogger(DatabaseChangeLogBean.class.getSimpleName());
    private static final String CHANGELOG_PATH = "org/yougi/db/changelog";

    @PersistenceContext
    private EntityManager em;

    public DatabaseChangeLogBean() {
        super(DatabaseChangeLog.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<DatabaseChangeLog> findAll() {
        return em.createQuery("select dbcl from DatabaseChangeLog dbcl order by dbcl.orderExecuted asc", DatabaseChangeLog.class)
                 .getResultList();
    }

    @Override
    public DatabaseChangeLog find(String id) {
        DatabaseChangeLog databaseChangeLog = super.find(id);
        databaseChangeLog.setChangesContent(getChangeLogContent(id));
        return databaseChangeLog;
    }

    private String getChangeLogContent(String id) {
        File changeLogFile = getChangeLogFile(id);
        StringBuilder content = new StringBuilder();
        try (InputStream in = Files.newInputStream(changeLogFile.toPath());
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, ioe.getMessage(), ioe);
        }
        return content.toString();
    }

    private File getChangeLogFile(String id) {
        List<File> files = PackageResourceHelper.INSTANCE.getFilesFolder(CHANGELOG_PATH);
        String filename;
        for(File file : files) {
            if (file.getName().endsWith(".sql")) {
                filename = file.getName();
                filename = filename.substring(9, 9 + id.length());
                if(filename.equals(id)) {
                    return file;
                }
            }
        }
        return null;
    }

    private Set<String> getChangeLogFiles() {
        final ClassLoader loader = JobSchedulerBean.class.getClassLoader();
        URL url = loader.getResource(CHANGELOG_PATH);

        if (url == null) {
            return Collections.emptySet();
        }

        VirtualFile virtualFile;
        Closeable handle = null;
        String protocol = url.getProtocol();
        Set<String> names = new HashSet<>();
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

            List<VirtualFile> files = virtualFile.getChildren();
            for(VirtualFile ccFile : files) {
                if (ccFile.getName().endsWith(".sql")) {
                    names.add(ccFile.getName());
                }
            }

            if(handle != null) {
                handle.close();
            }
        } catch (IOException | URISyntaxException ioe) {
            throw new EnvironmentResourceException(ioe.getMessage(), ioe);
        }

        return names;
    }
}