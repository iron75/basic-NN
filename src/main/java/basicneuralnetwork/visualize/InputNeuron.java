//Daniel Shiffman
//The Nature of Code, Fall 2006
//Neural Network

// Input Neuron Class
// Has additional functionality to receive beginning input

package basicneuralnetwork.visualize;

import processing.core.PApplet;

public class InputNeuron extends Neuron {
    public InputNeuron(int x, int y, String name, PApplet p) {
        super(x,  y, name,  p);
    }
    
    public InputNeuron(int x,int  y,int i,String name, PApplet p) {
        super( x,  y, i ,  name,p);
    }

    public void input(float d) {
        output = d;
    }

}
