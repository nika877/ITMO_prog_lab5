package commands


interface Command {
    fun execute(args: Array<String>)
    fun getDescription(): String
}