package org.cejug.yougi.knowledge.batch;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@Dependent
public class MessagesProcessor implements ItemProcessor {

    private static final Logger LOGGER = Logger.getLogger(MessagesProcessor.class.getSimpleName());

    @Override
    public Object processItem(Object message) throws Exception {
        LOGGER.log(Level.INFO, "Message {0}", message);
        return message;
    }
}