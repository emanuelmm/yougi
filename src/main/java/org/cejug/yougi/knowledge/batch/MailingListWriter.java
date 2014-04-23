package org.cejug.yougi.knowledge.batch;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@Dependent
public class MailingListWriter extends AbstractItemWriter {

    private static final Logger LOGGER = Logger.getLogger(MailingListWriter.class.getSimpleName());

    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional
    public void writeItems(List messages) throws Exception {
        LOGGER.log(Level.INFO, "List {0}", messages.size());
    }
}