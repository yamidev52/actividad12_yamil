package com.yamidev.mapas;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

public class BiometricHelper {

    public interface BiometricCallback {
        void onAuthenticationSuccess();
        void onAuthenticationError(@NonNull String error);
        void onAuthenticationFailed();
    }

    private final FragmentActivity activity;
    private final BiometricCallback callback;
    private final Executor executor;
    private BiometricPrompt biometricPrompt;

    public BiometricHelper(FragmentActivity activity, BiometricCallback callback) {
        this.activity = activity;
        this.callback = callback;
        this.executor = ContextCompat.getMainExecutor(activity);
        initBiometricPrompt();
    }

    private void initBiometricPrompt() {
        biometricPrompt = new BiometricPrompt(activity, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode,
                                                      @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (callback != null) {
                            callback.onAuthenticationError(errString.toString());
                        }
                    }

                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        if (callback != null) {
                            callback.onAuthenticationSuccess();
                        }
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        if (callback != null) {
                            callback.onAuthenticationFailed();
                        }
                    }
                });
    }

    /**
     * Verifica si se puede usar la autenticación biométrica (huella/rostro) en el dispositivo.
     */
    public boolean canUseBiometric() {
        BiometricManager biometricManager = BiometricManager.from(activity);
        int canAuth = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
        );

        switch (canAuth) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
            default:
                return false;
        }
    }

    /**
     * Muestra el diálogo de autenticación biométrica.
     */
    public void showBiometricPrompt(String title, String subtitle, String negativeButtonText) {
        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder()
                        .setTitle(title)
                        .setSubtitle(subtitle)
                        // Solo biometría, sin PIN/patrón/contraseña del dispositivo
                        .setNegativeButtonText(negativeButtonText)
                        .build();

        biometricPrompt.authenticate(promptInfo);
    }
}