package plantscam.android.prada.lab.plantscamera.ml

import java.nio.ByteBuffer

interface Classifier {
    fun recognize(pixels: ByteBuffer): Classification
}
