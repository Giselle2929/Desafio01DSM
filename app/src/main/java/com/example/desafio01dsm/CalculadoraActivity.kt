package com.example.desafio01dsm

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class CalculadoraActivity : AppCompatActivity() {

    private lateinit var txtOperacion: TextView
    private lateinit var txtResultado: TextView

    private var numeroActual = ""
    private var primerNumero = 0.0
    private var operador = ""
    private var esperandoSegundoNumero = false

    private val formato = DecimalFormat(
        "0.##########",
        DecimalFormatSymbols(Locale.US)
    )

    companion object {
        private const val ARCHIVO_HISTORIAL = "historial_calculadora.txt"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadora)

        inicializarVistas()
        configurarBotonesNumericos()
        configurarOperaciones()
        configurarBotonesEspeciales()
    }

    private fun inicializarVistas() {
        txtOperacion = findViewById(R.id.txtOperacion)
        txtResultado = findViewById(R.id.txtResultado)

        limpiarPantalla()
    }

    // --------------------------------------------------
    // NÚMEROS
    // --------------------------------------------------

    private fun configurarBotonesNumericos() {

        val botonesNumericos = listOf(
            R.id.btn0,
            R.id.btn1,
            R.id.btn2,
            R.id.btn3,
            R.id.btn4,
            R.id.btn5,
            R.id.btn6,
            R.id.btn7,
            R.id.btn8,
            R.id.btn9
        )

        botonesNumericos.forEach { id ->

            findViewById<Button>(id).setOnClickListener { vista ->

                val boton = vista as Button
                agregarNumero(boton.text.toString())
            }
        }

        findViewById<Button>(R.id.btnPunto).setOnClickListener {
            agregarPunto()
        }
    }

    private fun agregarNumero(numero: String) {

        if (esperandoSegundoNumero) {
            numeroActual = ""
            esperandoSegundoNumero = false
        }

        if (numeroActual == getString(R.string.cero)) {
            numeroActual = ""
        }

        numeroActual += numero

        txtResultado.text = numeroActual
    }

    private fun agregarPunto() {

        if (esperandoSegundoNumero) {
            numeroActual = ""
            esperandoSegundoNumero = false
        }

        val punto = getString(R.string.punto)

        if (numeroActual.contains(punto)) {
            return
        }

        if (numeroActual.isEmpty()) {
            numeroActual = getString(R.string.cero)
        }

        numeroActual += punto
        txtResultado.text = numeroActual
    }

    // --------------------------------------------------
    // OPERACIONES
    // --------------------------------------------------

    private fun configurarOperaciones() {

        findViewById<ImageButton>(R.id.btnSuma).setOnClickListener {
            seleccionarOperacion(getString(R.string.simbolo_suma))
        }

        findViewById<ImageButton>(R.id.btnResta).setOnClickListener {
            seleccionarOperacion(getString(R.string.simbolo_resta))
        }

        findViewById<ImageButton>(R.id.btnMultiplicacion).setOnClickListener {
            seleccionarOperacion(getString(R.string.simbolo_multiplicacion))
        }

        findViewById<ImageButton>(R.id.btnDivision).setOnClickListener {
            seleccionarOperacion(getString(R.string.simbolo_division))
        }

        findViewById<ImageButton>(R.id.btnPotencia).setOnClickListener {
            seleccionarOperacion(getString(R.string.simbolo_exponente))
        }

        findViewById<ImageButton>(R.id.btnRaiz).setOnClickListener {
            calcularRaiz()
        }

        findViewById<Button>(R.id.btnIgual).setOnClickListener {
            calcularResultado()
        }
    }

    private fun seleccionarOperacion(nuevaOperacion: String) {

        if (numeroActual.isBlank()) {
            mostrarMensaje(getString(R.string.error_numero_1))
            return
        }

        val numero = convertirNumero(numeroActual)

        if (numero == null) {
            mostrarMensaje(getString(R.string.error_resultado_invalido))
            return
        }

        primerNumero = numero
        operador = nuevaOperacion
        esperandoSegundoNumero = true

        txtOperacion.text = getString(
            R.string.formato_operacion_parcial,
            formato.format(primerNumero),
            operador
        )
    }

    private fun calcularResultado() {

        if (numeroActual.isBlank()) {
            mostrarMensaje(getString(R.string.error_numero_2))
            return
        }

        if (operador.isBlank()) {
            return
        }

        val segundoNumero = convertirNumero(numeroActual)

        if (segundoNumero == null) {
            mostrarMensaje(getString(R.string.error_resultado_invalido))
            return
        }

        if (
            operador == getString(R.string.simbolo_division) &&
            segundoNumero == 0.0
        ) {
            mostrarMensaje(getString(R.string.error_division_cero))
            return
        }

        val resultado = realizarOperacion(segundoNumero)

        if (!resultado.isFinite()) {
            mostrarMensaje(getString(R.string.error_resultado_invalido))
            return
        }

        val resultadoFormateado = formato.format(resultado)

        val operacionRealizada = getString(
            R.string.formato_operacion,
            formato.format(primerNumero),
            operador,
            formato.format(segundoNumero),
            resultadoFormateado
        )

        mostrarResultado(
            operacionRealizada,
            resultadoFormateado
        )

        guardarHistorial(operacionRealizada)

        numeroActual = resultadoFormateado
        primerNumero = resultado
        operador = ""
        esperandoSegundoNumero = true
    }

    private fun realizarOperacion(segundoNumero: Double): Double {

        return when (operador) {

            getString(R.string.simbolo_suma) -> {
                primerNumero + segundoNumero
            }

            getString(R.string.simbolo_resta) -> {
                primerNumero - segundoNumero
            }

            getString(R.string.simbolo_multiplicacion) -> {
                primerNumero * segundoNumero
            }

            getString(R.string.simbolo_division) -> {
                primerNumero / segundoNumero
            }

            getString(R.string.simbolo_exponente) -> {
                Math.pow(primerNumero, segundoNumero)
            }

            else -> {
                Double.NaN
            }
        }
    }

    // --------------------------------------------------
    // RAÍZ CUADRADA
    // --------------------------------------------------

    private fun calcularRaiz() {

        if (numeroActual.isBlank()) {
            mostrarMensaje(getString(R.string.error_numero_1))
            return
        }

        val numero = convertirNumero(numeroActual)

        if (numero == null) {
            mostrarMensaje(getString(R.string.error_resultado_invalido))
            return
        }

        if (numero < 0.0) {
            mostrarMensaje(getString(R.string.error_raiz_negativa))
            return
        }

        val resultado = Math.sqrt(numero)

        if (!resultado.isFinite()) {
            mostrarMensaje(getString(R.string.error_resultado_invalido))
            return
        }

        val resultadoFormateado = formato.format(resultado)

        val operacionRealizada = getString(
            R.string.formato_raiz,
            formato.format(numero),
            resultadoFormateado
        )

        mostrarResultado(
            operacionRealizada,
            resultadoFormateado
        )

        guardarHistorial(operacionRealizada)

        numeroActual = resultadoFormateado
        primerNumero = resultado
        operador = ""
        esperandoSegundoNumero = true
    }

    // --------------------------------------------------
    // CONVERSIÓN Y RESULTADO
    // --------------------------------------------------

    private fun convertirNumero(valor: String): Double? {
        return valor.toDoubleOrNull()
    }

    private fun mostrarResultado(
        operacion: String,
        resultado: String
    ) {
        txtOperacion.text = operacion
        txtResultado.text = resultado
    }

    // --------------------------------------------------
    // BOTONES ESPECIALES
    // --------------------------------------------------

    private fun configurarBotonesEspeciales() {

        findViewById<Button>(R.id.btnLimpiar).setOnClickListener {
            limpiarCalculadora()
        }

        findViewById<Button>(R.id.btnBorrar).setOnClickListener {
            borrarUltimoCaracter()
        }

        findViewById<Button>(R.id.btnHistorial).setOnClickListener {
            mostrarHistorial()
        }
    }

    private fun limpiarCalculadora() {

        numeroActual = ""
        primerNumero = 0.0
        operador = ""
        esperandoSegundoNumero = false

        limpiarPantalla()
    }

    private fun limpiarPantalla() {

        txtOperacion.text = getString(R.string.resultado)
        txtResultado.text = getString(R.string.cero)
    }

    private fun borrarUltimoCaracter() {

        if (numeroActual.isEmpty()) {
            return
        }

        numeroActual = numeroActual.dropLast(1)

        txtResultado.text =
            if (numeroActual.isEmpty()) {
                getString(R.string.cero)
            } else {
                numeroActual
            }
    }

    // --------------------------------------------------
    // HISTORIAL
    // --------------------------------------------------

    private fun guardarHistorial(operacion: String) {

        try {

            openFileOutput(
                ARCHIVO_HISTORIAL,
                MODE_APPEND
            ).use { archivo ->

                archivo.write(
                    operacion.toByteArray()
                )

                archivo.write(
                    System.lineSeparator().toByteArray()
                )
            }

        } catch (_: Exception) {

            mostrarMensaje(
                getString(R.string.error_guardar_historial)
            )
        }
    }

    private fun obtenerHistorial(): String? {

        return try {

            openFileInput(ARCHIVO_HISTORIAL)
                .bufferedReader()
                .use { lector ->
                    lector.readText()
                }

        } catch (_: Exception) {
            null
        }
    }

    private fun mostrarHistorial() {

        val historial = obtenerHistorial()

        if (historial.isNullOrBlank()) {

            mostrarMensaje(
                getString(R.string.historial_vacio)
            )

            return
        }

        AlertDialog.Builder(this)
            .setTitle(
                getString(R.string.titulo_historial)
            )
            .setMessage(historial)
            .setPositiveButton(
                getString(R.string.cerrar),
                null
            )
            .setNegativeButton(
                getString(R.string.borrar_historial)
            ) { _, _ ->
                eliminarHistorial()
            }
            .show()
    }

    private fun eliminarHistorial() {

        deleteFile(ARCHIVO_HISTORIAL)

        mostrarMensaje(
            getString(R.string.historial_eliminado)
        )
    }

    // --------------------------------------------------
    // MENSAJES
    // --------------------------------------------------

    private fun mostrarMensaje(mensaje: String) {

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }
}