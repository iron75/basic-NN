package com.hn;


import basicneuralnetwork.NeuralNetwork;

public class Main {
    public static void main (String[] args){
        System.out.println("heloo");

        // Neural network with 2 inputs, 2 hidden layers, 4 hidden nodes and 1 output
        NeuralNetwork nn = new NeuralNetwork(2, 2, 4, 1);

        // Writes a JSON-file with the current "state" (weights and biases) of the NN
        nn.writeToFile();
    }
}
