LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := src/CneFeatureConfig.cpp
LOCAL_MODULE := libcnefeatureconfig
LOCAL_MODULE_TAGS := optional
LOCAL_SHARED_LIBRARIES := libc libcutils liblog
LOCAL_C_INCLUDES := $(LOCAL_PATH)/inc
LOCAL_ARM_MODE := arm
LOCAL_PRELINK_MODULE := false

include $(BUILD_SHARED_LIBRARY)

