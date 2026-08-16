package com.example.desafio01dsm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnEjercicioPromedio: Button
    private lateinit var btnEjercicioSalario: Button
    private lateinit var btnEjercicioCalculadora: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnEjercicioPromedio = findViewById(R.id.btnEjercicioPromedio)
        btnEjercicioSalario = findViewById(R.id.btnEjercicioSalario)
        btnEjercicioCalculadora = findViewById(R.id.btnEjercicioCalculadora)

        btnEjercicioPromedio.setOnClickListener {
            val intent = Intent(this, PromedioActivity::class.java)
            startActivity(intent)
        }

        btnEjercicioSalario.setOnClickListener {
            val intent = Intent(this, SalarioActivity::class.java)
            startActivity(intent)
        }

        btnEjercicioCalculadora.setOnClickListener {
            val intent = Intent(this, CalculadoraActivity::class.java)
            startActivity(intent)
        }
    }
}