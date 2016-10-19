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
package org.fs.sub.database;

import android.support.annotation.NonNull;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.fs.sub.model.SrtEntity;

public interface IDatabaseHelper {

  /**
   *
   * @param hash
   */
  void withHashAndFirst(@NonNull String hash);

  /**
   * @param hash HASH of Media File
   */
  void withHashAndLangAndFirst(@NonNull String hash, @NonNull String lang);

  /**
   * @param imdb IMDB_ID
   */
  void withImdbAndLangAndFirst(@NonNull String imdb, @NonNull String lang);

  /**
   *
   * @param entity
   */
  void withUpdateOrSaveSrtEntity(@NonNull SrtEntity entity);

  /**
   * Release resources such as Dao objects created by OrmLite library.
   */
  void releaseAll();

  /**
   *
   * @return
   */
  RuntimeExceptionDao<SrtEntity, Long> getSrtEntityRuntimeDao();
}
