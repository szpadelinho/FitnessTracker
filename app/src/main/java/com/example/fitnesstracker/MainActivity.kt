package com.example.fitnesstracker

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val itemList = mutableListOf<Item>()
    private lateinit var adapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val recyclerView = findViewById<RecyclerView>(R.id.item_list)

        adapter = MyAdapter(itemList) { item, position ->
            showItemDetailsDialog(item, position)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val inputName: EditText = findViewById(R.id.item_name)
        val inputDistance: EditText = findViewById(R.id.item_distance)
        val inputTime: EditText = findViewById(R.id.item_time)
        val inputKcal: EditText = findViewById(R.id.item_kcal)
        val inputDesc: EditText = findViewById(R.id.item_desc)
        val inputIntensity: SeekBar = findViewById(R.id.item_intensity)
        val inputType: RadioGroup = findViewById(R.id.item_type)

        val submitButton: Button = findViewById(R.id.submit_button)

        val inputScoreValue = findViewById<TextView>(R.id.intensity_value_textview)

        inputIntensity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                inputScoreValue.text = when(p1){
                    0 -> "Lekki"
                    1 -> "Średni"
                    2 -> "Intensywny"
                    else -> "Nieznany"
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        submitButton.setOnClickListener{
            val name = inputName.text.toString().trim()
            val distance = inputDistance.text.toString().trim()
            val time = inputTime.text.toString().trim()
            val kcal = inputKcal.text.toString().trim()
            val desc = inputDesc.text.toString().trim()
            val intensity = inputIntensity.progress
            val type = when(inputType.checkedRadioButtonId){
                R.id.item_run -> "Bieg"
                R.id.item_bike -> "Rower"
                R.id.item_swim -> "Pływanie"
                else -> null
            }

            val iconRes = when(type){
                "Bieg" -> R.drawable.baseline_directions_run_24
                "Rower" -> R.drawable.baseline_directions_bike_24
                "Pływanie" -> R.drawable.baseline_water_24
                else -> R.drawable.baseline_question_mark_24
            }

            if(name.isEmpty() || distance.isEmpty() || time.isEmpty() || kcal.isEmpty() || desc.isEmpty() || type == null){
                Toast.makeText(this, "Wypełnij wszystkie pola!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val newItem = Item(
                name = name,
                distance = distance,
                time = time,
                kcal = kcal,
                desc = desc,
                intensity = intensity,
                type = type,
                iconRes = iconRes
            )

            itemList.add(newItem)
            adapter.notifyItemInserted(itemList.size - 1)

            inputName.text.clear()
            inputDistance.text.clear()
            inputTime.text.clear()
            inputKcal.text.clear()
            inputDesc.text.clear()
            inputIntensity.progress = 0
            inputType.clearCheck()
        }

        val saveButton = findViewById<Button>(R.id.save_to_gson_button)

        saveButton.setOnClickListener{
            try{
                ItemJsonManager.saveItemListToJson(this, itemList)
                Toast.makeText(this, "Zapisano dane do pliku", Toast.LENGTH_LONG).show()
            }
            catch(ex: Exception){
                Log.e("save", "Wystąpił błąd podczas zapisywania: $ex")
            }
        }

        val loadButton = findViewById<Button>(R.id.pull_gson_button)

        loadButton.setOnClickListener {
            try{
                val loadedItemList = ItemJsonManager.loadItemListFromJson(this)
                Toast.makeText(this, "Wczytano ${loadedItemList.size} elementów.", Toast.LENGTH_LONG).show()
                itemList.clear()
                itemList.addAll(loadedItemList)
                adapter.notifyDataSetChanged()
            }
            catch(ex: Exception){
                Log.e("load", "Cos poszło nie tak: $ex")
            }
        }
    }

    private fun showItemDetailsDialog(item: Item, position: Int){
        var builder = AlertDialog.Builder(this)
        builder.setTitle(item.name)

        val intensityValue = when(item.intensity){
            0 -> "Lekki"
            1 -> "Średni"
            2 -> "Intensywny"
            else -> "Nieznany"
        }

        builder.setMessage("""
            Nazwa: ${item.name}
            Rodzaj aktywnosci: ${item.type}
            Dystans: ${item.distance}
            Czas: ${item.time}
            Spalone kalorie: ${item.kcal}
            Intensywnosc: ${intensityValue}
            Opis: ${item.desc}
        """.trimIndent())

        builder.setPositiveButton("OK"){dialog, _ ->
            dialog.dismiss()
        }

        builder.setNegativeButton("Usuń"){dialog, _ ->
            itemList.removeAt(position)
            adapter.notifyItemRemoved(position)
            Toast.makeText(this, "Usunięto element $position", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        builder.show()
    }
}