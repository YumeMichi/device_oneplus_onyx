LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := sensors.cpp
LOCAL_SHARED_LIBRARIES := libgui libsensor
LOCAL_MODULE := libshims_sensors
LOCAL_MODULE_TAGS := optional
LOCAL_VENDOR_MODULE := true

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := atomic.cpp
LOCAL_MODULE := libshims_atomic
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_VENDOR_MODULE := true

include $(BUILD_SHARED_LIBRARY)
