package edu.umich.eecs.april.apriltag;

import java.util.Arrays;

public class ApriltagPose {
    // The decoded ID of the tag
    public int id;

    // How many error bits were corrected? Note: accepting large numbers of
    // corrected errors leads to greatly increased false positive rates.
    // NOTE: As of this implementation, the detector cannot detect tags with
    // a hamming distance greater than 2.
    public int hamming;

    // The center of the detection in image pixel coordinates.
    public double[] center = new double[2];

    // The corners of the tag in image pixel coordinates. These always
    // wrap counter-clock wise around the tag.
    // Flattened to [x0 y0 x1 y1 ...] for JNI convenience
    public double[] corners = new double[8];

    // Distance in meters from detected AprilTag.
    // translationMeters[0] = x distance in meters,
    // translationMeters[1] = y distance in meters,
    // translationMeters[2] = z distance in meters.
    public double[] translationMeters_1 = new double[3];

    // 3x3 rotation matrix
    public double[] rotation_1 = new double[9];

    // Distance in meters from detected AprilTag.
    // translationMeters[0] = x distance in meters,
    // translationMeters[1] = y distance in meters,
    // translationMeters[2] = z distance in meters.
    public double[] translationMeters_2 = new double[3];

    // 3x3 rotation matrix
    public double[] rotation_2 = new double[9];

    public double poseConfidence;

    @Override
    public String toString() {
        return "ApriltagPose{" +
                "id=" + id +
                ", hamming=" + hamming +
                ", center=" + Arrays.toString(center) +
                ", corners=" + Arrays.toString(corners) +
                ", translationMeters_1=" + Arrays.toString(translationMeters_1) +
                ", rotation_1=" + Arrays.toString(rotation_1) +
                ", translationMeters_2=" + Arrays.toString(translationMeters_2) +
                ", rotation_2=" + Arrays.toString(rotation_2) +
                ", poseConfidence=" + poseConfidence +
                '}';
    }
}
