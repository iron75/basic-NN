package basicneuralnetwork;

import basicneuralnetwork.activationfunctions.*;
import basicneuralnetwork.utilities.FileReaderAndWriter;
import basicneuralnetwork.utilities.MatrixConverter;
import basicneuralnetwork.visualize.Neuron;
import basicneuralnetwork.visualize.VizNetwork;
import jdk.nashorn.internal.runtime.ECMAException;
import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * Created by KimFeichtinger on 04.03.18.
 */
public class NeuralNetwork {
    transient PApplet p;

    public transient VizNetwork vizNetwork;

    private ActivationFunctionFactory activationFunctionFactory = new ActivationFunctionFactory();

    private Random random = new Random();

    // Dimensions of the neural network
    public int inputNodes;
    public int hiddenLayers;
    public int hiddenNodes;
    public int outputNodes;

    public SimpleMatrix[] weights;
    public SimpleMatrix[] biases;

    private double learningRate;

    private String activationFunctionKey;
    private double rate;

    //for flappy bird, copy yourself
    public NeuralNetwork(NeuralNetwork nn) {
        this(nn.inputNodes, nn.hiddenLayers, nn.hiddenNodes, nn.outputNodes, nn.p);

        for (int i = 0; i < weights.length; i++) {
            weights[i] = new SimpleMatrix(nn.weights[i]);
        }
        for (int i = 0; i < biases.length; i++) {
            biases[i] = new SimpleMatrix(nn.biases[i]);
        }
    }

    // Constructor
    // Generate a new neural network with 1 hidden layer with the given amount of nodes in the individual layers
    public NeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes, PApplet p) {
        this(inputNodes, 1, hiddenNodes, outputNodes, p);
    }

    // Constructor
    // Generate a new neural network with a given amount of hidden layers with the given amount of nodes in the individual layers
    // Every hidden layer will have the same amount of nodes
    public NeuralNetwork(int inputNodes, int hiddenLayers, int hiddenNodes, int outputNodes, PApplet p) {
        this.inputNodes = inputNodes;
        this.hiddenLayers = hiddenLayers;
        this.hiddenNodes = hiddenNodes;
        this.outputNodes = outputNodes;

        initializeDefaultValues();  //todo copy
        initializeWeights();
        initializeBiases();

        vizNetwork = new VizNetwork(inputNodes, hiddenLayers, hiddenNodes, outputNodes, p);
    }

    private void initializeDefaultValues() {
        this.setLearningRate(0.1);

        // Sigmoid is the default ActivationFunction
        this.setActivationFunction(ActivationFunction.SIGMOID);
    }

    private void initializeWeights() {
        weights = new SimpleMatrix[hiddenLayers + 1];

        // Initialize the weights between the layers and fill them with random values
        for (int i = 0; i < weights.length; i++) {
            if (i == 0) { // 1st weights that connects inputs to first hidden layer
                weights[i] = SimpleMatrix.random64(hiddenNodes, inputNodes, -1, 1, random);
            } else if (i == weights.length - 1) { // last weights that connect last hidden layer to output
                weights[i] = SimpleMatrix.random64(outputNodes, hiddenNodes, -1, 1, random);
            } else { // everything else
                weights[i] = SimpleMatrix.random64(hiddenNodes, hiddenNodes, -1, 1, random);
            }
        }
    }

    private void initializeBiases() {
        biases = new SimpleMatrix[hiddenLayers + 1];

        // Initialize the biases and fill them with random values
        for (int i = 0; i < biases.length; i++) {
            if (i == biases.length - 1) { // bias for last layer (output layer)
                biases[i] = SimpleMatrix.random64(outputNodes, 1, -1, 1, random);
            } else {
                biases[i] = SimpleMatrix.random64(hiddenNodes, 1, -1, 1, random);
            }
        }
    }

    // Guess method, input is a one column matrix with the input values
    public double[] guess(double[] input) {
        if (input.length != inputNodes) {
            throw new WrongDimensionException(input.length, inputNodes, "Input");
        } else {
            // Get ActivationFunction-object from the map by key
            ActivationFunction activationFunction = activationFunctionFactory.getActivationFunctionByKey(activationFunctionKey);

            // Transform array to matrix
            SimpleMatrix output = MatrixConverter.arrayToMatrix(input);

            for (int i = 0; i < hiddenLayers + 1; i++) {
                output = calculateLayer(weights[i], biases[i], output, activationFunction);
            }

            double[] o = MatrixConverter.getColumnFromMatrixAsArray(output, 0);
//            System.out.println(Arrays.toString(o));

            return o;
        }
    }

    public void train(double[] inputArray, double[] targetArray) {
        if (inputArray.length != inputNodes) {
            throw new WrongDimensionException(inputArray.length, inputNodes, "Input");
        } else if (targetArray.length != outputNodes) {
            throw new WrongDimensionException(targetArray.length, outputNodes, "Output");
        } else {
            // Get ActivationFunction-object from the map by key
            ActivationFunction activationFunction = activationFunctionFactory.getActivationFunctionByKey(activationFunctionKey);

            // Transform 2D array to matrix
            SimpleMatrix input = MatrixConverter.arrayToMatrix(inputArray);
            SimpleMatrix target = MatrixConverter.arrayToMatrix(targetArray);

            // Calculate the values of every single layer
            SimpleMatrix layers[] = new SimpleMatrix[hiddenLayers + 2];
            layers[0] = input;
            for (int j = 1; j < hiddenLayers + 2; j++) {
                layers[j] = calculateLayer(weights[j - 1], biases[j - 1], input, activationFunction);
                input = layers[j];
            }

            for (int n = hiddenLayers + 1; n > 0; n--) {
                // Calculate error
                SimpleMatrix errors = target.minus(layers[n]);

                // Calculate gradient
                SimpleMatrix gradients = calculateGradient(layers[n], errors, activationFunction);

                // Calculate delta
                SimpleMatrix deltas = calculateDeltas(gradients, layers[n - 1]);

                // Apply gradient to bias
                biases[n - 1] = biases[n - 1].plus(gradients);

                // Apply delta to weights
                weights[n - 1] = weights[n - 1].plus(deltas);

                // Calculate and set target for previous (next) layer
                SimpleMatrix previousError = weights[n - 1].transpose().mult(errors);
                target = previousError.plus(layers[n - 1]);
            }

            //
            vizNetwork.update(layers, weights, biases);
        }
    }

    // Generic function to calculate one layer
    private SimpleMatrix calculateLayer(SimpleMatrix weights, SimpleMatrix bias, SimpleMatrix input, ActivationFunction activationFunction) {
        // Calculate outputs of layer
        SimpleMatrix result = weights.mult(input);
        // Add bias to outputs
        result = result.plus(bias);
        // Apply activation function and return result
        return applyActivationFunction(result, false, activationFunction);
    }

    private SimpleMatrix calculateGradient(SimpleMatrix layer, SimpleMatrix error, ActivationFunction activationFunction) {
        SimpleMatrix gradient = applyActivationFunction(layer, true, activationFunction);
        gradient = gradient.elementMult(error);
        return gradient.scale(learningRate);
    }

    private SimpleMatrix calculateDeltas(SimpleMatrix gradient, SimpleMatrix layer) {
        return gradient.mult(layer.transpose());
    }

    // Applies an activation function to a matrix
    // An object of an implementation of the ActivationFunction-interface has to be passed
    // The function in this class will be  to the matrix
    private SimpleMatrix applyActivationFunction(SimpleMatrix input, boolean derivative, ActivationFunction activationFunction) {
        // Applies either derivative of activation function or regular activation function to a matrix and returns the result
        return derivative ? activationFunction.applyDerivativeOfActivationFunctionToMatrix(input)
                : activationFunction.applyActivationFunctionToMatrix(input);
    }

    //muss exra init werden, sonst NullPointerEx
    public void initVizNetwork(PApplet p) {
        vizNetwork = new VizNetwork(inputNodes, hiddenLayers, hiddenNodes, outputNodes, p);
    }

    //for flappy, copy yourself
    public NeuralNetwork copy() {
        return new NeuralNetwork(this);
    }

    public void mutate(float rate)  {
        try{
            mapMatrix(weights,rate, (d,e) -> function_mutate((float) d,(float)e));
            mapMatrix(biases,rate, (d,e) -> function_mutate((float) d,(float)e));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    void mapMatrix(SimpleMatrix[] matrix,float rate, DoubleBinaryOperator func) throws Exception {
        for (int i = 0; i < matrix.length; i++) {
            SimpleMatrix m = matrix[i];

            for (int j = 0; j < m.numCols(); j++) {
                for (int k = 0; k < m.numRows(); k++) {
                    m.set(k, j, func.applyAsDouble(m.get(k, j),rate));
                }
            }
        }
    }

    double function_mutate(float val, float rate) {
        if (Math.random() < rate) {     //mutionrate
            return (float) 10;   //mutate to new random number
//            return (float) Math.random() * 2 - 1;   //mutate to new random number
        } else
            return val;                 //else dont mutate
    }





    public void display() {
        if (vizNetwork != null)
            vizNetwork.display();
    }

    public void writeToFile() {
        FileReaderAndWriter.writeToFile(this);
    }

    public static NeuralNetwork readFromFile() {
        return FileReaderAndWriter.readFromFile();
    }

    public String getActivationFunctionName() {
        return activationFunctionKey;
    }

    public void setActivationFunction(String activationFunction) {
        this.activationFunctionKey = activationFunction;
    }

    public void addActivationFunction(String key, ActivationFunction activationFunction) {
        activationFunctionFactory.addActivationFunction(key, activationFunction);
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public int getInputNodes() {
        return inputNodes;
    }

    public int getHiddenLayers() {
        return hiddenLayers;
    }

    public int getHiddenNodes() {
        return hiddenNodes;
    }

    public int getOutputNodes() {
        return outputNodes;
    }

    public SimpleMatrix[] getWeights() {
        return weights;
    }

    public void setWeights(SimpleMatrix[] weights) {
        this.weights = weights;
    }

    public SimpleMatrix[] getBiases() {
        return biases;
    }

    public void setBiases(SimpleMatrix[] biases) {
        this.biases = biases;
    }

}
