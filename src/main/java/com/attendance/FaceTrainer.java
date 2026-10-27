package com.attendance;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

/** Reads all saved photos and teaches the recognizer. Saves the result in model.yml */
public class FaceTrainer {

    public void train() {
        File root = new File("faces");
        File[] studentFolders = root.listFiles(File::isDirectory);
        if (studentFolders == null || studentFolders.length == 0) {
            System.out.println("No photos found. Register a student first.");
            return;
        }

        // Collect every photo and its label (label = student id)
        List<File> files = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        for (File folder : studentFolders) {
            int id = Integer.parseInt(folder.getName());
            File[] photos = folder.listFiles((d, n) -> n.endsWith(".jpg"));
            if (photos == null) continue;
            for (File photo : photos) {
                files.add(photo);
                ids.add(id);
            }
        }

        MatVector images = new MatVector(files.size());
        Mat labels = new Mat(files.size(), 1, CV_32SC1);
        IntBuffer labelBuffer = labels.createBuffer();

        for (int i = 0; i < files.size(); i++) {
            images.put(i, imread(files.get(i).getAbsolutePath(), IMREAD_GRAYSCALE));
            labelBuffer.put(i, ids.get(i));
        }

        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
        recognizer.train(images, labels);
        recognizer.save("model.yml");
        System.out.println("Training done using " + files.size() + " photos. Model saved to model.yml");
    }
}