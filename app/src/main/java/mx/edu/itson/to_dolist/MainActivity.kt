package mx.edu.itson.to_dolist

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private lateinit var taskManager: TaskManager
    private lateinit var taskAdapter: TaskAdapter
    private var currentFilter: String = "ALL" // "ALL", "PENDING", "DONE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskManager = TaskManager(this)

        setupRecyclerView()
        setupFilters()
        loadTasks()

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            showTaskDialog()
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClick = { task ->
                taskManager.updateTask(task.copy(isDone = !task.isDone))
                loadTasks()
            },
            onDeleteClick = { task ->
                taskManager.deleteTask(task)
                loadTasks()
            },
            onEditClick = { task ->
                showTaskDialog(task)
            }
        )
        findViewById<RecyclerView>(R.id.rvTasks).apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupFilters() {
        findViewById<Button>(R.id.btnAll).setOnClickListener {
            currentFilter = "ALL"
            loadTasks()
        }
        findViewById<Button>(R.id.btnPending).setOnClickListener {
            currentFilter = "PENDING"
            loadTasks()
        }
        findViewById<Button>(R.id.btnDone).setOnClickListener {
            currentFilter = "DONE"
            loadTasks()
        }
    }

    private fun loadTasks() {
        val tasks = taskManager.getTasks()
        val filteredTasks = when (currentFilter) {
            "PENDING" -> tasks.filter { !it.isDone }
            "DONE" -> tasks.filter { it.isDone }
            else -> tasks
        }
        taskAdapter.submitList(filteredTasks)
    }

    private fun showTaskDialog(taskToEdit: Task? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val tilTask = dialogView.findViewById<TextInputLayout>(R.id.tilTask)
        val etTask = dialogView.findViewById<TextInputEditText>(R.id.etTask)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        if (taskToEdit != null) {
            tvTitle.text = "Editar tarea"
            etTask.setText(taskToEdit.content)
            btnSave.text = "Actualizar"
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val taskText = etTask.text.toString().trim()
            if (taskText.isEmpty()) {
                tilTask.error = "El campo no puede estar vacío"
            } else {
                if (taskToEdit == null) {
                    taskManager.addTask(Task(content = taskText))
                } else {
                    taskManager.updateTask(taskToEdit.copy(content = taskText))
                }
                loadTasks()
                dialog.dismiss()
            }
        }

        dialog.show()
        dialog.window?.let { window ->
            val layoutParams = window.attributes
            layoutParams.gravity = Gravity.TOP
            layoutParams.y = 200
            window.attributes = layoutParams
        }
    }
}
