package util

import java.nio.file.Path

interface InputManager {
    fun readLine(): String?
    fun hasInput(): Boolean
    fun startScriptMode(path: Path)
    fun endScriptMode()
    fun isScriptMode(): Boolean
    fun pushSource(path: Path)
    fun popSource()
    fun currentSource(): Path
    fun exitScriptMode()
}
