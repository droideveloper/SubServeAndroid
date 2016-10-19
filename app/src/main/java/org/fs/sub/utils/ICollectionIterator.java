package org.fs.sub.utils;

/**
 * Created by Fatih on 13/05/16.
 * as org.fs.sub.utils.ICollectionIterrator
 */
public interface ICollectionIterator<T> {

    /**
     * checks if we can go further or not
     * @return boolean value as true or false
     */
    boolean hasNext();

    /**
     * T type of the collectionIterator
     * @return T instance if it can go further else it will be null
     */
    T next();

    /**
     * T type for search that is intent to find in our SrtSequence objects that has time stamp
     * @param ms milliseconds to look for
     * @return returns T and sets current index found location if there is else it will remain where it was
     */
    T find(long ms);
}
