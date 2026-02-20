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
        loadTasks()

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            showTaskDialog()
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
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

    private fun loadTasks() {
        val tasks = taskManager.getTasks()
        taskAdapter.submitList(tasks)
    }

    private fun showTaskDialog(taskToEdit: Task? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val tilTask = dialogView.findViewById<TextInputLayout>(R.id.tilTask)
        val etTask = dialogView.findViewById<TextInputEditText>(R.id.etTask)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        if (taskToEdit != null) {
            tvTitle.text = getString(R.string.edit_task)
            etTask.setText(taskToEdit.content)
            btnSave.text = getString(R.string.update_task)
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
                tilTask.error = getString(R.string.empty_error)
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
