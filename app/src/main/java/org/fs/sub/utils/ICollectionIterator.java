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

public interface ICollectionIterator<T> {

  /**
   * checks if we can go further or not
   *
   * @return boolean value as true or false
   */
  boolean hasNext();

  /**
   * T type of the collectionIterator
   *
   * @return T instance if it can go further else it will be null
   */
  T next();

  /**
   * T type for search that is intent to find in our SrtSequence objects that has time stamp
   *
   * @param ms milliseconds to look for
   * @return returns T and sets current index found location if there is else it will remain where
   * it was
   */
  T find(long ms);
}
