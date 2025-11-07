# FIDO UAF Android Client

這是一個 FIDO UAF (Universal Authentication Framework) 的 Android 客戶端應用程式。

## 📋 目錄

- [環境需求](#環境需求)
- [快速開始](#快速開始)
- [常用指令](#常用指令)
- [專案結構](#專案結構)
- [故障排除](#故障排除)

---

## 🔧 環境需求

### 必要條件

1. **Java Development Kit (JDK)**
   - 版本：JDK 17 或更高
   - 下載：https://adoptium.net/

2. **Android SDK**
   - 建議使用 Android Studio（會自動安裝 SDK）
   - 下載：https://developer.android.com/studio
   - 最低需求：Android SDK 34（Android 14）

3. **Android 裝置或模擬器**
   - 最低系統版本：Android 6.0（API 23）
   - 建議使用實體手機進行 FIDO 功能測試

### 可選工具

- **Android Studio** - 推薦使用，提供完整的 IDE 功能
- **VS Code** - 可搭配 Android 插件使用

---

## 🚀 快速開始

### 步驟 1：設定 Android SDK 路徑

```bash
# 複製模板文件
cp local.properties.template local.properties

# 編輯 local.properties，設定您的 SDK 路徑
# Windows 範例：
# sdk.dir=C:\\Users\\YourName\\AppData\\Local\\Android\\Sdk

# WSL 範例：
# sdk.dir=/mnt/c/Users/YourName/AppData/Local/Android/Sdk
```

### 步驟 2：同步 Gradle 依賴

```bash
# Linux/Mac/WSL
./gradlew build

# Windows (命令提示字元或 PowerShell)
gradlew.bat build
```

首次執行會下載 Gradle 8.9 和所有依賴，需要一些時間。

### 步驟 3：連接 Android 裝置

#### 使用實體手機

1. 在手機上啟用**開發者選項**
   - 設定 → 關於手機 → 連點「版本號碼」7 次

2. 啟用 **USB 偵錯**
   - 設定 → 開發者選項 → USB 偵錯

3. 用 USB 連接手機到電腦

4. 驗證連接
   ```bash
   # 確認裝置已連接（需要先安裝 Android SDK platform-tools）
   adb devices
   ```

#### 使用 Android 模擬器

1. 在 Android Studio 中啟動 AVD Manager
2. 創建虛擬裝置（建議 Pixel 5，Android 13+）
3. 啟動模擬器

### 步驟 4：安裝 App 到手機

```bash
# 編譯並安裝 Debug 版本到連接的裝置
./gradlew installDebug

# 或者使用 Android Studio：
# Run → Run 'app' (Shift+F10)
```

---

## 📦 常用指令

### Gradle 常用任務

以下指令都在 `fidouafclient` 目錄下執行：

```bash
# 清理構建產物（類似 Maven 的 clean）
./gradlew clean

# 編譯專案
./gradlew build

# 編譯 Debug APK
./gradlew assembleDebug

# 編譯 Release APK（需要簽名配置）
./gradlew assembleRelease

# 安裝 Debug 版本到裝置
./gradlew installDebug

# 安裝並執行
./gradlew installDebug && adb shell am start -n org.ebayopensource.fidouafclient/.MainActivity

# 卸載 App
./gradlew uninstallDebug

# 查看所有可用任務
./gradlew tasks

# 查看專案依賴樹
./gradlew dependencies

# 同步依賴（不編譯）
./gradlew --refresh-dependencies
```

### APK 位置

編譯完成後，APK 檔案位於：
```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release.apk
```

### 手動安裝 APK

```bash
# 安裝到連接的裝置
adb install app/build/outputs/apk/debug/app-debug.apk

# 覆蓋安裝（保留資料）
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📁 專案結構

```
fidouafclient/
├── app/                                    # App 模組
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/                      # Java 源碼
│   │   │   │   └── org/ebayopensource/
│   │   │   │       ├── fido/uaf/client/   # FIDO UAF 核心邏輯
│   │   │   │       ├── fido/uaf/crypto/   # 加密相關
│   │   │   │       └── fidouafclient/     # UI 和 Activity
│   │   │   ├── res/                       # 資源文件（圖片、佈局、字串）
│   │   │   └── AndroidManifest.xml        # App 清單文件
│   │   └── test/                          # 單元測試
│   ├── build.gradle                       # App 模組的 Gradle 配置
│   └── proguard-rules.pro                 # 程式碼混淆規則
├── gradle/                                # Gradle Wrapper
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties       # Gradle 版本配置
├── build.gradle                           # 專案層級的 Gradle 配置
├── settings.gradle                        # 專案設定
├── gradle.properties                      # 給 Gradle 構建系統使用的配置文件
├── gradlew                                # Gradle Wrapper 腳本（Linux/Mac）
├── gradlew.bat                            # Gradle Wrapper 腳本（Windows）
├── local.properties.template              # SDK 路徑模板
├── .gitignore                             # Git 忽略規則
└── README_ZH-TW.md                        # 本文件
```

---

## 🔍 Gradle 配置說明

### 專案層級 `build.gradle`

定義整個專案的構建配置，包括：
- 插件版本（Android Gradle Plugin 8.7.3）
- Maven 倉庫（Google、Maven Central）

### App 模組 `app/build.gradle`

定義 App 的具體設定，包括：
- **compileSdk**: 編譯使用的 Android SDK 版本（34 = Android 14）
- **minSdk**: 最低支援的 Android 版本（23 = Android 6.0）
- **targetSdk**: 目標 Android 版本（34 = Android 14）
- **applicationId**: App 的唯一 ID
- **dependencies**: 依賴庫（類似 Maven 的 `<dependencies>`）

### 主要依賴庫

| Library Name       | Version  | Purpose                                         |
| ------------------ | -------- | ----------------------------------------------- |
| androidx.appcompat | 1.6.1    | Android compatibility library                   |
| gson               | 2.10.1   | JSON parsing                                    |
| spongycastle       | 1.58.0.0 | Cryptography library (BouncyCastle for Android) |
| commons-codec      | 1.16.0   | Encoding/decoding utilities                     |

---

## 🐛 故障排除

### 問題 1：`ANDROID_HOME` 或 `sdk.dir` 未設定

**錯誤訊息**：
```
SDK location not found. Define a valid SDK location with an ANDROID_HOME environment variable or by setting the sdk.dir path in your project's local properties file
```

**解決方案**：
1. 複製 `local.properties.template` 為 `local.properties`
2. 設定正確的 SDK 路徑

### 問題 2：`minSdkVersion` 相關錯誤

**錯誤訊息**：
```
Manifest merger failed : uses-sdk:minSdkVersion XX cannot be smaller than version YY
```

**解決方案**：檢查 `AndroidManifest.xml` 中的 `minSdkVersion` 是否與 `app/build.gradle` 一致。

### 問題 3：依賴下載失敗

**解決方案**：
```bash
# 清理並重新下載依賴
./gradlew clean
./gradlew build --refresh-dependencies
```

### 問題 4：裝置未被識別

**解決方案**：
```bash
# 重啟 ADB 服務
adb kill-server
adb start-server
adb devices
```

### 問題 5：App 安裝失敗（簽名衝突）

**錯誤訊息**：
```
INSTALL_FAILED_UPDATE_INCOMPATIBLE
```

**解決方案**：
```bash
# 完全卸載舊版 App
adb uninstall org.ebayopensource.fidouafclient
# 重新安裝
./gradlew installDebug
```

---

## 📝 開發注意事項

### 修改伺服器 URL

App 需要連接到您的 FIDO UAF Server。找到設定伺服器 URL 的位置並修改：

```java
// 檔案位置：app/src/main/java/org/ebayopensource/fidouafclient/...
// 搜尋關鍵字："http" 或 "localhost" 或 "server"
```

### 使用 Android Studio

1. 開啟 Android Studio
2. File → Open → 選擇 `fidouafclient` 資料夾
3. 等待 Gradle 同步完成
4. Run → Run 'app'

### 查看 Logcat 日誌

```bash
# 查看 App 的即時日誌
adb logcat | grep "fidouafclient"

# 或在 Android Studio 的 Logcat 視窗查看
```

---

## 🎯 與後端整合

此 Android Client 需要配合 FIDO UAF Server 使用：

1. **啟動後端伺服器**
   ```bash
   cd ../fido-uaf-server
   mvn spring-boot:run
   ```

2. **修改 App 中的伺服器位址**
   - 如果使用實體手機，需要使用電腦的 IP 位址（而非 localhost）
   - 例如：`http://1XX.XXX.X.XXX:8080`

3. **確保網路可達**
   - 手機和電腦在同一個 Wi-Fi 網路
   - 防火牆允許連接埠 8080

---

## 📚 相關資源

- [Android 開發者文件](https://developer.android.com/docs)
- [Gradle 使用者手冊](https://docs.gradle.org/current/userguide/userguide.html)
- [FIDO UAF 規範](https://fidoalliance.org/specifications/)

---

## 📄 License

This project includes code derived from [eBay UAF](https://github.com/eBay/UAF),
which is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

All modifications made to the original source are noted within the code and documentation.
© 2025 YourName. Licensed under the Apache License, Version 2.0.
### Important License Information

This is a derivative work incorporating code from:
- **jgrams/webauthn_java_spring_demo** (Apache 2.0)
- **eBay/UAF** (Apache 2.0)

All modifications and enhancements are also released under Apache 2.0. When using this code, you must:
1. Retain all copyright notices from original works
2. Include a copy of the Apache License 2.0
3. State any significant modifications made to the original code
4. Ensure compliance with the Apache License 2.0 terms

For detailed attribution and third-party notices, see the [LICENSE](LICENSE) file.

## Disclaimer

This software is provided "AS IS" without warranty of any kind. The authors and contributors are not liable for any damages arising from the use of this software. See the LICENSE file for complete terms and conditions.
