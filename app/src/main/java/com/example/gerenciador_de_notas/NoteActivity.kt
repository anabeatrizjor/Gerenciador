package com.example.gerenciador_de_notas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NoteActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private var allNotes: List<Note> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        sharedPref = getSharedPreferences("note_preferences", Context.MODE_PRIVATE)

        val menuHome = findViewById<ImageView>(R.id.menuHomeMain)
        val menuCalendar = findViewById<ImageView>(R.id.menuCalendarMain)
        val addNote = findViewById<ImageView>(R.id.addNote)
        val searchView = findViewById<SearchView>(R.id.searchViewMain)
        val miniMenu = findViewById<ImageView>(R.id.miniMenuOptions)

        miniMenu.setOnClickListener {
            exibirDialogo("Deseja realmente sair do aplicativo?")
        }

        notesRecyclerView = findViewById(R.id.notesRecyclerView)
        notesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with empty list
        notesAdapter = NotesAdapter(emptyList(), this)
        notesRecyclerView.adapter = notesAdapter

        // Load notes from SharedPreferences or another data source
        loadNotes()

        addNote.setOnClickListener {
            startActivity(Intent(this, AddNote::class.java))
        }

        menuHome.setOnClickListener {
            Toast.makeText(this, "Você já está na tela principal", Toast.LENGTH_SHORT).show()
        }

        menuCalendar.setOnClickListener {
            startActivity(Intent(this, CalendarScreen::class.java))
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText.orEmpty())
                return true
            }
        })

        window.statusBarColor = getColor(R.color.blueDark)
    }

    private fun loadNotes() {
        val notesJson = sharedPref.getString("notes", "[]")
        val notesListType = object : TypeToken<List<Note>>() {}.type
        allNotes = Gson().fromJson(notesJson, notesListType)
        notesAdapter.updateNotes(allNotes)
    }

    private fun filterNotes(query: String) {
        val filteredNotes = allNotes.filter { note ->
            note.title.contains(query, ignoreCase = true) ||
                    note.content.contains(query, ignoreCase = true)
        }
        notesAdapter.updateNotes(filteredNotes)
    }

    private fun exibirDialogo(mensagem: String) {
        AlertDialog.Builder(this)
            .setTitle("ENCERRAR ATIVIDADE")
            .setMessage(mensagem)
            .setPositiveButton("SIM") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("NÃO") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}