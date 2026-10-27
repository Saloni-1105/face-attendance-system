package com.attendance;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import java.io.File;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/** Opens the webcam and saves 30 face photos of a student into faces/<studentId>/ */
public class FaceRegistration {
    private static final int PHOTOS_TO_TAKE = 30;

    public void capture(int studentId) throws Exception {
        File folder = new File("faces/" + studentId);
        folder.mkdirs();

        CascadeClassifier detector = new CascadeClassifier("haarcascade_frontalface_default.xml");
        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);   // 0 = default webcam
        camera.start();
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        CanvasFrame window = new CanvasFrame("Registering face - look at the camera");
        window.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);

        int taken = 0;
        while (window.isVisible() && taken < PHOTOS_TO_TAKE) {
            Frame frame = camera.grab();
            if (frame == null) break;
            Mat color = converter.convert(frame);

            // Face detection works on a gray image
            Mat gray = new Mat();
            cvtColor(color, gray, COLOR_BGR2GRAY);
            equalizeHist(gray, gray);

            RectVector faces = new RectVector();
            detector.detectMultiScale(gray, faces);

            // Save only when exactly one face is visible
            if (faces.size() == 1) {
                Mat face = new Mat(gray, faces.get(0));
                resize(face, face, new Size(200, 200));   // same size for every photo
                imwrite(folder.getPath() + "/" + taken + ".jpg", face);
                taken++;
                Thread.sleep(150);   // small gap so photos are slightly different
            }

            // Draw a green box on screen
            for (long i = 0; i < faces.size(); i++) {
                rectangle(color, faces.get(i), new Scalar(0, 255, 0, 0));
            }
            putText(color, "Photos: " + taken + "/" + PHOTOS_TO_TAKE, new Point(10, 30),
                    FONT_HERSHEY_SIMPLEX, 0.8, new Scalar(0, 0, 255, 0));
            window.showImage(converter.convert(color));
        }

        camera.stop();
        window.dispose();
        System.out.println("Saved " + taken + " photos for student id " + studentId);
    }
}