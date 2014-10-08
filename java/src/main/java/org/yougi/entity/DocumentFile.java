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

import javax.persistence.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = "DOCUMENT_FILE")
public class DocumentFile implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CHEMIN = "/mnt/FichiersEPC/";

    private static final int EOF = -1;

    @Id
    private String id;

    @Column(name = "NOM", nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE_CONTENU", nullable = false)
    private ContentType typeContenu;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_ENREGISTREMENT", nullable = false)
    private final Date dateEnregistrement = Calendar.getInstance().getTime();

    @Column(name = "DUREE_STOCKAGE")
    private Integer dureeStockage = StorageDuration.TEMPORARY.getDays();

    @Column(name = "CHEMIN_ABSOLU")
    private String cheminAbsolu;

    @Column(name = "NOM_FICHIER")
    private String nomFichier;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "UTILISATEUR")
    private String utilisateur;

    @Transient
    private byte[] contenu;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "CONTENU")
    private byte[] ancienContenu;

    public DocumentFile() {
    }

    public DocumentFile(String nom, ContentType typeContenu, StorageDuration dureeStockage, byte[] contenu) {
        init(nom, typeContenu, dureeStockage, contenu, null, null);
    }

    public DocumentFile(String nom, ContentType typeContenu, StorageDuration dureeStockage, byte[] contenu, String description) {
        init(nom, typeContenu, dureeStockage, contenu, description, null);
    }

    public DocumentFile(String nom, ContentType typeContenu, StorageDuration dureeStockage, byte[] contenu, String description, String utilisateur) {
        init(nom, typeContenu, dureeStockage, contenu, description, utilisateur);
    }

    public DocumentFile(String nom, ContentType typeContenu, StorageDuration dureeStockage, InputStream isContenu) throws EnvironmentResourceException {
        byte[] bContenu = convertirAByte(isContenu);
        init(nom, typeContenu, dureeStockage, bContenu, null, null);
    }

    public DocumentFile(String nom, ContentType typeContenu, StorageDuration dureeStockage, InputStream isContenu, String description) throws EnvironmentResourceException {
        byte[] bContenu = convertirAByte(isContenu);
        init(nom, typeContenu, dureeStockage, bContenu, description, null);
    }

    public DocumentFile(String nom, ContentType typeContenu, StorageDuration dureeStockage, InputStream isContenu, String description, String utilisateur) throws EnvironmentResourceException {
        byte[] bContenu = convertirAByte(isContenu);
        init(nom, typeContenu, dureeStockage, bContenu, description, utilisateur);
    }

    private void init(String nom, ContentType typeContenu, StorageDuration dureeStockage, byte[] contenu, String description, String utilisateur) {
        if (!contientExtension(nom)) {
            this.nom = nom + "." + typeContenu.getExtension();
        } else {
            this.nom = nom;
        }
        this.typeContenu = typeContenu;
        this.contenu = contenu;

        if (this.dureeStockage != null) {
            this.dureeStockage = dureeStockage.getDays();
        } else {
            this.dureeStockage = StorageDuration.TEMPORARY.getDays();
        }

        this.nomFichier = definirNomFichier();
        this.cheminAbsolu = CHEMIN;
        this.description = description;
        this.utilisateur = utilisateur;
    }

    /**
     * Il crée un nom pour le fichier qui n'entre jamais en conflit.
     * */
    private String definirNomFichier() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().toUpperCase() + "." + typeContenu.getExtension();
    }

    public byte[] convertirAByte(InputStream input) throws EnvironmentResourceException {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int nRead;
            byte[] buffer = new byte[16384];
            while ((nRead = input.read(buffer, 0, buffer.length)) != EOF) {
                output.write(buffer, 0, nRead);
            }
            output.flush();
            return output.toByteArray();
        } catch (IOException ex) {
            throw new EnvironmentResourceException("Impossible de lire le input stream.");
        }
    }

    private boolean contientExtension(String nomFichier) {
        int posExtension = nomFichier.lastIndexOf(".");
        boolean contient = false;
        if (posExtension >= 1) {
            String extension = nomFichier.substring(posExtension + 1);
            if (extension.length() > 2 && extension.length() < 5) {
                contient = ContentType.isKnownExtension(extension);
            }
        }
        return contient;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public ContentType getTypeContenu() {
        return typeContenu;
    }

    public Date getDateEnregistrement() {
        return dateEnregistrement;
    }

    /**
     * Temps en jours que le fichier est stocké dans la base
     * de donnés avant d'être supprimé automatiquement. Le décompte commence à
     * partir de la date d'enregistrement.
     */
    public Integer getDureeStockage() {
        return dureeStockage;
    }

    public String getCheminAbsolu() {
        return cheminAbsolu;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public byte[] getContenu() {
        return contenu;
    }

    public byte[] getAncienContenu() {
        return this.ancienContenu;
    }

    public String getDescription() {
        return description;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DocumentFile other = (DocumentFile) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
}