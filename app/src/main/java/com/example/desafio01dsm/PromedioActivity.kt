package com.example.desafio01dsm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.DecimalFormat

class PromedioActivity : AppCompatActivity() {

    private lateinit var inputNombreEstudiante: EditText
    private lateinit var inputNota1: EditText
    private lateinit var inputNota2: EditText
    private lateinit var inputNota3: EditText
    private lateinit var inputNota4: EditText
    private lateinit var inputNota5: EditText

    private lateinit var btnCalcularPromedio: Button
    private lateinit var txtResultadoPromedio: TextView
    private lateinit var txtEstadoPromedio: TextView

    private val formatoDecimal = DecimalFormat("0.00")

    companion object {
        private const val CANAL_NOTIFICACION = "canal_promedio"
        private const val ID_NOTIFICACION = 1
        private const val CODIGO_NOTIFICACION = 100

        // Las ponderaciones son diferentes y suman 100%
        private const val PONDERACION_NOTA_1 = 0.10
        private const val PONDERACION_NOTA_2 = 0.15
        private const val PONDERACION_NOTA_3 = 0.20
        private const val PONDERACION_NOTA_4 = 0.25
        private const val PONDERACION_NOTA_5 = 0.30

        private const val NOTA_MINIMA = 0.0
        private const val NOTA_MAXIMA = 10.0
        private const val NOTA_APROBACION = 6.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promedio)

        crearCanalNotificacion()

        inputNombreEstudiante = findViewById(R.id.inputNombreEstudiante)
        inputNota1 = findViewById(R.id.inputNota1)
        inputNota2 = findViewById(R.id.inputNota2)
        inputNota3 = findViewById(R.id.inputNota3)
        inputNota4 = findViewById(R.id.inputNota4)
        inputNota5 = findViewById(R.id.inputNota5)

        btnCalcularPromedio = findViewById(R.id.btnCalcularPromedio)
        txtResultadoPromedio = findViewById(R.id.txtResultadoPromedio)
        txtEstadoPromedio = findViewById(R.id.txtEstadoPromedio)

        btnCalcularPromedio.setOnClickListener {
            procesarPromedio()
        }
    }

    private fun procesarPromedio() {

        val nombre = inputNombreEstudiante.text.toString().trim()

        if (nombre.isEmpty()) {
            inputNombreEstudiante.error = getString(R.string.error_nombre)
            inputNombreEstudiante.requestFocus()
            return
        }

        val nota1 = obtenerNota(inputNota1)
        val nota2 = obtenerNota(inputNota2)
        val nota3 = obtenerNota(inputNota3)
        val nota4 = obtenerNota(inputNota4)
        val nota5 = obtenerNota(inputNota5)

        if (
            nota1 == null ||
            nota2 == null ||
            nota3 == null ||
            nota4 == null ||
            nota5 == null
        ) {
            return
        }

        val promedio = calcularPromedio(
            nota1,
            nota2,
            nota3,
            nota4,
            nota5
        )

        val promedioFormateado = formatoDecimal.format(promedio)
        val aprobado = promedio >= NOTA_APROBACION

        txtResultadoPromedio.text = getString(
            R.string.promedio_final,
            promedioFormateado
        )

        txtEstadoPromedio.text = if (aprobado) {
            getString(R.string.estado_aprobado)
        } else {
            getString(R.string.estado_reprobado)
        }

        enviarNotificacion(
            promedioFormateado,
            aprobado
        )
    }

    private fun obtenerNota(campoNota: EditText): Double? {

        val textoNota = campoNota.text.toString().trim()

        if (textoNota.isEmpty()) {
            campoNota.error = getString(R.string.error_nota_vacia)
            campoNota.requestFocus()
            return null
        }

        val nota = textoNota.toDoubleOrNull()

        if (
            nota == null ||
            nota < NOTA_MINIMA ||
            nota > NOTA_MAXIMA
        ) {
            campoNota.error = getString(R.string.error_nota_rango)
            campoNota.requestFocus()
            return null
        }

        return nota
    }

    private fun calcularPromedio(
        nota1: Double,
        nota2: Double,
        nota3: Double,
        nota4: Double,
        nota5: Double
    ): Double {

        return (nota1 * PONDERACION_NOTA_1) +
                (nota2 * PONDERACION_NOTA_2) +
                (nota3 * PONDERACION_NOTA_3) +
                (nota4 * PONDERACION_NOTA_4) +
                (nota5 * PONDERACION_NOTA_5)
    }

    private fun crearCanalNotificacion() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val canal = NotificationChannel(
                CANAL_NOTIFICACION,
                getString(R.string.titulo_promedio),
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val administrador =
                getSystemService(NotificationManager::class.java)

            administrador.createNotificationChannel(canal)
        }
    }

    private fun enviarNotificacion(
        promedio: String,
        aprobado: Boolean
    ) {

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                CODIGO_NOTIFICACION
            )

            return
        }

        mostrarNotificacion(
            promedio,
            aprobado
        )
    }

    private fun mostrarNotificacion(
        promedio: String,
        aprobado: Boolean
    ) {

        val estado = if (aprobado) {
            getString(R.string.estado_aprobado)
        } else {
            getString(R.string.estado_reprobado)
        }

        val mensaje = getString(
            R.string.notificacion_promedio,
            promedio,
            estado
        )

        val notificacion =
            NotificationCompat.Builder(
                this,
                CANAL_NOTIFICACION
            )
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getString(R.string.titulo_promedio))
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(this).notify(
                ID_NOTIFICACION,
                notificacion
            )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (
            requestCode == CODIGO_NOTIFICACION &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {

            // La notificación se volverá a solicitar
            // al realizar nuevamente el cálculo.
        }
    }
}