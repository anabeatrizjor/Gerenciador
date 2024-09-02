package com.example.gerenciador_de_notas

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.gerenciador_de_notas.Note
import com.example.gerenciador_de_notas.NoteActivity
import com.example.gerenciador_de_notas.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddNote : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var rootLayout: RelativeLayout

    private var noteId: Int = -1 // Default to an invalid ID
    private var initialColor: Int = R.color.oceanBack // Default color

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        rootLayout = findViewById(R.id.rootLayout)
        sharedPref = getSharedPreferences("note_preferences", MODE_PRIVATE)

        val backArrow = findViewById<ImageView>(R.id.backArrowNotes)
        val miniMenu = findViewById<ImageView>(R.id.miniMenuNote)
        val colorSelector = findViewById<ImageView>(R.id.colorSelector)
        val saveNote = findViewById<ImageView>(R.id.checkSave)
        val editPen = findViewById<ImageView>(R.id.editPen)

        val titleInput: EditText = findViewById(R.id.titleInput)
        val noteInput: EditText = findViewById(R.id.noteInput)


        val colorResId = getSavedColorResId()  // Recupera o ID do recurso de cor salvo
        updateColorSelector(colorResId)

        // Get data from Intent
        val intent = intent
        noteId = intent.getIntExtra("NOTE_ID", -1)
        titleInput.setText(intent.getStringExtra("NOTE_TITLE"))
        noteInput.setText(intent.getStringExtra("NOTE_CONTENT"))
        initialColor = intent.getIntExtra("NOTE_COLOR", R.color.oceanBack)
        applyBackgroundColor(initialColor)

        colorSelector.setOnClickListener {
            selectColor()
        }

        backArrow.setOnClickListener {
            startActivity(Intent(this, NoteActivity::class.java))
        }

        miniMenu.setOnClickListener {
            exibirDialogo("Deseja apagar essa nota?", noteId)
        }

        saveNote.setOnClickListener {
            saveNote()
        }

        // Adding onClickListener for the editPen
        editPen.setOnClickListener {
            // Code to enable editing
            enableEditing(true)
        }
    }

    private fun selectColor() {
        val builder = AlertDialog.Builder(this)

        val dialogView = layoutInflater.inflate(R.layout.color_selector, null, false)
        builder.setView(dialogView)

        val pinkColor = dialogView.findViewById<LinearLayout>(R.id.pinkLayout)
        val orangeColor = dialogView.findViewById<LinearLayout>(R.id.orangeLayout)
        val yellowColor = dialogView.findViewById<LinearLayout>(R.id.yellowLayout)
        val blueColor = dialogView.findViewById<LinearLayout>(R.id.oceanLayout)
        val cancelBtn = dialogView.findViewById<Button>(R.id.cancelButton)

        val dialog = builder.create()

        pinkColor.setOnClickListener {
            saveBackgroundPreference(R.color.pinkBack)
            applyBackgroundColor(R.color.pinkBack)
            updateColorSelector(R.drawable.pink_color)  // Update the colorSelector image
            dialog.dismiss()
        }

        orangeColor.setOnClickListener {
            saveBackgroundPreference(R.color.orangeBack)
            applyBackgroundColor(R.color.orangeBack)
            updateColorSelector(R.drawable.orange_color)  // Update the colorSelector image
            dialog.dismiss()
        }

        yellowColor.setOnClickListener {
            saveBackgroundPreference(R.color.yellowBack)
            applyBackgroundColor(R.color.yellowBack)
            updateColorSelector(R.drawable.yellow_color)  // Update the colorSelector image
            dialog.dismiss()
        }

        blueColor.setOnClickListener {
            saveBackgroundPreference(R.color.oceanBack)
            applyBackgroundColor(R.color.oceanBack)
            updateColorSelector(R.drawable.blue_color)  // Update the colorSelector image
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateColorSelector(drawableResId: Int) {
        val colorSelector = findViewById<ImageView>(R.id.colorSelector)
        colorSelector.setImageResource(drawableResId)
    }

    private fun saveNote() {
        val title = findViewById<EditText>(R.id.titleInput).text.toString()
        val content = findViewById<EditText>(R.id.noteInput).text.toString()
        val colorResId = getSavedBackgroundPreference()

        val note = Note(
            id = noteId.takeIf { it != -1 } ?: System.currentTimeMillis().toInt(), // Use timestamp if creating new
            title = title,
            content = content,
            colorResId = colorResId
        )

        saveNoteToSharedPreferences(note)

        startActivity(Intent(this, NoteActivity::class.java))
    }

    private fun saveNoteToSharedPreferences(note: Note) {
        val notesJson = sharedPref.getString("notes", "[]")
        val notesListType = object : com.google.gson.reflect.TypeToken<MutableList<Note>>() {}.type
        val notes: MutableList<Note> = Gson().fromJson(notesJson, notesListType)

        // Update existing note or add new
        val index = notes.indexOfFirst { it.id == note.id }
        if (index >= 0) {
            notes[index] = note
        } else {
            notes.add(note)
        }

        with(sharedPref.edit()) {
            putString("notes", Gson().toJson(notes))
            apply()
        }
        Log.d("AddNote", "Nota salva: $note")
    }

    private fun saveBackgroundPreference(colorResId: Int) {
        with(sharedPref.edit()) {
            putInt("background_color", colorResId)
            apply()
        }
        Log.d("AddNote", "Cor salva: $colorResId")
    }

    private fun getSavedBackgroundPreference(): Int {
        return sharedPref.getInt("background_color", R.color.oceanBack)
    }

    private fun applyBackgroundColor(colorResId: Int) {
        val color = ContextCompat.getColor(this, colorResId)
        rootLayout.setBackgroundColor(color)
        window.statusBarColor = color
        Log.d("AddNote", "Cor aplicada: $colorResId")
    }

    private fun exibirDialogo(mensagem: String, noteId: Int) {
        AlertDialog.Builder(this)
            .setTitle("APAGAR NOTA")
            .setMessage(mensagem)
            .setPositiveButton("SIM") { _, _ ->
                deleteNote(noteId)
            }
            .setNegativeButton("NÃO") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteNote(noteId: Int) {
        // Retrieve notes from SharedPreferences
        val notesJson = sharedPref.getString("notes", "[]")
        val notesListType = object : TypeToken<MutableList<Note>>() {}.type
        val notes: MutableList<Note> = Gson().fromJson(notesJson, notesListType)

        // Remove the note with the matching ID
        val iterator = notes.iterator()
        while (iterator.hasNext()) {
            val note = iterator.next()
            if (note.id == noteId) {
                iterator.remove()
                break
            }
        }

        // Save updated notes to SharedPreferences
        with(sharedPref.edit()) {
            putString("notes", Gson().toJson(notes))
            apply()
        }

        // Notify the user and return to NoteActivity
        Toast.makeText(this, "Nota apagada com sucesso!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, NoteActivity::class.java))
        finish()
    }


    private fun enableEditing(enable: Boolean) {
        val titleInput: EditText = findViewById(R.id.titleInput)
        val noteInput: EditText = findViewById(R.id.noteInput)

        titleInput.isEnabled = enable
        noteInput.isEnabled = enable

        // Change the visibility of the Save button or other UI elements if necessary
    }

    private fun getSavedColorResId(): Int {
        // Substitua pelo ID do recurso de cor salvo, exemplo:
        return when (getSavedBackgroundPreference()) {
            R.color.pinkBack -> R.drawable.pink_color
            R.color.orangeBack -> R.drawable.orange_color
            R.color.yellowBack -> R.drawable.yellow_color
            R.color.oceanBack -> R.drawable.blue_color
            else -> R.drawable.blue_color
        }
    }
}
