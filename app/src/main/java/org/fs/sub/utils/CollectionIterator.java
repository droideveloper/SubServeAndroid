/*
 * SubServe Android Copyright (C) 2016 Fatih.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fs.sub.utils;

import org.fs.sub.model.SrtSequenceEntity;
import org.fs.xml.type.XMLRpcObject;

import java.util.LinkedList;

public class CollectionIterator implements ICollectionIterator<SrtSequenceEntity> {

  private final XMLRpcObject mutex;
  private final LinkedList<SrtSequenceEntity> dataSet;
  private int index = 0;

  public CollectionIterator(LinkedList<SrtSequenceEntity> dataSet) {
    this.dataSet = dataSet;
    this.mutex = new XMLRpcObject();
  }

  @Override public boolean hasNext() {
    return index < size();
  }

  @Override public SrtSequenceEntity next() {
    synchronized (mutex) {
      return hasNext() ? dataSet.get(index++) : null;
    }
  }

  @Override public SrtSequenceEntity find(long ms) {
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
