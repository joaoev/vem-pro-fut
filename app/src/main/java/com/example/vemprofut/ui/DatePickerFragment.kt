package com.example.vemprofut.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private val calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // default date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // return new DatePickerDialog instance
        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.get(Calendar.DAY_OF_WEEK)

        // Obtém o dia da semana em português
        val formatoDiaDaSemana = SimpleDateFormat("EEEE", Locale("pt", "BR"))
        val diaDaSemana = formatoDiaDaSemana.format(calendar.time)

        val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(calendar.time)

        val selectedDateBundle = Bundle().apply {
            putString("SELECTED_DATE", selectedDate)
            putString("DIA_DA_SEMANA", diaDaSemana)
        }

        setFragmentResult("REQUEST_KEY", selectedDateBundle)
    }
}