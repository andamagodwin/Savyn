package app.andama.savyn.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    /**
     * Formatting currency for Uganda Shillings (UGX).
     * While Locale("en", "UG") would work for symbols, 
     * explicit control ensures consistency.
     */
    private val ugLocale = Locale("en", "UG")
    private val formatter = NumberFormat.getCurrencyInstance(ugLocale)

    fun format(amount: Double): String {
        return formatter.format(amount)
    }
}
