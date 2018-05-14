package plantscam.android.prada.lab.plantscamera.utils

import android.graphics.Matrix


class ImageUtils {
    companion object {
        /**
         * Returns a transformation matrix from one reference frame into another.
         * Handles cropping (if maintaining aspect ratio is desired) and rotation.
         *
         * @param srcWidth Width of source frame.
         * @param srcHeight Height of source frame.
         * @param dstWidth Width of destination frame.
         * @param dstHeight Height of destination frame.
         * @param applyRotation Amount of rotation to apply from one frame to another.
         * Must be a multiple of 90.
         * @param maintainAspectRatio If true, will ensure that scaling in x and y remains constant,
         * cropping the image if necessary.
         * @return The transformation fulfilling the desired requirements.
         */
        fun getTransformationMatrix(
                srcWidth: Int,
                srcHeight: Int,
                dstWidth: Int,
                dstHeight: Int,
                applyRotation: Int,
                maintainAspectRatio: Boolean): Matrix {
            val matrix = Matrix()

            if (applyRotation != 0) {
                if (applyRotation % 90 != 0) {
                    throw IllegalArgumentException("Rotation of $applyRotation% 90 != 0")
                }

                // Translate so center of image is at origin.
                matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f)

                // Rotate around origin.
                matrix.postRotate(applyRotation.toFloat())
            }

            // Account for the already applied rotation, if any, and then determine how
            // much scaling is needed for each axis.
            val transpose = (Math.abs(applyRotation) + 90) % 180 == 0

            val inWidth = if (transpose) srcHeight else srcWidth
            val inHeight = if (transpose) srcWidth else srcHeight

            // Apply scaling if necessary.
            if (inWidth != dstWidth || inHeight != dstHeight) {
                val scaleFactorX = dstWidth / inWidth.toFloat()
                val scaleFactorY = dstHeight / inHeight.toFloat()

                if (maintainAspectRatio) {
                    // Scale by minimum factor so that dst is filled completely while
                    // maintaining the aspect ratio. Some image may fall off the edge.
                    val scaleFactor = Math.max(scaleFactorX, scaleFactorY)
                    matrix.postScale(scaleFactor, scaleFactor)
                } else {
                    // Scale exactly to fill dst from src.
                    matrix.postScale(scaleFactorX, scaleFactorY)
                }
            }

            if (applyRotation != 0) {
                // Translate back from origin centered reference to destination frame.
                matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f)
            }

            return matrix
        }
    }
}