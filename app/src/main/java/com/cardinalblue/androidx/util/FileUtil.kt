package com.cardinalblue.androidx.util

import java.io.*

@Throws(IOException::class)
fun InputStream.toBytes() : kotlin.ByteArray {
    val os = ByteArrayOutputStream()
    copy(os)
    return os.toByteArray()
}


@Throws(IOException::class)
fun InputStream.copy(out: OutputStream) {
    val IO_BUFFER_SIZE = 1024 * 4
    val b = ByteArray(IO_BUFFER_SIZE)
    var read: Int = -1
    while ({ read = this.read(b); read }() != -1) {
        out.write(b, 0, read)
    }
}