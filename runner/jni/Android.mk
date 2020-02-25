LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := runner
LOCAL_CFLAGS += -Wall -Wextra -Werror
LOCAL_SRC_FILES := runner.c
LOCAL_LDLIBS := -llog
include $(BUILD_EXECUTABLE)