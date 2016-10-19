package org.fs.sub.utils;

import org.fs.sub.model.SrtSequenceEntity;
import org.fs.xml.type.XMLRpcObject;

import java.util.LinkedList;

/**
 * Created by Fatih on 13/05/16.
 * as org.fs.sub.utils.CollectionIterator
 */
public class CollectionIterator implements ICollectionIterator<SrtSequenceEntity> {

    private final XMLRpcObject                  mutex;
    private final LinkedList<SrtSequenceEntity> dataSet;
    private int   index = 0;

    public CollectionIterator(LinkedList<SrtSequenceEntity> dataSet) {
        this.dataSet = dataSet;
        this.mutex = new XMLRpcObject();
    }

    @Override
    public boolean hasNext() {
        return index < size();
    }

    @Override
    public SrtSequenceEntity next() {
        synchronized (mutex) {
            return hasNext() ? dataSet.get(index++) : null;
        }
    }

    @Override
    public SrtSequenceEntity find(long ms) {
        synchronized (mutex) {
            if (size() > 0) { // means not null or empty list
                for (int i = 0; i < dataSet.size(); i++) {
                    SrtSequenceEntity e = dataSet.get(i);
                    if (SrtSequenceEntity.isBetween(ms, e)) {
                        index = i;
                        return e;
                    }
                }
            }
            return null;
        }
    }

    private int size() {
        return this.dataSet != null ? dataSet.size() : 0;
    }
}
