package basicneuralnetwork.visualize;

import basicneuralnetwork.NeuralNetwork;
import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;

import java.util.ArrayList;

public class VizNetwork {
    PApplet p;
//    NeuralNetwork nn;

    // Layers
    InputNeuron[] input;
    HiddenNeuron[][] hidden;
    OutputNeuron[] output;

    int hiddenLayers;

    protected ArrayList<Connection> connections = new ArrayList<>();

    public static float max_weight = -1;

    public VizNetwork(int inputNodes, int hiddenLayers, int hiddenNodes, int outputNodes, PApplet p) {
        this.p = p;
//        nn = neuralNetwork;
        hidden = new HiddenNeuron[hiddenLayers][];

        //init
        input = new InputNeuron[inputNodes + 1];  // Got to add a bias input
        for (int i = 0; i < hidden.length; i++) {
            hidden[i] = new HiddenNeuron[hiddenNodes + 1];
        }
        this.hiddenLayers = hiddenLayers;
        output = new OutputNeuron[outputNodes + 0];


        //setup
        vizSetupNetwork();
    }

    public void vizSetupNetwork() {
        // Make input neurons
        for (int i = 0; i < input.length - 1; i++) {
            input[i] = new InputNeuron(-300, 0 + i * 50,"i"+i,  p);
        }

        // Make hidden neurons
        for (int j = 0; j < hiddenLayers; j++) {
            for (int i = 0; i < hidden[j].length - 1; i++) {
                hidden[j][i] = new HiddenNeuron(-100 + j * 100, 00 + i * 50,"h"+j+""+i, p);
            }
        }

        // Make bias neurons
        input[input.length - 1] = new InputNeuron(-300, 0 + (input.length - 1) * 50, 1,"ib", p);

        for (int j = 0; j < hiddenLayers; j++) {
            hidden[j][hidden[0].length - 1] = new HiddenNeuron(-100 + j * 100, 0 + (hidden[0].length - 1) * 50, 1,"hb"+j, p);
        }

        // Make output neuron
        for (int i = 0; i < output.length - 0; i++) {
            output[i] = new OutputNeuron(400, 0 + i * 50, "ob",p);
        }


        // Connect input layer to hidden layer
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < hidden[0].length - 1; j++) {
                // Create the connection object and put it in both neurons
                Connection c = new Connection(input[i], hidden[0][j], p);
                hidden[0][j].addConnection(c);
                connections.add(c);
            }
        }


        // Connect the hidden layer to hiddenlayers
        for (int i = 0; i < hiddenLayers - 1; i++) {
            for (int j = 0; j < hidden[0].length - 0; j++) {
                for (int k = 0; k < hidden[0].length - 1; k++) {
                    Connection c = new Connection(hidden[i][j], hidden[i + 1][k], p);
                    hidden[i + 1][k].addConnection(c);
                    connections.add(c);
                }
            }
        }

        // Connect the hidden layer to the output neuron
        for (int i = 0; i < hidden[0].length; i++) {
            for (int j = 0; j < output.length - 0; j++) {
                Connection c = new Connection(hidden[hiddenLayers - 1][i], output[j], p);
                output[j].addConnection(c);
                connections.add(c);
            }
        }
    }


    public void update(SimpleMatrix[] layers, SimpleMatrix[] weights, SimpleMatrix[] biases) {
//        SimpleMatrix m = nn.weights[0];
//        System.out.println(layers[hiddenLayers]);


        for (int i = 0; i < input.length - 1; i++) {
            input[i].output = (float) layers[0].get(i, 0);
        }

        for (int j = 0; j < hiddenLayers;j++){
            for (int i = 0; i < hidden[0].length - 1; i++) {
                hidden[j][i].update(weights[j], biases[j]);
                hidden[j][i].output = (float) layers[j+1].get(i, 0);
            }
        }

        System.out.println("biases"+biases.length);
        System.out.println("hiddenLayers"+hiddenLayers);

        for (int i = 0; i < output.length; i++) {
            output[i].update(weights[hiddenLayers-0], biases[hiddenLayers-0]);
            output[i].output = (float) layers[hiddenLayers+1].get(0);
        }

    }


    public void display() {
        p.background(0);

        p.pushMatrix();
        p.translate(0, 0);
        {
            // Draw all its connections
            for (Connection c : connections) {
                c.display();
            }

            //draw neuron
            for (Neuron n : input) {
                n.display();
            }
            for (int i = 0; i < hiddenLayers; i++) {
                for (Neuron n : hidden[i]) {
                    n.display();
                }
            }
            for (Neuron n : output) {
                n.display();
            }

        }
        p.popMatrix();
    }
}
