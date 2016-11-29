LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
    ril.cpp \
    ril_event.cpp\
    RilSocket.cpp \
    RilSapSocket.cpp \

LOCAL_SHARED_LIBRARIES := \
    liblog \
    libutils \
    libbinder \
    libcutils \
    libhardware_legacy \
    librilutils \

LOCAL_STATIC_LIBRARIES := \
    libprotobuf-c-nano-enable_malloc \

LOCAL_C_INCLUDES += external/nanopb-c
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../include
LOCAL_EXPORT_C_INCLUDE_DIRS := $(LOCAL_PATH)/../include

LOCAL_MODULE:= libril
LOCAL_CLANG := true
LOCAL_SANITIZE := integer

include $(BUILD_SHARED_LIBRARY)
