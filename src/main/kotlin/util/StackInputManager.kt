package util

import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class StackInputManager : InputManager {
    lateinit var outputManager: OutputManager
    private data class ReaderWrapper(val reader: BufferedReader, val isSystem: Boolean)

    private val inputStack = Stack<ReaderWrapper>()
    private val systemReader = ReaderWrapper(System.`in`.bufferedReader(), isSystem = true)
    private var scriptMode = false

    init {
        inputStack.push(systemReader)
    }

    override fun readLine(): String? {
        while (inputStack.isNotEmpty()) {
            val reader = inputStack.peek().reader
            val line = reader.readLine()
            if (line != null) {
                val trimmed = line.trim()
                if (scriptMode && trimmed.isNotEmpty()) {
                    outputManager.writeLine(trimmed) // Теперь это работает!
                }
                return if (trimmed.isNotEmpty()) trimmed else readLine()
            } else {
                popSource()
            }
        }
        return null
    }

    override fun hasInput(): Boolean = true // Can't reliably check without blocking, always true.

    override fun isScriptMode(): Boolean = scriptMode

    override fun pushSource(path: Path) {
        val reader = Files.newBufferedReader(path)
        inputStack.push(ReaderWrapper(reader, isSystem = false))
        scriptMode = true
    }

    override fun popSource() {
        if (inputStack.size > 1) {
            val removed = inputStack.pop()
            if (!removed.isSystem) {
                removed.reader.close()
            }
            scriptMode = inputStack.size > 1
        }
    }

    override fun exitScriptMode() {
        while (inputStack.size > 1) {
            popSource()
        }
        scriptMode = false
    }

    override fun currentSource(): Path {
        throw UnsupportedOperationException("currentSource is not supported for Path-based sources")
    }

    override fun startScriptMode(path: Path) {
        pushSource(path)
    }

    override fun endScriptMode() {
        exitScriptMode()
    }
}
