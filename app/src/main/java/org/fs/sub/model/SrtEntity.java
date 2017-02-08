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
package org.fs.sub.model;

import android.os.Parcel;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.fs.core.AbstractEntity;
import org.fs.sub.SubServeApplication;
import org.fs.util.StringUtility;

@DatabaseTable(tableName = "srt")
public final class SrtEntity extends AbstractEntity {

  public final static String CLM_HASH = "hash";
  public final static String CLM_NAME = "name";
  public final static String CLM_DATA = "data";
  public final static String CLM_LANG = "lang";
  public final static String CLM_IMDB_ID = "imdb_id";

  @DatabaseField(generatedId = true) private Long id;

  @DatabaseField(columnName = CLM_HASH) private String hash;

  @DatabaseField(columnName = CLM_NAME) private String name;

  @DatabaseField(columnName = CLM_LANG) private String lang;

  @DatabaseField(columnName = CLM_IMDB_ID) private String imdb;

  @DatabaseField(columnName = CLM_DATA, dataType = DataType.BYTE_ARRAY) private byte[] data;

  private SrtEntity(Parcel input) {
    super(input);
  }

  public SrtEntity() { }

  public Long id() {
    return id;
  }

  public String hash() {
    return hash;
  }

  public byte[] data() {
    return data;
  }

  public String name() {
    return name;
  }

  public String lang() {
    return lang;
  }

  public String imdb() {
    return imdb;
  }

  public void id(Long id) {
    this.id = id;
  }

  public void hash(String hash) {
    this.hash = hash;
  }

  public void imdb(String imdb) {
    this.imdb = imdb;
  }

  public void data(byte[] data) {
    this.data = data;
  }

  public void name(String name) {
    this.name = name;
  }

  public void lang(String lang) {
    this.lang = lang;
  }

  @Override protected String getClassTag() {
    return SrtEntity.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return SubServeApplication.isApplicationLogEnabled();
  }

  @Override protected void readParcel(Parcel input) {
    id(input.readLong());
    hash(input.readString());
    imdb(input.readString());
    name(input.readString());
    lang(input.readString());
    input.readByteArray(data);
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    out.writeLong(id());
    if (!StringUtility.isNullOrEmpty(hash())) {
      out.writeString(hash());
    } else {
      out.writeString("");
    }
    if (!StringUtility.isNullOrEmpty(imdb())) {
      out.writeString(imdb());
    } else {
      out.writeString("");
    }
    out.writeString(name());
    out.writeString(lang());
    out.writeByteArray(data());
  }

  public final static Creator<SrtEntity> CREATOR = new Creator<SrtEntity>() {
    @Override public SrtEntity createFromParcel(Parcel source) {
      return new SrtEntity(source);
    }

    @Override public SrtEntity[] newArray(int size) {
      return new SrtEntity[size];
    }
  };
}
