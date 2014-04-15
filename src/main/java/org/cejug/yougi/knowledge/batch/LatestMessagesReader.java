package org.cejug.yougi.knowledge.batch;

import javax.batch.api.chunk.AbstractItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@Dependent
public class LatestMessagesReader extends AbstractItemReader {

    @Override
    public void open(Serializable checkpoint) throws Exception {

    }

    @Override
    public Object readItem() throws Exception {
        return null;
    }
}
