package plantscam.android.prada.lab.plantscamera.ml

import java.nio.ByteBuffer

/**
 * Created by Piasy{github.com/Piasy} on 29/05/2017.
 */

//public interface for the classifer
//exposes its name and the recognize function
//which given some drawn pixels as input
//classifies what it sees as an MNIST image
interface Classifier {
    fun recognize(pixels: ByteBuffer): Classification
}
