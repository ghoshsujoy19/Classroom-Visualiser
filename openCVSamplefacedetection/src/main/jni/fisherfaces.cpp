/*
 * Copyright (c) 2011. Philipp Wagner <bytefish[at]gmx[dot]de>.
 * Released to public domain under terms of the BSD Simplified license.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the organization nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *   See <http://www.opensource.org/licenses/bsd-license>
 */

#include "opencv2/core/core.hpp"
#include "opencv2/face.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "fisherfaces.h"

#include <iostream>
#include <fstream>
#include <sstream>
#include <jni.h>

using namespace cv;
using namespace cv::face;
using namespace std;

static Mat norm_0_255(InputArray _src) {
    Mat src = _src.getMat();
    // Create and return normalized image:
    Mat dst;
    switch(src.channels()) {
        case 1:
            cv::normalize(_src, dst, 0, 255, NORM_MINMAX, CV_8UC1);
            break;
        case 3:
            cv::normalize(_src, dst, 0, 255, NORM_MINMAX, CV_8UC3);
            break;
        default:
            src.copyTo(dst);
            break;
    }
    return dst;
}

static void read_csv(const string& filename, vector<Mat>& images, vector<int>& labels, char separator = ';') {
    std::ifstream file(filename.c_str(), ifstream::in);
    if (!file) {
        string error_message = "No valid input file was given, please check the given filename.";
        CV_Error(CV_StsBadArg, error_message);
    }
    string line, path, classlabel;
    while (getline(file, line)) {
        stringstream liness(line);
        getline(liness, path, separator);
        getline(liness, classlabel);
        if(!path.empty() && !classlabel.empty()) {
            images.push_back(imread(path, 0));
            labels.push_back(atoi(classlabel.c_str()));
        }
    }
}
JNIEnv *jniEnv;
int fisherRecognize(vector<Mat> images, vector<int> labels, Mat testSample) {

    // These vectors hold the images and corresponding labels.

    // Read in the data. This can fail if no valid
    // input filename is given.

    // Get the height from the first image. We'll need this
    // later in code to reshape the images to their original
    // size:
    if(images.size() == 0){
        return -1;
    }
    int height = ((Mat)images[0]).rows;
    // The following lines simply get the last images from
    // your dataset and remove it from the vector. This is
    // done, so that the training data (which we learn the
    // cv::FaceRecognizer on) and the test data we test
    // the model with, do not overlap.
    // The following lines create an Fisherfaces model for
    // face recognition and train it with the images and
    // labels read from the given CSV file.
    // If you just want to keep 10 Fisherfaces, then call
    // the factory method like this:
    //
    //      cv::createFisherFaceRecognizer(10);
    //
    // However it is not useful to discard Fisherfaces! Please
    // always try to use _all_ available Fisherfaces for
    // classification.
    //
    // If you want to create a FaceRecognizer with a
    // confidence threshold (e.g. 123.0) and use _all_
    // Fisherfaces, then call it with:
    //
    //      cv::createFisherFaceRecognizer(0, 123.0);
    //
    Ptr<FaceRecognizer> model = FisherFaceRecognizer::create();
    model->train(images, labels);
    // The following line predicts the label of a given
    // test image:
    double confidence = 0.0;
    int predictedLabel = -1;
    model->predict(testSample, predictedLabel, confidence);
    if(jniEnv){
        jclass clazz = jniEnv->FindClass("com/itu/yaylas/facerecognizer/FaceDetectionActivity");
        jmethodID setConfidence = jniEnv->GetStaticMethodID(clazz, "setFisherConfidence","(D)V");
        jniEnv->CallStaticVoidMethod(clazz, setConfidence, confidence);
    }
    return predictedLabel;
    //
    // To get the confidence of a prediction call the model with:
    //
    //      int predictedLabel = -1;
    //      double confidence = 0.0;
    //      model->predict(testSample, predictedLabel, confidence);
    //

//    // Here is how to get the eigenvalues of this Eigenfaces model:
//    Mat eigenvalues = model->getMat("eigenvalues");
//    // And we can do the same to display the Eigenvectors (read Eigenfaces):
//    Mat W = model->getMat("eigenvectors");
//    // Get the sample mean from the training data
//    Mat mean = model->getMat("mean");
//    // Display or save:
//    /* if(argc == 2) {
//         imshow("mean", norm_0_255(mean.reshape(1, ((Mat)images[0]).rows)));
//     } else {
//         imwrite(format("%s/mean.png", output_folder.c_str()), norm_0_255(mean.reshape(1, ((Mat)images[0]).rows)));
//     }*/
//    // Display or save the first, at most 16 Fisherfaces:
//    for (int i = 0; i < min(16, W.cols); i++) {
//        string msg = format("Eigenvalue #%d = %.5f", i, eigenvalues.at<double>(i));
//        cout << msg << endl;
//        // get eigenvector #i
//        Mat ev = W.col(i).clone();
//        // Reshape to original size & normalize to [0...255] for imshow.
//        Mat grayscale = norm_0_255(ev.reshape(1, height));
//        // Show the image & apply a Bone colormap for better sensing.
//        Mat cgrayscale;
//        applyColorMap(grayscale, cgrayscale, COLORMAP_BONE);
//        // Display or save:
//        /*if(argc == 2) {
//            imshow(format("fisherface_%d", i), cgrayscale);
//        } else {
//            imwrite(format("%s/fisherface_%d.png", output_folder.c_str(), i), norm_0_255(cgrayscale));
//        }*/
//    }
//    // Display or save the image reconstruction at some predefined steps:
//    for(int num_component = 0; num_component < min(16, W.cols); num_component++) {
//        // Slice the Fisherface from the model:
//        Mat ev = W.col(num_component);
//        Mat projection = subspaceProject(ev, mean, ((Mat)images[0]).reshape(1,1));
//        Mat reconstruction = subspaceReconstruct(ev, mean, projection);
//        // Normalize the result:
//        reconstruction = norm_0_255(reconstruction.reshape(1, ((Mat)images[0]).rows));
//
//    }
//
//    return 0;
}