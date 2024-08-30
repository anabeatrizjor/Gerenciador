package com.example.gerenciador_de_notas

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MenuScreen : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_screen)

        val menuHome = findViewById<ImageView>(R.id.menuHomeMenu)
        val menuCalendar = findViewById<ImageView>(R.id.menuCalendarMenu)
        val menuMenu = findViewById<ImageView>(R.id.menuMenuMenu)

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
}