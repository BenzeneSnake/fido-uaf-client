package org.ebayopensource.fidouafclient.fp;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.security.Signature;

/**
 * BiometricPrompt 輔助類
 * 統一處理 Android 6.0+ 的生物識別認證
 */
public class BiometricPromptHelper {

    private final Context context;
    private final BiometricAuthListener listener;

    public BiometricPromptHelper(Context context, BiometricAuthListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void authenticate(Signature signature) {

        BiometricPrompt.AuthenticationCallback callback =
                new BiometricPrompt.AuthenticationCallback() {

                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull BiometricPrompt.AuthenticationResult result) {

                        Signature sig = result.getCryptoObject().getSignature();

                        try {
                            listener.onAuthSuccess(sig);
                        } catch (Exception e) {
                            listener.onAuthFailed();
                        }
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        listener.onAuthFailed();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        listener.onAuthError(errorCode, errString);
                    }
                };

        BiometricPrompt biometricPrompt =
                new BiometricPrompt((FragmentActivity) context,
                        ContextCompat.getMainExecutor(context),
                        callback);

        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("生物識別認證")
                        .setSubtitle("使用指紋或臉部辨識進行身份驗證")
                        .setNegativeButtonText("取消")
                        .build();

        biometricPrompt.authenticate(promptInfo,
                new BiometricPrompt.CryptoObject(signature));
    }

    /**
     * 檢查設備是否支援生物識別
     *
     * @return true 如果支援
     */
    public static boolean isBiometricAvailable(FragmentActivity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }

        androidx.biometric.BiometricManager biometricManager =
                androidx.biometric.BiometricManager.from(activity);

        int canAuthenticate = biometricManager.canAuthenticate(
                androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG);

        return canAuthenticate == androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS;
    }
}
