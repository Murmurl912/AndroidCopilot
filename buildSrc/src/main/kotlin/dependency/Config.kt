package dependency

import java.io.File
import java.io.FileInputStream
import java.util.Properties

object Config {

    var properties: Properties = Properties()

    fun config(file: String) {
        if (File(file).exists()) {
            FileInputStream(file).use {
                properties.load(it)
            }
        }
    }

    fun openaiToken(): String {
        return properties["openai.key"] as String? ?: ""
    }

    fun openaiApi(): String {
        return properties["openai.api"] as String? ?: ""
    }



}