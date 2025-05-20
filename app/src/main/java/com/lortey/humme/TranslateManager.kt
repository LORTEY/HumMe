package com.lortey.humme

import android.content.Context
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

var currentTranslationMap:MutableMap<String,String> = mutableMapOf()
var currentlyChosenTranslationLocation:Location = Location.ASSETS
var currentlyChosenTranslationFilePath:String = "Polski"

//get string translated in current map
fun getTranslation(text:String):String{
    if(text in currentTranslationMap){
        return currentTranslationMap[text]!!
    }else{
        Log.d("cardflareTranslations", "No Translation For: $text")
        currentTranslationMap[text] = text
        return text
    }
}

private val jsonFormat = Json { prettyPrint = true }

//has debuging usecases
public fun saveMap(context: Context, filename:String){
    val jsonString = jsonFormat.encodeToString(currentTranslationMap)
    val file = File(context.getExternalFilesDir(null), "Translations/$filename")
    file.writeText(jsonString)
}

//load selected language from file
fun loadMap(context: Context){
    val state = AppSettings["Language"]?.state as Translations
    currentlyChosenTranslationLocation= if(state.typeOfTranslation == TypeOfTranslation.Default) Location.ASSETS else Location.FILES_DIR
    currentlyChosenTranslationFilePath = state.name
    var jsonString = ""
    when(currentlyChosenTranslationLocation){
        Location.ASSETS ->
            jsonString = context.assets.open("Translations/$currentlyChosenTranslationFilePath").bufferedReader().use { it.readText() }

        Location.FILES_DIR ->
            jsonString = File(context.getExternalFilesDir(null), "Translations/$currentlyChosenTranslationFilePath").readText()
    }

    currentTranslationMap = jsonFormat.decodeFromString<MutableMap<String,String>>(jsonString)
}

//useful for app updates
fun remap(context: Context){
    val fromTo:Map<String,String> = mapOf()
    fromTo.forEach{key, value ->
        if(key in currentTranslationMap){
            currentTranslationMap[value] = currentTranslationMap[key]!!
            currentTranslationMap.remove(key)
        }

    }
    //saveMap(filename = str)
}
//get all possible Translations , creator provided, custom and ai generated not yet implemented
fun getPossibleTranslations(context: Context):List<Translations>{
    val possibleTranslations:MutableList<Translations> = mutableListOf()
    possibleTranslations.add(Translations("English",TypeOfTranslation.Default,
))
    possibleTranslations.add(Translations("Polski",TypeOfTranslation.Default,
))

    val filenames = listFilesInFilesDir(context,folderName =  "Translations")
    filenames.forEach { name ->
        possibleTranslations.add(Translations(name,TypeOfTranslation.Custom,))
    }

    /*val AiTranslations = getAllSupportedLanguages()

    AiTranslations.forEach { name ->
        possibleTranslations.add(Translations(name,TypeOfTranslation.AI,
            {currentlyChosenTranslationLocation= location.FILES_DIR
                currentlyChosenTranslationFilePath = name}))
    }*/

    return possibleTranslations
}


// lists all files in FlashcardDirectory
fun listFilesInFilesDir(context: Context, folderName: String = "FlashcardDirectory"): Array<String> {
    try {
        val FlashcardDirectory = File(context.getExternalFilesDir(null), folderName) // Use getExternalFilesDir() here
        if (FlashcardDirectory.exists()) {
            val filenames = FlashcardDirectory.list()
            if (filenames != null) {
                return filenames
            }
        } else {
            Log.d("FilesDir", "No files found in the files directory.")
        }
    } catch (e: Exception) {
        Log.e("FilesDir", "Error reading files directory: ${e.message}")
    }
    return arrayOf()
}

//location of Translations
enum class Location{
    ASSETS, FILES_DIR
}

//type of translation: provided by app creator, user custom or ai translated not yet implemented
enum class TypeOfTranslation{
    Default, Custom, AI
}

//translation entry in translation choose screen
@Serializable
data class Translations(
    val name:String,
    val typeOfTranslation: TypeOfTranslation
) : StateData