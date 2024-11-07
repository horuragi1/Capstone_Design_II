#include <jni.h>
#include "com_example_demo_FreeRDP.h" // JNI 헤더 파일

JNIEXPORT jstring JNICALL Java_com_example_demo_FreeRDP_login(JNIEnv *env, jobject obj) {
    return (*env)->NewStringUTF(env, "Hello from C!");
}