package com.example.gerenciador_de_notas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarScreen : AppCompatActivity(), CalendarAdapter.OnItemListener {

    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var selectedDate: LocalDate

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_screen)

        val menuHome = findViewById<ImageView>(R.id.menuHomeCalendar)
        val menuCalendar = findViewById<ImageView>(R.id.menuCalendarCalendar)
        val menuMenu = findViewById<ImageView>(R.id.menuMenuCalendar)
        val previousMonth = findViewById<ImageView>(R.id.previousArrow)
        val nextMonth = findViewById<ImageView>(R.id.forwardArrow)

        previousMonth.setOnClickListener {
            previousMonthAction(null)
        }

        nextMonth.setOnClickListener {
            nextMonthAction(null)
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

        initWidgets()
        selectedDate = LocalDate.now()
        setMonthView()
    }

    private fun initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
        monthYearText = findViewById(R.id.monthTV)
    }

    private fun setMonthView() {
        monthYearText.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)

        val calendarAdapter = CalendarAdapter(daysInMonth, this@CalendarScreen)
        val layoutManager = GridLayoutManager(applicationContext, 7)
        calendarRecyclerView.layoutManager = layoutManager
        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun daysInMonthArray(date: LocalDate): ArrayList<String> {
        val daysInMonthArray = ArrayList<String>()
        val yearMonth = YearMonth.from(date)

        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value

        for (i in 1..42) {
            daysInMonthArray.add(
                if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) "" else (i - dayOfWeek).toString()
            )
        }
        return daysInMonthArray
    }

    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    fun previousMonthAction(view: View?) {
        selectedDate = selectedDate.minusMonths(1)
        setMonthView()
    }

    fun nextMonthAction(view: View?) {
        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
    }

    override fun onItemClick(position: Int, dayText: String) {
        if (dayText.isNotEmpty()) {
            val message = "Selected Date $dayText ${monthYearFromDate(selectedDate)}"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}
