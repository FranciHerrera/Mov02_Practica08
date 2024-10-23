package com.example.mov2_sensores

import android.annotation.SuppressLint
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var detalle: TextView
    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor
    private lateinit var imageView: ImageView

    private var existeSensorProximidad: Boolean = false
    private lateinit var listadoSensores: List<Sensor>

    private var sensorLuz: Sensor? = null
    private var sensorAcelerometro: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        detalle = findViewById(R.id.textView)
        imageView = findViewById(R.id.imageView)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    }

    // Método para listar sensores
    @SuppressLint("SetTextI18n")
    fun clickListado(view: View?) {
        listadoSensores = sensorManager.getSensorList(Sensor.TYPE_ALL)
        detalle.text = "Lista de sensores del dispositivo"
        for (sensor in listadoSensores) {
            detalle.text = "${detalle.text}\nNombre: ${sensor.name}\nVersión: ${sensor.version}"
        }
    }

    @SuppressLint("SetTextI18n")
    fun clickMangnetico(view: View?) {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            Toast.makeText(applicationContext, "El dispositivo tiene sensor magnético.", Toast.LENGTH_SHORT).show()
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!!
            detalle.setBackgroundColor(Color.GRAY)
            detalle.text = "Propiedades del sensor Magnético:\nNombre: ${sensor.name}\nVersión: ${sensor.version}\nFabricante: ${sensor.vendor}"
        } else {
            Toast.makeText(applicationContext, "El dispositivo no cuenta con sensor magnético.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun clickProximidad(view: View?) {
        val proximidadSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        if (proximidadSensor != null) {
            sensor = proximidadSensor
            existeSensorProximidad = true
            detalle.text = "El dispositivo tiene sensor de proximidad: ${sensor.name}"
            detalle.setBackgroundColor(Color.GREEN)

            // Registrar el sensor de proximidad
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            detalle.text = "No se cuenta con sensor de proximidad"
            existeSensorProximidad = false
        }
    }

    // Nuevo: Método para manejar el sensor de luz
    @SuppressLint("SetTextI18n")
    fun clickLuz(view: View?) {
        sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (sensorLuz != null) {
            sensorManager.registerListener(this, sensorLuz, SensorManager.SENSOR_DELAY_NORMAL)
            detalle.text = "Sensor de luz activado"
        } else {
            Toast.makeText(this, "Sensor de luz no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    // Nuevo: Método para manejar el acelerómetro
    @SuppressLint("SetTextI18n")
    fun clickAcelerometro(view: View?) {
        sensorAcelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (sensorAcelerometro != null) {
            sensorManager.registerListener(this, sensorAcelerometro, SensorManager.SENSOR_DELAY_NORMAL)
            detalle.text = "Acelerómetro activado"
        } else {
            Toast.makeText(this, "Acelerómetro no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            // Lógica para el sensor de proximidad
        } else if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val luz = event.values[0]
            detalle.text = "Nivel de luz: $luz"

            // Cambiar imagen dependiendo del valor de luz
            if (luz > 10000) {
                imageView.setImageResource(R.drawable.topo2)
            } else {
                imageView.setImageResource(R.drawable.topo)
            }
        } else if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            detalle.text = "Aceleración en X: $x, Y: $y, Z: $z"

            // Cambiar imagen dependiendo del valor de aceleración
            if (x > 5) {
                imageView.setImageResource(R.drawable.topo2)
            } else {
                imageView.setImageResource(R.drawable.topo)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se necesita manejar este método
    }

    override fun onResume() {
        super.onResume()
        // Registrar sensores si están activados
        sensorLuz?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
        sensorAcelerometro?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    override fun onPause() {
        super.onPause()
        // Desregistrar sensores para optimizar la batería
        sensorManager.unregisterListener(this)
    }
}
