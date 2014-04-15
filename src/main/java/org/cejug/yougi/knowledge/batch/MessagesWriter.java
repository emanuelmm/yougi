package org.cejug.yougi.knowledge.batch;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@Dependent
public class MessagesWriter extends AbstractItemWriter {
    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional
    public void writeItems(List messages) throws Exception {

    }
}
