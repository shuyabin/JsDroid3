@echo off
cd %~dp0
pushd %CD%
cd ..
call ndk-build
adb push ./libs/arm64-v8a/runner /data/local/tmp/main
adb shell  chmod 777 /data/local/tmp/main
adb shell /data/local/tmp/main
popd

