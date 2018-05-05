//Daniel Shiffman
//The Nature of Code, Fall 2006
//Neural Network

// Hidden Neuron Class
// So far not necessary to differentiate these

package basicneuralnetwork.visualize;

import processing.core.PApplet;

public class HiddenNeuron extends Neuron {
    
    public HiddenNeuron(int x, int y, PApplet p) {
        super(x,  y,  p);
    }
    
    public HiddenNeuron(int x,int  y,int i, PApplet p) {
        super( x,  y, i , p);
    }
    
}
