LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := jsd
LOCAL_SRC_FILES := jsd.c

include $(BUILD_SHARED_LIBRARY)