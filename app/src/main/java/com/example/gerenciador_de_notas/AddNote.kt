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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class AddNote : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var rootLayout: RelativeLayout

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        rootLayout = findViewById(R.id.rootLayout)
        sharedPref = getSharedPreferences("note_preferences", MODE_PRIVATE)

        // Aplica a cor de fundo salva
        val savedColor = getSavedBackgroundPreference()
        applyBackgroundColor(savedColor)

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        val miniMenu = findViewById<ImageView>(R.id.miniMenuNote)
        val colorSelector = findViewById<ImageView>(R.id.colorSelector)
        val saveNote = findViewById<Button>(R.id.checkSave)

        colorSelector.setOnClickListener {
            selectColor()
        }

        backArrow.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        miniMenu.setOnClickListener {
            exibirDialogo("Deseja apagar essa nota?")
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.blueDark)

        val titleInput: EditText = findViewById(R.id.titleInput)
        val noteInput: EditText = findViewById(R.id.noteInput)

        saveNote.setOnClickListener {
            val title = titleInput.text.toString()
            val content = noteInput.text.toString()
            val color = getSavedBackgroundPreference()
            saveNote(title, content, color)
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun saveNote(title: String, content: String, color: Int) {
        val editor = sharedPref.edit()
        val notes = sharedPref.getStringSet("notes", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        notes.add("$title: $content|$color") // Salvando a cor junto com a nota
        editor.putStringSet("notes", notes)
        editor.apply()
    }

    private fun selectColor() {
        val builder = AlertDialog.Builder(this)

        val dialogView = layoutInflater.inflate(R.layout.color_selector, null)
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
            dialog.dismiss()
        }

        orangeColor.setOnClickListener {
            saveBackgroundPreference(R.color.orangeBack)
            applyBackgroundColor(R.color.orangeBack)
            dialog.dismiss()
        }

        yellowColor.setOnClickListener {
            saveBackgroundPreference(R.color.yellowBack)
            applyBackgroundColor(R.color.yellowBack)
            dialog.dismiss()
        }

        blueColor.setOnClickListener {
            saveBackgroundPreference(R.color.oceanBack)
            applyBackgroundColor(R.color.oceanBack)
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun exibirDialogo(mensagem: String) {
        AlertDialog.Builder(this)
            .setTitle("APAGAR NOTA")
            .setMessage(mensagem)
            .setPositiveButton("SIM") { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
            }
            .setNegativeButton("NÃO") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun saveBackgroundPreference(colorResId: Int) {
        with(sharedPref.edit()) {
            putInt("background_color", colorResId)
            apply()
        }
        Log.d("AddNote", "Cor salva: $colorResId")
    }

    private fun getSavedBackgroundPreference(): Int {
        val color = sharedPref.getInt("background_color", R.color.oceanBack)
        Log.d("AddNote", "Cor recuperada: $color")
        return color
    }

    private fun applyBackgroundColor(colorResId: Int) {
        val color = ContextCompat.getColor(this, colorResId)
        rootLayout.setBackgroundColor(color)
        Log.d("AddNote", "Cor aplicada: $colorResId")
    }
}
