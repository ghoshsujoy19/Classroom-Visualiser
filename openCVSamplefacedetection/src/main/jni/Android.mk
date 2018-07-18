LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#OPENCV_INSTALL_MODULES:=on
#OPENCV_LIB_TYPE:=SHARED

include /home/sujoy/opencvf/opencv3-android-sdk-with-contrib-master/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES  := DetectionBasedTracker_jni.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl

LOCAL_MODULE     := detection_based_tracker

include $(BUILD_SHARED_LIBRARY)