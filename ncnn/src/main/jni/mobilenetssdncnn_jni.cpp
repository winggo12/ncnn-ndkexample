// Tencent is pleased to support the open source community by making ncnn available.
//
// Copyright (C) 2020 THL A29 Limited, a Tencent company. All rights reserved.
//
// Licensed under the BSD 3-Clause License (the "License"); you may not use this file except
// in compliance with the License. You may obtain a copy of the License at
//
// https://opensource.org/licenses/BSD-3-Clause
//
// Unless required by applicable law or agreed to in writing, software distributed
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.

#include <android/asset_manager_jni.h>
#include <android/bitmap.h>
#include <android/log.h>

#include <jni.h>

#include <string>
#include <vector>

// ncnn
#include "net.h"
#include "benchmark.h"

static ncnn::UnlockedPoolAllocator g_blob_pool_allocator;
static ncnn::PoolAllocator g_workspace_pool_allocator;

static ncnn::Net mobilenetssd;

struct KeyPoint
{
    float x;
    float y;
    float prob;
};

extern "C" {

// FIXME DeleteGlobalRef is missing for objCls
static jclass objCls = NULL;
static jmethodID constructortorId;
static jfieldID xId;
static jfieldID yId;
static jfieldID wId;
static jfieldID hId;
static jfieldID labelId;
static jfieldID probId;

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "MobilenetSSDNcnn", "JNI_OnLoad");

    ncnn::create_gpu_instance();

    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "MobilenetSSDNcnn", "JNI_OnUnload");

    ncnn::destroy_gpu_instance();
}

// public native boolean Init(AssetManager mgr);
JNIEXPORT jboolean JNICALL Java_com_example_ncnn_MobilenetSSDNcnn_Init(JNIEnv* env, jobject thiz, jobject assetManager)
{
    ncnn::Option opt;
    opt.lightmode = true;
    opt.num_threads = 4;
    opt.blob_allocator = &g_blob_pool_allocator;
    opt.workspace_allocator = &g_workspace_pool_allocator;
    opt.use_packing_layout = true;

    // use vulkan compute
    if (ncnn::get_gpu_count() != 0)
        opt.use_vulkan_compute = true;

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);

    mobilenetssd.opt = opt;

    // init param
    {
        int ret = mobilenetssd.load_param(mgr, "mobilepose.param");
        if (ret != 0)
        {
            __android_log_print(ANDROID_LOG_DEBUG, "MobilenetSSDNcnn", "load_param failed");
            return JNI_FALSE;
        }
    }

    // init bin
    {
        int ret = mobilenetssd.load_model(mgr, "mobilepose.bin");
        if (ret != 0)
        {
            __android_log_print(ANDROID_LOG_DEBUG, "MobilenetSSDNcnn", "load_model failed");
            return JNI_FALSE;
        }
    }

    // init jni glue
    jclass localObjCls = env->FindClass("com/example/ncnn/MobilenetSSDNcnn$KeyPoint");
    objCls = reinterpret_cast<jclass>(env->NewGlobalRef(localObjCls));

    constructortorId = env->GetMethodID(objCls, "<init>", "(Lcom/example/ncnn/MobilenetSSDNcnn;)V");

    xId = env->GetFieldID(objCls, "x", "F");
    yId = env->GetFieldID(objCls, "y", "F");
    probId = env->GetFieldID(objCls, "prob", "F");

    return JNI_TRUE;
}

// public native Obj[] Detect(Bitmap bitmap, boolean use_gpu);
JNIEXPORT jobjectArray JNICALL Java_com_example_ncnn_MobilenetSSDNcnn_Detect(JNIEnv* env, jobject thiz, jobject bitmap, jboolean use_gpu)
{
    if (use_gpu == JNI_TRUE && ncnn::get_gpu_count() == 0)
    {
        return NULL;
        //return env->NewStringUTF("no vulkan capable gpu");
    }

    double start_time = ncnn::get_current_time();

    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env, bitmap, &info);
    int width = info.width;
    int height = info.height;
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888)
        return NULL;

    // ncnn from bitmap
    ncnn::Mat in = ncnn::Mat::from_android_bitmap_resize(env, bitmap, ncnn::Mat::PIXEL_BGR, 256, 320);

    // mobilenetssd
    std::vector<KeyPoint> kpts;
    {
        const float mean_vals[3] = {0.485f , 0.456f, 0.406f};
        const float norm_vals[3] = {1 / 255.f, 1 / 255.f, 1 / 255.f};
        in.substract_mean_normalize(mean_vals, norm_vals);

        ncnn::Extractor posenet = mobilenetssd.create_extractor();

        posenet.set_vulkan_compute(use_gpu);

        posenet.input("input.1", in);

        ncnn::Mat out;
        posenet.extract("497", out);

        kpts.clear();
        for (int p = 0; p < out.c; p++)
        {
            const ncnn::Mat m = out.channel(p);

            float max_prob = 0.f;
            int max_x = 0;
            int max_y = 0;
            for (int y = 0; y < out.h; y++)
            {
                const float* ptr = m.row(y);
                for (int x = 0; x < out.w; x++)
                {
                    float prob = ptr[x];
                    if (prob > max_prob)
                    {
                        max_prob = prob;
                        max_x = x;
                        max_y = y;
                    }
                }
            }

            KeyPoint keypoint;
            keypoint.x = max_x * width / (float)out.w;
            keypoint.y = max_y * height / (float)out.h;
            keypoint.prob = max_prob;

            kpts.push_back(keypoint);
        }


//        for (int i=0; i<out.h; i++)
//        {
//            const float* values = out.row(i);
//
//            KeyPoint kpt;
//
//            kpt.x = 0;
//            kpt.y = 0;
//            kpt.prob = 0;
//
//
//            kpts.push_back(kpt);
//        }
    }

    // objects to Obj[]
//    static const char* class_names[] = {"background",
//        "aeroplane", "bicycle", "bird", "boat",
//        "bottle", "bus", "car", "cat", "chair",
//        "cow", "diningtable", "dog", "horse",
//        "motorbike", "person", "pottedplant",
//        "sheep", "sofa", "train", "tvmonitor"};

    jobjectArray jObjArray = env->NewObjectArray(kpts.size(), objCls, NULL);

    for (size_t i=0; i<kpts.size(); i++)
    {
        jobject jObj = env->NewObject(objCls, constructortorId, thiz);

        env->SetFloatField(jObj, xId, kpts[i].x);
        env->SetFloatField(jObj, yId, kpts[i].y);
        env->SetFloatField(jObj, probId, kpts[i].prob);

//        env->SetFloatField(jObj, xId, objects[i].x);
//        env->SetFloatField(jObj, yId, objects[i].y);
//        env->SetFloatField(jObj, wId, objects[i].w);
//        env->SetFloatField(jObj, hId, objects[i].h);
//        env->SetObjectField(jObj, labelId, env->NewStringUTF(class_names[objects[i].label]));
//        env->SetFloatField(jObj, probId, objects[i].prob);

        env->SetObjectArrayElement(jObjArray, i, jObj);
    }

    double elasped = ncnn::get_current_time() - start_time;
    __android_log_print(ANDROID_LOG_DEBUG, "MobilenetSSDNcnn", "%.2fms   detect", elasped);

    return jObjArray;
}

}
