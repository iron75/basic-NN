//Daniel Shiffman
//The Nature of Code, Fall 2006
//Neural Network

// Input Neuron Class
// Has additional functionality to receive beginning input

package basicneuralnetwork.visualize;

import processing.core.PApplet;

public class InputNeuron extends Neuron {
    public InputNeuron(int x, int y, PApplet p) {
        super(x,  y,  p);
    }
    
    public InputNeuron(int x,int  y,int i, PApplet p) {
        super( x,  y, i , p);
    }

    public void input(float d) {
        output = d;
    }

}
