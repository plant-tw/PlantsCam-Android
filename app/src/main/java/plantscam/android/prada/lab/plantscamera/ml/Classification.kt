package plantscam.android.prada.lab.plantscamera.ml

data class Classification(
    val conf: Float = 0.toFloat(),
    val label: String)