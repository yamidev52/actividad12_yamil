package com.yamidev.mapas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private ImageView fingerprintIcon;
    private TextView titleText;
    private TextView descriptionText;
    private TextView statusText;
    private MaterialButton authenticateButton;

    private BiometricHelper biometricHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // usa el layout que me mandaste

        // Referencias al layout
        fingerprintIcon = findViewById(R.id.fingerprintIcon);
        titleText = findViewById(R.id.titleText);
        descriptionText = findViewById(R.id.descriptionText);
        statusText = findViewById(R.id.statusText);
        authenticateButton = findViewById(R.id.authenticateButton);

        // Inicializar helper biométrico
        biometricHelper = new BiometricHelper(this, new BiometricHelper.BiometricCallback() {
            @Override
            public void onAuthenticationSuccess() {
                statusText.setText("Autenticación correcta ✅");
                Toast.makeText(MainActivity.this,
                        "Huella reconocida, acceso concedido",
                        Toast.LENGTH_SHORT).show();

                // Aquí podrías abrir otra Activity, por ejemplo:
                // startActivity(new Intent(MainActivity.this, HomeActivity.class));
                // finish();
            }

            @Override
            public void onAuthenticationError(@NonNull String error) {
                statusText.setText("Error: " + error);
                Toast.makeText(MainActivity.this,
                        "Error de autenticación: " + error,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                statusText.setText("Huella no reconocida ❌, intenta de nuevo");
                Toast.makeText(MainActivity.this,
                        "Huella no coincidente",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Verificar si el dispositivo soporta biometría
        if (!biometricHelper.canUseBiometric()) {
            authenticateButton.setEnabled(false);
            statusText.setText("La autenticación biométrica no está disponible");
        } else {
            statusText.setText(getString(R.string.ready_to_authenticate));
        }

        // Click en el botón
        authenticateButton.setOnClickListener(view -> {
            statusText.setText("Coloca tu huella en el sensor...");
            biometricHelper.showBiometricPrompt(
                    getString(R.string.auth_title),
                    getString(R.string.auth_description),
                    "Cancelar"
            );
        });
    }
}