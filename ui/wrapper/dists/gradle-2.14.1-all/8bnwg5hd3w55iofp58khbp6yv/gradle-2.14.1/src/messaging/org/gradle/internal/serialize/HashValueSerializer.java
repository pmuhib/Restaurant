/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.internal.serialize;

import org.gradle.internal.hash.HashValue;

public class HashValueSerializer implements Serializer<HashValue> {
    @Override
    public HashValue read(Decoder decoder) throws Exception {
        byte hashSize = decoder.readByte();
        byte[] hash = new byte[hashSize];
        decoder.readBytes(hash);
        return new HashValue(hash);
    }

    @Override
    public void write(Encoder encoder, HashValue value) throws Exception {
        byte[] hash = value.asByteArray();
        encoder.writeByte((byte) hash.length);
        encoder.writeBytes(hash);
    }
}
