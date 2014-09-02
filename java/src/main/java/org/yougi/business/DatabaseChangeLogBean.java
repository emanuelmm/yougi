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

import org.yougi.entity.DatabaseChangeLog;
import org.yougi.util.PackageResourceHelper;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class DatabaseChangeLogBean extends AbstractBean<DatabaseChangeLog> {

    private static final Logger LOGGER = Logger.getLogger(DatabaseChangeLogBean.class.getSimpleName());

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
        List<File> files = PackageResourceHelper.getFilesFolder("org/yougi/db/changelog");
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
}