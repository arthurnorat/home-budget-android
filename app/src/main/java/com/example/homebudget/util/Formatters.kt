package com.example.homebudget.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Formatters {

    fun todayIsoDate(): String = isoDateFormatter().format(Date())

    fun monthKey(): String {
        val fmt = SimpleDateFormat("yyyy-MM", Locale.US)
        fmt.timeZone = TimeZone.getDefault()
        return fmt.format(Date())
    }

    /** "yyyy-MM-dd" -> "dd/MM/yyyy" por manipulação de string (sem parsing de
     *  data, então nenhum risco de fuso horário aqui). */
    fun displayDate(iso: String): String {
        val parts = iso.split("-")
        return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else iso
    }

    fun currency(cents: Int): String {
        val value = BigDecimal(cents).divide(BigDecimal(100))
        val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return format.format(value)
    }

    fun parseAmountToCents(text: String): Int? {
        val normalized = text.trim().replace(",", ".")
        val value = normalized.toDoubleOrNull() ?: return null
        if (value < 0) return null
        return Math.round(value * 100).toInt()
    }

    /** O DatePicker do Material3 trabalha com millis em UTC à meia-noite do
     *  dia selecionado — por isso a conversão para/de "yyyy-MM-dd" aqui usa
     *  Calendar em UTC, independente do fuso do aparelho, evitando o bug
     *  clássico de "dia errado por causa do fuso" no DatePicker do Compose. */
    fun isoDateToUtcMillis(iso: String): Long {
        val parts = iso.split("-").map { it.toInt() }
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.clear()
        calendar.set(parts[0], parts[1] - 1, parts[2])
        return calendar.timeInMillis
    }

    fun utcMillisToIsoDate(millis: Long): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = millis
        val y = calendar.get(Calendar.YEAR)
        val m = calendar.get(Calendar.MONTH) + 1
        val d = calendar.get(Calendar.DAY_OF_MONTH)
        return "%04d-%02d-%02d".format(y, m, d)
    }

    private fun isoDateFormatter(): SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getDefault() }
}
