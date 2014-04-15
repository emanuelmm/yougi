package org.cejug.yougi.knowledge.batch;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@Dependent
public class MessagesProcessor implements ItemProcessor {

    @Override
    public Object processItem(Object message) throws Exception {
        return null;
    }
}