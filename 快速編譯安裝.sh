#!/bin/bash
# ================================================================
# FIDO UAF Client - 快速編譯安裝腳本
# 此腳本會自動編譯並安裝 App 到連接的 Android 裝置
# ================================================================

set -e  # 遇到錯誤立即停止

echo "=========================================="
echo "FIDO UAF Android Client - 快速編譯安裝"
echo "=========================================="
echo ""

# 檢查是否存在 local.properties
if [ ! -f "local.properties" ]; then
    echo "❌ 錯誤：找不到 local.properties"
    echo ""
    echo "請執行以下步驟："
    echo "1. 複製模板文件："
    echo "   cp local.properties.template local.properties"
    echo ""
    echo "2. 編輯 local.properties，設定您的 Android SDK 路徑"
    echo "   例如：sdk.dir=/mnt/c/Users/YourName/AppData/Local/Android/Sdk"
    echo ""
    exit 1
fi

echo "✅ 找到 local.properties"
echo ""

# 檢查 Android 裝置連接
echo "🔍 檢查 Android 裝置連接..."
if command -v adb &> /dev/null; then
    DEVICES=$(adb devices | grep -v "List" | grep "device$" | wc -l)
    if [ "$DEVICES" -eq 0 ]; then
        echo "⚠️  警告：未檢測到連接的 Android 裝置"
        echo ""
        echo "請確認："
        echo "1. 手機已用 USB 連接到電腦"
        echo "2. 已啟用 USB 偵錯模式"
        echo "3. 在手機上允許 USB 偵錯授權"
        echo ""
        read -p "是否繼續編譯（不安裝）？ (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
        SKIP_INSTALL=true
    else
        echo "✅ 檢測到 $DEVICES 個 Android 裝置"
        adb devices
        echo ""
        SKIP_INSTALL=false
    fi
else
    echo "⚠️  未安裝 adb 工具"
    echo "將只進行編譯，不會安裝到裝置"
    SKIP_INSTALL=true
fi

# 清理舊的構建產物
echo "🧹 清理舊的構建產物..."
./gradlew clean
echo ""

# 編譯 Debug APK
echo "🔨 編譯 Debug APK..."
./gradlew assembleDebug
echo ""

if [ "$SKIP_INSTALL" = false ]; then
    # 安裝到裝置
    echo "📱 安裝 App 到裝置..."
    ./gradlew installDebug
    echo ""

    echo "✅ 安裝完成！"
    echo ""
    echo "🚀 啟動 App..."
    adb shell am start -n org.ebayopensource.fidouafclient/.MainActivity
    echo ""
    echo "=========================================="
    echo "✨ 完成！App 已安裝並啟動"
    echo "=========================================="
else
    echo "✅ 編譯完成！"
    echo ""
    echo "APK 位置："
    echo "  app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "手動安裝指令："
    echo "  adb install app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "=========================================="
    echo "✨ 編譯完成"
    echo "=========================================="
fi

echo ""
echo "📝 查看日誌："
echo "  adb logcat | grep fidouafclient"
echo ""
