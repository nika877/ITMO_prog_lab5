package Data

/**
 * Координаты работника
 * @property X Координата X (не null)
 * @property Y Координата Y (> -518, не null)
 */
data class Coordinates(
    val X: Float,
    val Y: Float
) {
    init {
        require(Y > -518) { "Y должен быть больше -518" }
    }
}