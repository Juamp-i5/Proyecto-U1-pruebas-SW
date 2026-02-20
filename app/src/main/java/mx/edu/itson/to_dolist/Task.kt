package mx.edu.itson.to_dolist

data class Task(
    val id: Long = System.currentTimeMillis(),
    val content: String
)
