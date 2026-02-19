package mx.edu.itson.to_dolist

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class TaskManager(private val context: Context) {
    private val gson = Gson()
    private val file = File(context.filesDir, "tasks.json")

    fun getTasks(): List<Task> {
        if (!file.exists()) return emptyList()
        return try {
            val json = file.readText()
            val type = object : TypeToken<List<Task>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveTasks(tasks: List<Task>) {
        val json = gson.toJson(tasks)
        file.writeText(json)
    }

    fun addTask(task: Task) {
        val tasks = getTasks().toMutableList()
        tasks.add(task)
        saveTasks(tasks)
    }

    fun updateTask(updatedTask: Task) {
        val tasks = getTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            tasks[index] = updatedTask
            saveTasks(tasks)
        }
    }

    fun deleteTask(task: Task) {
        val tasks = getTasks().toMutableList()
        tasks.removeAll { it.id == task.id }
        saveTasks(tasks)
    }
}
