package com.tt.noteapplication.util

import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist

object JSoupUtil {
    fun replaceBreakAndSpace(input: String): String {
        return Jsoup.clean(input, Whitelist().addTags("br"))
            .replace("<br>", "", true)
            .replace("&nbsp;", " ", true)
    }
}