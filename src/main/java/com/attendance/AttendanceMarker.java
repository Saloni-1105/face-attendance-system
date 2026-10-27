package com.attendance;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

/** Opens the webcam, recognizes faces and marks attendance in MySQL. Close the window to stop. */
public class AttendanceMarker {
    // Lower number = better match. If the number is above this, we say "Unknown".
    // If real students show as Unknown, increase it (e.g. 80). If wrong people get matched, decrease it.
    private static final double THRESHOLD = 70.0;

    public void start() throws Exception {
        if (!new File("model.yml").exists()) {
            System.out.println("model.yml not found. Train the model first (menu option 2).");
            return;
        }

        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
        recognizer.read("model.yml");

        CascadeClassifier detector = new CascadeClassifier("haarcascade_frontalface_default.xml");
        Map<Integer, String> names = new StudentDAO().getAllNames();
        AttendanceDAO attendanceDAO = new AttendanceDAO();
        Set<Integer> alreadyMarked = new HashSet<>();   // avoids hitting the database every frame

        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
        camera.start();
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        CanvasFrame window = new CanvasFrame("Attendance - close window to stop");
        window.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);

        while (window.isVisible()) {
            Frame frame = camera.grab();
            if (frame == null) break;
            Mat color = converter.convert(frame);

            Mat gray = new Mat();
            cvtColor(color, gray, COLOR_BGR2GRAY);
            equalizeHist(gray, gray);

            RectVector faces = new RectVector();
            detector.detectMultiScale(gray, faces);

            for (long i = 0; i < faces.size(); i++) {
                Rect r = faces.get(i);
                Mat face = new Mat(gray, r);
                resize(face, face, new Size(200, 200));

                IntPointer label = new IntPointer(1);
                DoublePointer confidence = new DoublePointer(1);
                recognizer.predict(face, label, confidence);

                int studentId = label.get(0);
                String text;
                Scalar boxColor;

                if (confidence.get(0) < THRESHOLD && names.containsKey(studentId)) {
                    text = names.get(studentId);
                    boxColor = new Scalar(0, 255, 0, 0);   // green
                    if (!alreadyMarked.contains(studentId)) {
                        boolean isNew = attendanceDAO.markPresent(studentId);
                        alreadyMarked.add(studentId);
                        System.out.println(isNew ? "Marked present: " + text
                                                 : text + " was already marked today.");
                    }
                } else {
                    text = "Unknown";
                    boxColor = new Scalar(0, 0, 255, 0);   // red
                }

                rectangle(color, r, boxColor);
                putText(color, text, new Point(r.x(), Math.max(r.y() - 10, 20)),
                        FONT_HERSHEY_SIMPLEX, 0.8, boxColor);
            }
            window.showImage(converter.convert(color));
        }

        camera.stop();
        window.dispose();
        System.out.println("Attendance session ended.");
    }
}