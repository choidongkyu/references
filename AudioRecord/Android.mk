LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    $(call all-java-files-under, java) \

LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_MODULE_PATH := $(PRODUCT_OUT)/system/priv-app
LOCAL_CERTIFICATE := platform

LOCAL_JAVA_LIBRARIES := android.hidl.base-V1.0-java
LOCAL_STATIC_JAVA_LIBRARIES := \
    vendor.quber.hardware.aecanalysis-V1.0-java

LOCAL_STATIC_ANDROID_LIBRARIES := \
    android-support-v7-appcompat \
    android-support-v4 \
    android-support-design

LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/res 


LOCAL_USE_AAPT2 := true
LOCAL_DEX_PREOPT := false
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_PACKAGE_NAME := AudioRecord

include $(BUILD_PACKAGE)
