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

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import org.fs.sub.model.SrtEntity;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {

  private final static Class<?>[] clazzes = new Class<?>[] { SrtEntity.class };

  /**
   * this method used for generating classes for related db system and sqlite database
   *
   * @throws SQLException
   * @throws IOException
   */
  public static void main(String... args) throws SQLException, IOException {
    writeConfigFile("ormlite_config.txt", clazzes);
  }
}
