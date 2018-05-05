// Daniel Shiffman
// The Nature of Code, Fall 2006
// Neural Network

// Class to describe a connection between two neurons

package basicneuralnetwork.visualize;

import com.hn.Main;
import processing.core.PApplet;

public class Connection {
    PApplet p;

    private Neuron from;     // Connection goes from. . .
    private Neuron to;       // To. . .
    float weight;   // Weight of the connection. . .

    // Constructor  builds a connection with a random weight
    public Connection(Neuron a_, Neuron b_, PApplet p) {
        this(a_, b_, (float) Math.random() * 2 - 1, p);
//        this(a_, b_, 0f,p);
    }

    // In case I want to set the weights manually, using this for testing
    public Connection(Neuron a_, Neuron b_, float w, PApplet p) {
        this.p = p;
        from = a_;
        to = b_;
        weight = w;

        if (p.abs(weight) > VizNetwork.max_weight)       //for drawing strokeweight
            VizNetwork.max_weight = weight;
    }


    // A connection is drawn as a line.
    void display() {
        if (weight >= 0) {
            p.stroke(255, 255, 0);
//            System.out.println("weight1 "+weight);
            p.strokeWeight(weight);
        } else {
//            System.out.println("weight0 "+weight);
            p.stroke(255, 0, 0);
            p.strokeWeight(-weight);
        }
        p.line(from.pos.x, from.pos.y, to.pos.x, to.pos.y);

        if (Main.DEBUG == 1) {
            p.fill(102, 153, 255);

//            if (from.name.equals("i0"))
                p.text(p.nfc(weight, 2), (to.pos.x - from.pos.x) / 2 + from.pos.x + 20, (to.pos.y - from.pos.y) / 2 + from.pos.y);
        }
    }

    public Neuron getFrom() {
        return from;
    }

    public Neuron getTo() {
        return to;
    }

    public float getWeight() {
        return weight;
    }

    // Changing the weight of the connection
    public void adjustWeight(float deltaWeight) {
        weight += deltaWeight;

        if (p.abs(weight) > VizNetwork.max_weight)   //for drawing strokeweight
            VizNetwork.max_weight = weight;
    }

}
