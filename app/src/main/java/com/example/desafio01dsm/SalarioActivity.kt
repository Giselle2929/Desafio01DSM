package com.example.desafio01dsm

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class SalarioActivity : AppCompatActivity() {

    private lateinit var inputNombreEmpleado: EditText
    private lateinit var inputSalarioBase: EditText

    private lateinit var btnCalcularSalario: Button

    private lateinit var txtNombreEmpleado: TextView
    private lateinit var txtSalarioBruto: TextView
    private lateinit var txtDescuentoRenta: TextView
    private lateinit var txtDescuentoAfp: TextView
    private lateinit var txtDescuentoIsss: TextView
    private lateinit var txtSalarioNeto: TextView

    private val formatoDecimal = DecimalFormat("0.00")

    companion object {

        // Porcentajes de descuento
        private const val PORCENTAJE_AFP = 0.0725
        private const val PORCENTAJE_ISSS = 0.03

        // Límites para el cálculo de renta
        private const val LIMITE_RENTA_1 = 472.00
        private const val LIMITE_RENTA_2 = 895.24
        private const val LIMITE_RENTA_3 = 2038.10

        // Porcentajes de renta
        private const val PORCENTAJE_RENTA_2 = 0.10
        private const val PORCENTAJE_RENTA_3 = 0.20
        private const val PORCENTAJE_RENTA_4 = 0.30

        // Cuotas fijas de renta
        private const val CUOTA_RENTA_2 = 17.67
        private const val CUOTA_RENTA_3 = 60.00
        private const val CUOTA_RENTA_4 = 288.57
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salario)

        inputNombreEmpleado = findViewById(R.id.inputNombreEmpleado)
        inputSalarioBase = findViewById(R.id.inputSalarioBase)

        btnCalcularSalario = findViewById(R.id.btnCalcularSalario)

        txtNombreEmpleado = findViewById(R.id.txtNombreEmpleado)
        txtSalarioBruto = findViewById(R.id.txtSalarioBruto)
        txtDescuentoRenta = findViewById(R.id.txtDescuentoRenta)
        txtDescuentoAfp = findViewById(R.id.txtDescuentoAfp)
        txtDescuentoIsss = findViewById(R.id.txtDescuentoIsss)
        txtSalarioNeto = findViewById(R.id.txtSalarioNeto)

        btnCalcularSalario.setOnClickListener {
            procesarSalario()
        }
    }

    private fun procesarSalario() {

        val nombreEmpleado = inputNombreEmpleado.text.toString().trim()

        if (nombreEmpleado.isEmpty()) {
            inputNombreEmpleado.error = getString(R.string.error_empleado)
            inputNombreEmpleado.requestFocus()
            return
        }

        val textoSalario = inputSalarioBase.text.toString().trim()

        if (textoSalario.isEmpty()) {
            inputSalarioBase.error = getString(R.string.error_salario)
            vibrarDispositivo()
            inputSalarioBase.requestFocus()
            return
        }

        val salarioBase = textoSalario.toDoubleOrNull()

        if (salarioBase == null || salarioBase <= 0.0) {
            inputSalarioBase.error = getString(R.string.error_salario)
            vibrarDispositivo()
            inputSalarioBase.requestFocus()
            return
        }

        txtNombreEmpleado.text = getString(
            R.string.empleado_resultado,
            nombreEmpleado
        )

        val renta = calcularRenta(salarioBase)
        val afp = salarioBase * PORCENTAJE_AFP
        val isss = salarioBase * PORCENTAJE_ISSS
        val salarioNeto = salarioBase - renta - afp - isss

        txtSalarioBruto.text = getString(
            R.string.salario_bruto,
            formatoDecimal.format(salarioBase)
        )

        txtDescuentoRenta.text = getString(
            R.string.descuento_renta,
            formatoDecimal.format(renta)
        )

        txtDescuentoAfp.text = getString(
            R.string.descuento_afp,
            formatoDecimal.format(afp)
        )

        txtDescuentoIsss.text = getString(
            R.string.descuento_isss,
            formatoDecimal.format(isss)
        )

        txtSalarioNeto.text = getString(
            R.string.salario_neto,
            formatoDecimal.format(salarioNeto)
        )
    }

    private fun calcularRenta(salarioBase: Double): Double {
        return when {
            salarioBase <= LIMITE_RENTA_1 -> 0.0

            salarioBase <= LIMITE_RENTA_2 ->
                (salarioBase - LIMITE_RENTA_1) * PORCENTAJE_RENTA_2 +
                        CUOTA_RENTA_2

            salarioBase <= LIMITE_RENTA_3 ->
                (salarioBase - LIMITE_RENTA_2) * PORCENTAJE_RENTA_3 +
                        CUOTA_RENTA_3

            else ->
                (salarioBase - LIMITE_RENTA_3) * PORCENTAJE_RENTA_4 +
                        CUOTA_RENTA_4
        }
    }

    private fun vibrarDispositivo() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            val administrador =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

            administrador.defaultVibrator.vibrate(
                VibrationEffect.createOneShot(
                    300,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )

        } else {

            @Suppress("DEPRECATION")
            val vibrador =
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                vibrador.vibrate(
                    VibrationEffect.createOneShot(
                        300,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )

            } else {

                @Suppress("DEPRECATION")
                vibrador.vibrate(300)
            }
        }
    }
}