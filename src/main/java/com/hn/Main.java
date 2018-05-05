package com.hn;

import basicneuralnetwork.NeuralNetwork;
import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;


public class Main extends PApplet {
    public static final int DEBUG = 1;

    NeuralNetwork nn;

    private boolean right_mouse_pressed;

    int count, countTmp;
    int success, successTmp;


    public static void main(String[] args) {
        PApplet.main("com.hn.Main", args);
    }

    @Override
    public void settings() {
        size(1000, 1000);
    }

    @Override
    public void setup() {
        smooth();

        nn = new NeuralNetwork(2, 2, 4, 1, this);

        /*// Training
        for (int i = 0; i < 5000; i++) {
            float[] test = new float[2];
            test[0] = (int) random(2);
            test[1] = (int) random(2);
            nn.train(test, xor(test));
        }*/


    }

    @Override
    public void draw() {
        if (right_mouse_pressed) {
            train();
//            System.out.println(nn.biases[1]);
        }


        //draw
        background(0);

        pushMatrix();
        translate(width / 2, height / 4);
        nn.display();
        popMatrix();

        //stats
        if (count > 0) {
            fill(255);
            text("count " + count + "   success " + success + "   successrate " + nfc(success / (float) count * 100, 2) + "%", 10, 10);

            text("countTmp " + (count - countTmp) + "   successTmp " + (success - successTmp) + "   successrate " + nfc((success - successTmp) / (float) (count - countTmp) * 100, 2) + "%", 10, 20);


        }
    }


    private void train() {
        count++;

        double[] test = new double[2];
        test[0] = (int) random(2);
        test[1] = (int) random(2);

//            float guess = nn.feedForward(test);
        float guess = (float) (nn.guess(test)[0]);
        nn.train(test, new double[]{xor(test)});

        float error = xor(test) - guess;
        if (guess < 0.5f) {
            guess = 0;
        } else {
            guess = 1;
        }

        System.out.print("[" + (int) test[0] + "," + (int) test[1] + "] --> " + guess
                + "   error " + error);


        if (guess != xor(test)) {
            System.out.println("   ERROR");
        } else {
            System.out.println();
            success++;
        }
    }

    //xor function
    int xor(double[] val) {
        return (val[0] + val[1]) == 1 ? 1 : 0;
    }

    @Override
    public void mousePressed() {
        if (mouseButton == LEFT) {
            train();
        }

        if (mouseButton == RIGHT) {
            right_mouse_pressed = true;
        }
    }

    @Override
    public void mouseReleased() {
        if (mouseButton == RIGHT) {
            right_mouse_pressed = false;
        }
    }

    @Override
    public void keyPressed() {
        if (keyCode == ' ') {
            System.out.println("space");
            countTmp = count;
            successTmp = success;
        }

        // Training
        if (keyCode == 'T') {
            System.out.println("training");
            for (int i = 0; i < 1000; i++) {
                double[] test = new double[2];
                test[0] = (int) random(2);
                test[1] = (int) random(2);
                nn.train(test, new double[]{xor(test)});

                count++;
            }
        }

        // Writes a JSON-file with the current "state" (weights and biases) of the NN
        if (keyCode == 'S') {
            System.out.println("save NN");
            nn.writeToFile();
        }

        // Reads from a (previously generated) JSON-file the nn-Data and returns a NeuralNetwork-object
        if (keyCode == 'L') {
            System.out.println("load NN");
            nn = NeuralNetwork.readFromFile();
            nn.initVizNetwork(this);
        }
    }
}

