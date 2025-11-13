# FIDO UAF Android Client
## Overview

This is an Android client application for FIDO UAF (Universal Authentication Framework).
## Attribution

This project is based on and incorporates code from:

 **eBay UAF (Universal Authentication Framework)**
   - Original repository: https://github.com/eBay/UAF
   - Copyright (c) 2015 eBay Inc.
   - Licensed under the Apache License, Version 2.0
   - Provides the reference implementation of the FIDO UAF protocol.
   - Portions of this project are derived from or based on the original eBay UAF implementation.

All original works are used in accordance with their respective Apache 2.0 licenses.

**Prerequisites:**

1. **Java Development Kit (JDK)**:
   - version: JDK17 or above
   - Download: https://adoptium.net/

2. **Android SDK**
   - It is recommended to use Android Studio (which will automatically install the SDK)
   - Download: https://developer.android.com/studio
3. **Androiddevice or emulator**
   - Minimum system version: Android 6.0 (API 23)
   - It is recommended to use a physical device for testing FIDO features

### Optional Tools

- Android Studio – Recommended, provides a full-featured IDE
- VS Code – Can be used with Android plugins

---

## Key Modifications

This derivative work includes the following enhancements:

* **Gradle Upgraded to 8.7.3**

## Getting Started

### step 1：Set Android SDK Path

```bash
# Copy the template file
cp local.properties.template local.properties

# Edit local.properties to set your SDK path
# Windows example：
# sdk.dir=C:\\Users\\YourName\\AppData\\Local\\Android\\Sdk

# WSL example：
# sdk.dir=/mnt/c/Users/YourName/AppData/Local/Android/Sdk
```

### step 2：Sync Gradle Dependencies

```bash
# Linux/Mac/WSL
./gradlew build

# Windows (CMD or PowerShell)
gradlew.bat build
```
The first time you run this, Gradle 8.9 and all the dependencies will be downloaded，which may take some time.

### step 3：Connect an Android Device

#### Use a Physical Device

1. Enable **Developer** Options on your phone
2. Enable USB Debugging
3. Connect your phone to the computer via USB
4. Verify the connection
  ```bash
   # Check if the device is connected (requires Android SDK platform-tools to be installed)
   adb devices
   ```
### Step 4: Install the App on Your Device

```bash
# Build and install Debug version to the connected device
./gradlew installDebug

# Or use Android Studio：
# Run → Run 'app' (Shift+F10)
```

---

## 📦 Common Commands

### Common Gradle Tasks 

The following commends should be executed in the `fidouafclient` directory:

```bash
# Clean build artifacts
./gradlew clean

# Build project
./gradlew build

# Build Debug APK
./gradlew assembleDebug

# Build and Release APK（assiment config is required）
./gradlew assembleRelease

# Install Debug Version on the Device
./gradlew installDebug

# Install and run the app
./gradlew installDebug && adb shell am start -n org.ebayopensource.fidouafclient/.MainActivity

# Uninstall the app
./gradlew uninstallDebug

# List all available tasks
./gradlew tasks

# View the project dependency tree
./gradlew dependencies

# Sync dependencies (without building)
./gradlew --refresh-dependencies
```

### APK Location

After compilation,the APK files location are located at:
```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release.apk
```

### Manually installing APK

```bash
# Install APK to the connected device
adb install app/build/outputs/apk/debug/app-debug.apk

# Reinstall (keep existing data)
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📁 Project Structure

```
fidouafclient/
├── app/                                    # App Module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/                      # Java Source code
│   │   │   │   └── org/ebayopensource/
│   │   │   │       ├── fido/uaf/client/   # Core FIDO UAF Logic
│   │   │   │       ├── fido/uaf/crypto/   # Cryptography Related
│   │   │   │       └── fidouafclient/     # UI and Activity
│   │   │   ├── res/                       # Resource Files (Images, Layouts, Strings)
│   │   │   └── AndroidManifest.xml        # App Manifest File
│   │   └── test/                          # Unit Tests
│   ├── build.gradle                       # Gradle Configuration for App Module
│   └── proguard-rules.pro                 # Code Obfuscation Rules
├── gradle/                                # Gradle Wrapper
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties       # Gradle Version Configuration
├── build.gradle                           # Project-level Gradle Configuration
├── settings.gradle                        # Project Settings
├── gradle.properties                      # Configuration file used by the Gradle build system 
├── gradlew                                # Gradle Wrapper Script (Linux/Mac)
├── gradlew.bat                            # Gradle Wrapper Script (Windows)
├── local.properties.template              # SDK Path Template
├── .gitignore                             # Git Ignore Rules
└── README.md                              # This Document
```

---

## 🔍 Gradle Configuration Overview

### Project-level `build.gradle`

Defines the build configuration for the entire project, including:
- Plugin version (Android Gradle Plugin 8.7.3)
- Maven repositories (Google, Maven Central)

### App module `app/build.gradle`

Specifies settings for the app itself, including:
- **compileSdk**: Android SDK version used for compilation (34 = Android 14)
- **minSdk**: Minimum supported Android version (23 = Android 6.0)
- **targetSdk**: Target Android version (34 = Android 14)
- **applicationId**: The app’s unique ID
- **dependencies**: Libraries the app depends on (similar to Maven’s `<dependencies>`)

### Main Dependencies

| Library Name       | Version  | Purpose                                         |
| ------------------ | -------- | ----------------------------------------------- |
| androidx.appcompat | 1.6.1    | Android compatibility library                   |
| gson               | 2.10.1   | JSON parsing                                    |
| spongycastle       | 1.58.0.0 | Cryptography library (BouncyCastle for Android) |
| commons-codec      | 1.16.0   | Encoding/decoding utilities                     |

---

## 🐛 Bug fix

### Problem 1： Not Set `ANDROID_HOME` or `sdk.dir`

**Error Message:**：
```
SDK location not found. Define a valid SDK location with an ANDROID_HOME environment variable or by setting the sdk.dir path in your project's local properties file
```
**Solution**：
1. Copy `local.properties.template` to `local.properties`
2. Set the correct SDK path.

### Problem 2：`minSdkVersion` Related Error

**Error Message:**：
```
Manifest merger failed : uses-sdk:minSdkVersion XX cannot be smaller than version YY
```

**Solution**：Check `minSdkVersion` of `AndroidManifest.xml` is the same as `app/build.gradle`

### Problem 3： Dependencies Download Failed

**Solution**：
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### Problem 4：Device Not Recognized

**Solution**：
```bash
# Restart  ADB Restart 
adb kill-server
adb start-server
adb devices
```
### Problem 5：App Installation Failed（Signature Conflict）

**Error Message:**：
```
INSTALL_FAILED_UPDATE_INCOMPATIBLE
```

**Solution**：
```bash
# Completely uninstall the old version of the app
adb uninstall org.ebayopensource.fidouafclient
# Reinstall
./gradlew installDebug
```

---

## 📝 Development Notes

### Modify the Server URL

The app needs to connect to your FIDO UAF Server. Locate where the server URL is configured and update it:

```java
// File location: app/src/main/java/org/ebayopensource/fidouafclient/...
// Search keywords: "http", "localhost", or "server"
```

### Using Android Studio

1. Open Android Studio
2. Go to File → Open → select the fidouafclient folder
3. Wait for Gradle to finish syncing
4. Run → Run 'app'

### View Logcat Logs

```bash
# View real-time app logs
adb logcat | grep "fidouafclient"

# Or check the Logcat window in Android Studio
```

---

## 🎯 Backend Integration

This Android client must work together with the FIDO UAF Server:

1. **Run the FIDO UAF Server**
   ```bash
   cd ../fido-uaf-server
   mvn spring-boot:run
   ```

2. **Modify the server address in the app**
    - If you're using a physical device, use your computer's IP address instead of localhost.
    - Example：`http://1XX.XXX.X.XXX:8080`

3. **Configure Network Security (for HTTP communication)**

   To allow HTTP communication with the server (for testing purposes), you need to modify the network security configuration:

   **File location**: `app/src/debug/res/xml/network_security_config.xml`

   Update the domain to your computer's IPv4 address:
   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <network-security-config>
       <!-- Allow cleartext HTTP -->
       <domain-config cleartextTrafficPermitted="true">
           <domain includeSubdomains="true">YOUR_IPV4_ADDRESS</domain>
       </domain-config>
   </network-security-config>
   ```

   Replace `YOUR_IPV4_ADDRESS` with your actual IPv4 address (e.g., `192.168.1.100`).

   **How to find your IPv4 address:**
   - **Windows**: Run `ipconfig` in Command Prompt
   - **Linux/Mac**: Run `ifconfig` or `ip addr`
   - **WSL**: Run `ip addr show eth0` or check Windows host IP

4. **Ensure network connectivity**
    - The phone and computer must be on the same Wi-Fi network.
    - Make sure the firewall allows port 8080 connections.

---

## 📚 Related Resources

- [Android Developer Documentation](https://developer.android.com/docs)
- [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide.html)
- [FIDO UAF Specifications](https://fidoalliance.org/specifications/)

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
