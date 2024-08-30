package com.example.gerenciador_de_notas

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var notesListView: ListView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)
        notesListView = findViewById(R.id.notesListView)

        loadNotes()

        val menuHome = findViewById<ImageView>(R.id.menuHomeMain)
        val menuCalendar = findViewById<ImageView>(R.id.menuCalendarMain)
        val menuMenu = findViewById<ImageView>(R.id.menuMenuMain)
        val addNote = findViewById<ImageView>(R.id.addNote)

        addNote.setOnClickListener {
            Toast.makeText(this, "Nova nota iniciada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AddNote::class.java))
        }

        menuHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        menuCalendar.setOnClickListener {
            startActivity(Intent(this, CalendarScreen::class.java))
        }

        menuMenu.setOnClickListener {
            startActivity(Intent(this, MenuScreen::class.java))
        }

        window.statusBarColor = getColor(R.color.blueDark)

    }

    private fun loadNotes() {
        val notes = sharedPref.getStringSet("notes", emptySet())?.toList() ?: emptyList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notes)
        notesListView.adapter = adapter
    }
}