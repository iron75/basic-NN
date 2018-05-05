//Daniel Shiffman
//The Nature of Code, Fall 2006
//Neural Network

// Hidden Neuron Class
// So far not necessary to differentiate these

package basicneuralnetwork.visualize;

import processing.core.PApplet;

public class HiddenNeuron extends Neuron {
    
    public HiddenNeuron(int x, int y,String name, PApplet p) {
        super(x,  y, name,  p);
    }
    
    public HiddenNeuron(int x,int  y,int i,String name, PApplet p) {
        super( x,  y, i ,  name,p);
    }
    
}
