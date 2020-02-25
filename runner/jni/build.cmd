@echo off
cd %~dp0
pushd %CD%
cd ..
call ndk-build
cd libs
cd arm64-v8a
rename runner libjsdroid.so
cd ../armeabi-v7a
rename runner libjsdroid.so
cd ../x86
rename runner libjsdroid.so
cd ../x86_64
rename runner libjsdroid.so
popd

