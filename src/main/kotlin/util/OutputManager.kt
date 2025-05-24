package util

interface OutputManager {
    fun write(text: String)
    fun writeLine(text: String)
    fun error(text: String)
    fun success(text: String)
    fun info(text: String)
    fun setSilentMode(silent: Boolean)
    fun isSilentMode(): Boolean
}