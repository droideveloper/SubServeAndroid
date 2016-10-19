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

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;

public abstract class IHash {

  protected final long CHUNK_SIZE = 64 * 1024;

  public static IHash from(@NonNull String uri) {
    if (uri.startsWith("http://") || uri.startsWith("https://")) {
      return new RemoteFileHash(uri);
    } else {
      return new LocaleFileHash(new File(uri));
    }
  }

  public abstract long invoke() throws IOException;

  public abstract long fileSize();

  protected long hashChunk(ByteBuffer buffer) {
    LongBuffer longBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();
    long hash = 0L;
    while (longBuffer.hasRemaining()) {
      hash += longBuffer.get();
    }
    return hash;
  }
}
