#!/usr/bin/env bash
gradlew assembleDebug
java -jar crawl_launcher.jar --apk-file app/build/outputs/apk/debug/app-debug.apk --android-sdk $ANDROID_HOME
# java -jar crawl_launcher.jar --app-package-name com.a494studios.koreanconjugator --android-sdk C:/Users/akash/AppData/Local/Android/Sdk                                                         