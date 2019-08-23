PLATFORM_PATH := device/oneplus/onyx

# Bootloader
TARGET_BOOTLOADER_BOARD_NAME := msm8974
TARGET_NO_BOOTLOADER         := true

# Platform
TARGET_BOARD_PLATFORM     := msm8974
TARGET_BOARD_PLATFORM_GPU := qcom-adreno420

TARGET_CPU_ABI      := armeabi-v7a
TARGET_CPU_ABI2     := armeabi
TARGET_CPU_SMP      := true
TARGET_ARCH         := arm
TARGET_ARCH_VARIANT := armv7-a-neon
TARGET_CPU_VARIANT  := krait

# Kernel
BOARD_KERNEL_BASE       := 0x00000000
BOARD_KERNEL_CMDLINE    := androidboot.hardware=qcom androidboot.bootdevice=msm_sdcc.1 ehci-hcd.park=3
BOARD_KERNEL_CMDLINE    += androidboot.selinux=permissive
BOARD_KERNEL_PAGESIZE   := 2048
BOARD_MKBOOTIMG_ARGS    := --ramdisk_offset 0x01000000 --tags_offset 0x00000100
BOARD_KERNEL_IMAGE_NAME := zImage-dtb
TARGET_KERNEL_ARCH      := arm
TARGET_KERNEL_CONFIG    := onyx_defconfig
TARGET_KERNEL_SOURCE    := kernel/oneplus/onyx
KERNEL_TOOLCHAIN        := $(shell pwd)/prebuilts/gcc/$(HOST_OS)-x86/arm/arm-linux-androideabi-4.9/bin
TARGET_KERNEL_CROSS_COMPILE_PREFIX := arm-linux-androidkernel-

BOARD_FLASH_BLOCK_SIZE               := 131072
BOARD_BOOTIMAGE_PARTITION_SIZE       := 16777216
BOARD_RECOVERYIMAGE_PARTITION_SIZE   := 16777216
BOARD_SYSTEMIMAGE_PARTITION_SIZE     := 2147483648
BOARD_USERDATAIMAGE_PARTITION_SIZE   := 12611874816
TARGET_USERIMAGES_USE_EXT4           := true
TARGET_USERIMAGES_USE_F2FS           := true

# Vendor Init
TARGET_INIT_VENDOR_LIB         := libinit_onyx
TARGET_RECOVERY_DEVICE_MODULES := libinit_onyx

# TWRP
BOARD_CUSTOM_BOOTIMG_MK      := $(PLATFORM_PATH)/custombootimg.mk
BOARD_HAS_NO_SELECT_BUTTON   := true
BOARD_SUPPRESS_SECURE_ERASE  := true
DEVICE_RESOLUTION            := 1080x1920
RECOVERY_SDCARD_ON_DATA      := true
TARGET_RECOVERY_PIXEL_FORMAT := "RGB_565"
TW_EXTRA_LANGUAGES           := true
TW_INCLUDE_CRYPTO            := true
TW_NO_USB_STORAGE            := true
TW_THEME                     := portrait_hdpi
TW_DEVICE_VERSION            := YumeMichi
TW_USE_TOOLBOX               := true
PLATFORM_SECURITY_PATCH      := 2099-12-31
TW_EXCLUDE_TWRPAPP           := true

# Assertions
TARGET_BOARD_INFO_FILE       := $(PLATFORM_PATH)/board-info.txt
