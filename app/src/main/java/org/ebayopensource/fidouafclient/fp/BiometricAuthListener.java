package org.ebayopensource.fidouafclient.fp;

import java.security.Signature;

/**
 * 生物識別認證回調接口
 */
public interface BiometricAuthListener {

    /**
     * 認證成功
     *
     * @param signature 已認證的 Signature 對象（帶有認證令牌）
     */
    void onAuthSuccess(Signature signature);

    /**
     * 認證失敗
     */
    void onAuthFailed();

    /**
     * 認證錯誤
     *
     * @param errorCode 錯誤代碼
     * @param errString 錯誤訊息
     */
    void onAuthError(int errorCode, CharSequence errString);
}
