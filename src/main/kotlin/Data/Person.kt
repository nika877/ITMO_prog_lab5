package Data

/**
 * Персональные данные работника
 * @property Weight Вес (>0)
 * @property EyeColor Цвет глаз (может быть null)
 * @property HairColor Цвет волос (может быть null)
 * @property Location Локация (может быть null)
 */
data class Person(
    val Weight: Float,
    val EyeColor: Color?,
    val HairColor: Color?,
    val Location: Location?
) {
    init {
        require(Weight > 0) { "Вес должен быть больше 0" }
    }
}