package com.example.myapplication

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val TAG = "MainActivity"

    lateinit var adapter: ArrayAdapter<*>

    lateinit var pokedex: Pokedex

    lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pokedex = loadPokedex(applicationContext, "pokedex.json")
        Log.d(TAG, pokedex.toString())

        tts = TextToSpeech(this, this)


        setContentView(R.layout.activity_main)
        val listView: ListView = findViewById<ListView>(R.id.theList)
        val theFilter = findViewById<EditText>(R.id.searchFilter)

        val names = pokedex.map { it.name }

        this.adapter = ArrayAdapter(this, R.layout.list_item_layout, names as List<Any?>)

        listView.adapter = this.adapter

        listView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val name = names[position]
                val eintrag = this@MainActivity.pokedex[position].eintraege.random()
                Toast.makeText(this@MainActivity, eintrag, Toast.LENGTH_LONG).show()
                Log.d(TAG, "onItemClick $name")
                Log.d(TAG, eintrag)
                tts.speak(eintrag, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }

        theFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                this@MainActivity.adapter.filter.filter(charSequence)
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
    }

    fun loadPokedex(context: Context, fileName: String): Pokedex {
        val content: String = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val type: Type = object : TypeToken<Pokedex>() {}.type
        return Gson().fromJson(content, type)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts.setLanguage(Locale.GERMANY)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    public override fun onDestroy() {
        // Shutdown TTS
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

}