package org.cejug.yougi.knowledge.batch;

import javax.batch.api.chunk.AbstractItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@Dependent
public class MailingListReader extends AbstractItemReader {

    private static final Logger LOGGER = Logger.getLogger(MailingListReader.class.getSimpleName());

    @Override
    public void open(Serializable checkpoint) throws Exception {
        LOGGER.log(Level.INFO, "open");
    }

    @Override
    public Object readItem() throws Exception {
        LOGGER.log(Level.INFO, "read Item");
        return "read item";
    }
}
