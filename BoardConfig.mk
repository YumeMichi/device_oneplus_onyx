#
# Copyright (C) 2016 The CyanogenMod Project
# Copyright (C) 2017 The LineageOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

PLATFORM_PATH := device/oneplus/onyx

BOARD_VENDOR := oneplus

# Include path
TARGET_SPECIFIC_HEADER_PATH := $(PLATFORM_PATH)/include

# Bootloader
TARGET_BOOTLOADER_BOARD_NAME := MSM8974
TARGET_NO_BOOTLOADER := true
TARGET_NO_RADIOIMAGE := true

# Platform
TARGET_BOARD_PLATFORM := msm8974

# Architecture
TARGET_ARCH := arm
TARGET_ARCH_VARIANT := armv7-a-neon
TARGET_CPU_ABI := armeabi-v7a
TARGET_CPU_ABI2 := armeabi
TARGET_CPU_VARIANT := generic
TARGET_CPU_VARIANT_RUNTIME := krait

# Binder API version
TARGET_USES_64_BIT_BINDER := true

# Kernel
BOARD_KERNEL_BASE := 0x00000000
BOARD_KERNEL_CMDLINE := androidboot.hardware=qcom androidboot.bootdevice=msm_sdcc.1 ehci-hcd.park=3
BOARD_KERNEL_PAGESIZE := 2048
BOARD_MKBOOTIMG_ARGS := --ramdisk_offset 0x01000000 --tags_offset 0x00000100
BOARD_KERNEL_IMAGE_NAME := zImage-dtb
BOARD_RAMDISK_USE_XZ := true
TARGET_KERNEL_ARCH := arm
TARGET_KERNEL_CONFIG := onyx_defconfig
TARGET_KERNEL_SOURCE := kernel/oneplus/onyx
TARGET_KERNEL_CROSS_COMPILE_PREFIX := arm-eabi-
TARGET_KERNEL_ADDITIONAL_FLAGS := HOSTCFLAGS="-fuse-ld=lld -Wno-unused-command-line-argument"
KERNEL_TOOLCHAIN := $(PWD)/prebuilts/gcc/linux-x86/arm/linaro-arm-eabi-4.9/bin

# ANT+
BOARD_ANT_WIRELESS_DEVICE := "vfs-prerelease"

# Assert
TARGET_BOARD_INFO_FILE ?= $(PLATFORM_PATH)/board-info.txt
TARGET_OTA_ASSERT_DEVICE := onyx,OnePlus,E1003,ONE

# Audio
BOARD_USES_ALSA_AUDIO := true
AUDIO_FEATURE_ENABLED_EXTENDED_COMPRESS_FORMAT := true
AUDIO_FEATURE_ENABLED_EXTN_FORMATS := true
AUDIO_FEATURE_ENABLED_EXTN_POST_PROC := true
AUDIO_FEATURE_ENABLED_FLUENCE := true
AUDIO_FEATURE_ENABLED_HFP := true
AUDIO_FEATURE_ENABLED_HWDEP_CAL := true
AUDIO_FEATURE_ENABLED_MULTI_VOICE_SESSIONS := true
AUDIO_FEATURE_ENABLED_NEW_SAMPLE_RATE := true
AUDIO_FEATURE_ENABLED_PROXY_DEVICE := true
AUDIO_FEATURE_LOW_LATENCY_PRIMARY := true
USE_CUSTOM_AUDIO_POLICY := 1
USE_XML_AUDIO_POLICY_CONF := 1

# Bluetooth
BOARD_BLUETOOTH_BDROID_BUILDCFG_INCLUDE_DIR := $(PLATFORM_PATH)/bluetooth
BOARD_HAVE_BLUETOOTH_QCOM := true

# Camera
MALLOC_SVELTE_FOR_LIBC32 := true
USE_DEVICE_SPECIFIC_CAMERA := true
TARGET_HAS_LEGACY_CAMERA_HAL1 := true
TARGET_PROCESS_SDK_VERSION_OVERRIDE := \
    /system/bin/mediaserver=22 \
    /system/vendor/bin/mm-qcamera-daemon=22

# Charger
BOARD_CHARGER_DISABLE_INIT_BLANK := true
BOARD_CHARGER_ENABLE_SUSPEND := true
BACKLIGHT_PATH := /sys/class/leds/lcd-backlight/brightness

# Display
TARGET_SCREEN_DENSITY := 420

# Filesystem
BOARD_FLASH_BLOCK_SIZE := 131072
BOARD_BOOTIMAGE_PARTITION_SIZE     := 16777216
BOARD_ROOT_EXTRA_FOLDERS := firmware persist
BOARD_CACHEIMAGE_FILE_SYSTEM_TYPE := ext4
BOARD_CACHEIMAGE_PARTITION_SIZE    := 536870912
BOARD_PERSISTIMAGE_FILE_SYSTEM_TYPE := ext4
BOARD_PERSISTIMAGE_PARTITION_SIZE  := 33554432
BOARD_RECOVERYIMAGE_PARTITION_SIZE := 16777216
BOARD_ROOT_EXTRA_SYMLINKS := \
    /data/tombstones:/tombstones
BOARD_SYSTEMIMAGE_PARTITION_SIZE   := 2147483648
BOARD_USERDATAIMAGE_FILE_SYSTEM_TYPE := ext4
BOARD_USERDATAIMAGE_PARTITION_SIZE := 12611874816
TARGET_USERIMAGES_USE_EXT4 := true
TARGET_USERIMAGES_USE_F2FS := true

# Filesystem
TARGET_FS_CONFIG_GEN := $(PLATFORM_PATH)/config.fs

# FM
BOARD_HAVE_QCOM_FM := true
TARGET_QCOM_NO_FM_FIRMWARE := true

# Fonts
EXCLUDE_SERIF_FONTS := true
SMALLER_FONT_FOOTPRINT := true

# Graphics
MAX_EGL_CACHE_KEY_SIZE := 12*1024
MAX_EGL_CACHE_SIZE := 2048*1024
TARGET_ADDITIONAL_GRALLOC_10_USAGE_BITS := 0x02000000U
TARGET_BOOTANIMATION_MULTITHREAD_DECODE := true
TARGET_CONTINUOUS_SPLASH_ENABLED := true
TARGET_DISABLE_POSTRENDER_CLEANUP := true
TARGET_USES_ION := true
USE_OPENGL_RENDERER := true

# HIDL
DEVICE_MANIFEST_FILE := $(PLATFORM_PATH)/manifest.xml
DEVICE_MATRIX_FILE := $(PLATFORM_PATH)/compatibility_matrix.xml
PRODUCT_ENFORCE_VINTF_MANIFEST_OVERRIDE := true

# Init
TARGET_INIT_VENDOR_LIB := libinit_onyx
TARGET_RECOVERY_DEVICE_MODULES := libinit_onyx

# Keymaster
TARGET_PROVIDES_KEYMASTER := true

# Legacy memfd
TARGET_HAS_MEMFD_BACKPORT := true

# Lights
TARGET_PROVIDES_LIBLIGHT := true

# Media
TARGET_USES_MEDIA_EXTENSIONS := true


# Power
TARGET_TAP_TO_WAKE_NODE := "/proc/touchpanel/double_tap_enable"

# QCOM hardware
BOARD_USES_QCOM_HARDWARE := true

# Releasetools
TARGET_RECOVERY_UPDATER_LIBS := librecovery_updater_onyx
TARGET_RELEASETOOLS_EXTENSIONS := $(PLATFORM_PATH)

# Recovery
TARGET_RECOVERY_FSTAB := $(PLATFORM_PATH)/rootdir/etc/fstab.qcom

# SELinux
include device/qcom/sepolicy-legacy/sepolicy.mk

BOARD_SEPOLICY_DIRS += \
    $(PLATFORM_PATH)/sepolicy

# SHIMS
TARGET_LD_SHIM_LIBS := \
    /system/vendor/lib/libril-qc-qmi-1.so|libaudioclient_shim.so \
    /system/lib/libgui.so|libshims_sensors.so \
    /system/vendor/lib/hw/camera.vendor.msm8974.so|libshims_camera.so \
    /system/vendor/lib/hw/camera.vendor.msm8974.so|libshims_camera_parameters.so \
    /system/vendor/lib/libmmcamera2_stats_algorithm.so|libshims_atomic.so

# Vendor security patch level
VENDOR_SECURITY_PATCH := 2016-11-01

# Wifi
BOARD_HAS_QCOM_WLAN              := true
BOARD_WLAN_DEVICE                := qcwcn
BOARD_WPA_SUPPLICANT_DRIVER      := NL80211
BOARD_WPA_SUPPLICANT_PRIVATE_LIB := lib_driver_cmd_qcwcn
BOARD_HOSTAPD_DRIVER             := NL80211
BOARD_HOSTAPD_PRIVATE_LIB        := lib_driver_cmd_qcwcn
PRODUCT_VENDOR_MOVE_ENABLED      := true
TARGET_DISABLE_WCNSS_CONFIG_COPY := true
TARGET_USES_QCOM_WCNSS_QMI       := true
TARGET_USES_WCNSS_MAC_ADDR_REV   := true
WIFI_DRIVER_FW_PATH_STA          := "sta"
WIFI_DRIVER_FW_PATH_AP           := "ap"
WIFI_HIDL_UNIFIED_SUPPLICANT_SERVICE_RC_ENTRY := true
WPA_SUPPLICANT_VERSION           := VER_0_8_X

-include vendor/oneplus/onyx/BoardConfigVendor.mk
