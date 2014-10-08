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
package org.yougi.entity;

import org.yougi.exception.EnvironmentResourceException;
import org.yougi.reference.StorageDuration;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.util.Objects;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
public final class Attachment {
    private static final int FILE_SIZE_LIMIT = 8000;
    private String fileName;
    private ContentType contentType;
    private DataSource dataSource;

    private Attachment(File file, String fileName, ContentType contentType) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.dataSource = new FileDataSource(file);
    }

    private Attachment(ByteArrayInputStream stream, String fileName, ContentType contentType) throws IOException {
        this.fileName = fileName;
        this.contentType = contentType;
        this.dataSource = new ByteArrayDataSource(stream, this.contentType.toString());
    }

    public static Attachment getInstance(File file, String fileName, ContentType contentType) {
        Attachment pieceJointe;
        if (fileName != null) {
            pieceJointe = new Attachment(file, fileName, contentType);
        } else {
            pieceJointe = new Attachment(file, file.getName(), contentType);
        }
        return pieceJointe;
    }

    public static Attachment getInstance(ByteArrayInputStream stream, String fileName, ContentType contentType) throws IOException {
        return new Attachment(stream, fileName, contentType);
    }

    public static Attachment getInstance(InputStream stream, String fileName, ContentType contentType) throws IOException {
        int bytesRead = 0;

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] buff = new byte[FILE_SIZE_LIMIT];

        while ((bytesRead = stream.read(buff)) != -1) {
            bao.write(buff, 0, bytesRead);
        }

        byte[] data = bao.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        return new Attachment(bin, fileName, contentType);
    }

    public DocumentFile createDocument(StorageDuration storageDuration) throws EnvironmentResourceException {
        DocumentFile document = null;
        try {
            int block = 16384;
            InputStream in = dataSource.getInputStream();
            ByteArrayOutputStream contenu = new ByteArrayOutputStream();
            byte[] data = new byte[block];
            int checkpoint;
            while ((checkpoint = in.read(data, 0, data.length)) != -1) {
                contenu.write(data, 0, checkpoint);
            }
            contenu.flush();
            document = new DocumentFile(fileName, contentType, storageDuration, contenu.toByteArray());
        } catch (IOException ioe) {
            throw new EnvironmentResourceException("Not possible to transform the attachment in a document.", ioe);
        }
        return document;
    }

    public final String getFileName() {
        return fileName;
    }

    public final ContentType getContentType() {
        return contentType;
    }

    public final DataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Attachment other = (Attachment) obj;
        if (!Objects.equals(this.fileName, other.fileName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.fileName);
        hash = 19 * hash + (this.contentType != null ? this.contentType.hashCode() : 0);
        return hash;
    }
}