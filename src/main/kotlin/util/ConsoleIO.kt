package util

import java.nio.channels.Channels
import java.util.*

class ConsoleIO(
    private val inputManager: StackInputManager,
    private var outputStream: java.io.OutputStream = Channels.newOutputStream(Channels.newChannel(System.out)),
    private var errorStream: java.io.OutputStream = Channels.newOutputStream(Channels.newChannel(System.err))
) : InputManager by inputManager, OutputManager {

    companion object {
        private const val COLOR_RESET = "\u001B[0m"
        private const val COLOR_RED = "\u001B[31m"
        private const val COLOR_GREEN = "\u001B[32m"
        private const val COLOR_YELLOW = "\u001B[33m"
        private const val COLOR_CYAN = "\u001B[36m"
    }

    private var silentMode = false
    private var debugMode = false


    override fun write(text: String) {
        if (!silentMode) {
            outputStream.write(text.toByteArray())
            outputStream.flush()
        }
    }

    fun warning(message: String) = writeLine("[!] $message")

    override fun writeLine(text: String) {
        if (!silentMode) {
            outputStream.write("$text\n".toByteArray())
            outputStream.flush()
        }
    }

    override fun error(text: String) {
        errorStream.write("$COLOR_RED[ERROR] $text$COLOR_RESET\n".toByteArray())
        errorStream.flush()
    }

    override fun success(text: String) {
        if (!silentMode) {
            outputStream.write("$COLOR_GREEN[SUCCESS] $text$COLOR_RESET\n".toByteArray())
            outputStream.flush()
        }
    }

    override fun info(text: String) {
        if (!silentMode) {
            outputStream.write("$COLOR_CYAN[INFO] $text$COLOR_RESET\n".toByteArray())
            outputStream.flush()
        }
    }

    override fun setSilentMode(silent: Boolean) {
        silentMode = silent
        if (debugMode) writeDebug("Тихий режим: $silent")
    }

    override fun isSilentMode(): Boolean = silentMode

    fun readWithRetry(prompt: String, maxAttempts: Int = 3): String {
        var attempts = 0
        while (attempts < maxAttempts) {
            try {
                write(prompt)
                return inputManager.readLine() ?: throw IllegalArgumentException("Пустой ввод")
            } catch (e: Exception) {
                attempts++
                error("Некорректный ввод (попытка $attempts/$maxAttempts): ${e.message}")
            }
        }
        throw InputMismatchException("Превышено максимальное количество попыток ввода")
    }

    fun writeDebug(message: String) {
        errorStream.write("$COLOR_YELLOW[DEBUG] $message$COLOR_RESET\n".toByteArray())
        errorStream.flush()
    }
}