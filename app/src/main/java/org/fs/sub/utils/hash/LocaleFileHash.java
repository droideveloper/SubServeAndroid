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
package org.fs.sub.utils.hash;

import org.fs.util.PreconditionUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

final class LocaleFileHash extends IHash {

  private final File file;

  public LocaleFileHash(File file) {
    PreconditionUtility.checkNotNull(file, "why file == null ?");
    this.file = file;
  }

  @Override public long fileSize() {
    return file.length();
  }

  @Override public long invoke() throws IOException {
    long size = file.length();
    long chunkSize = Math.min(size, CHUNK_SIZE);

    FileChannel fileChannel = new FileInputStream(file).getChannel();
    try {
      long head = hashChunk(fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, chunkSize));
      long tail = hashChunk(
          fileChannel.map(FileChannel.MapMode.READ_ONLY, Math.max(size - chunkSize, 0), chunkSize));
      return head + tail + size;
    } finally {
      fileChannel.close();
    }
  }
}
