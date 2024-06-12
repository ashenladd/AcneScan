package com.dicoding.asclepius.utils

import com.dicoding.asclepius.utils.DateConstant.DATE_MONTH_DAY_YEAR_FORMAT
import com.dicoding.asclepius.utils.DateConstant.DATE_MONTH_YEAR_FORMAT
import com.dicoding.asclepius.utils.DateConstant.TIME_FORMAT
import com.dicoding.asclepius.utils.DateConstant.TIME_HOUR_MINUTE_MONTH_DAY_YEAR
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private fun Long.formatDate(toFormat: String = DATE_MONTH_YEAR_FORMAT): String {
    val sdf = SimpleDateFormat(
        toFormat,
        Locale.getDefault()
    )
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date(this))
}


private fun String.parseDate(fromFormat: String = TIME_FORMAT): Long {
    val sdf = SimpleDateFormat(
        fromFormat,
        Locale.getDefault()
    )
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.parse(this)?.time ?: 0L
}

fun String.parseFormatDate(
    fromFormat: String = TIME_FORMAT,
    toFormat: String = DATE_MONTH_DAY_YEAR_FORMAT,
): String {
    return this.parseDate(fromFormat).formatDate(toFormat)
}

fun Long.convertMillisToDateString(): String {
    val formatter = SimpleDateFormat(TIME_HOUR_MINUTE_MONTH_DAY_YEAR, Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    val date = Date(this)
    return formatter.format(date)
}



fun String.parseClassificationResult(): String {
    val analyzeResult = split("\n")[0]
    val stringList = analyzeResult.split(" ")
    val classification: String
    val confidence: String
    if (analyzeResult.contains("non", ignoreCase = true)) {
        classification = stringList.subList(0, 2).joinToString(" ")
        confidence = stringList[2]
    } else {
        classification = stringList[0]
        confidence = stringList[1]
    }
    return "Result : $classification\nConfidence : $confidence"
}