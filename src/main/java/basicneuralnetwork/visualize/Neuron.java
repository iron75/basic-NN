package basicneuralnetwork.visualize;

import com.hn.Main;
import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Neuron {
    PApplet p;
    PVector pos = new PVector();
    String name;

    int r = 25;

    protected float output;
    protected ArrayList<Connection> connections;
    protected boolean bias = false;


    // A regular Neuron
    public Neuron(int x, int y, String name, PApplet p) {
        this(x, y, 0, name, p);
        bias = false;
    }

    // Constructor for a bias neuron
    public Neuron(int x, int y, int output, String name, PApplet p) {
        this.p = p;
        pos.x = x;
        pos.y = y;
        this.name = name;

        this.output = output;
        connections = new ArrayList();
        bias = true;
    }


    void display() {
        p.stroke(255);
        p.strokeWeight(1);
        // Brightness is mapped to sum
        float b = p.map(output, 0, 1, 0, 255);
        p.fill(b);

        p.ellipse(pos.x, pos.y, r, r);

        // Size shrinks down back to original dimensions
//        r = p.lerp(r, 32, 0.1f);


        if (Main.DEBUG == 1) {
            p.fill(102, 153, 255);
            p.text(output, pos.x, pos.y - 20);
            p.fill(0, 0, 250);
            p.text(name, pos.x -10, pos.y + 5);
        }
    }


    void update(SimpleMatrix w, SimpleMatrix bias) {
//        System.out.println(bias);

        for (int i = 0; i < w.numCols(); i++) {
            connections.get(i).weight = (float) w.get(0, i);
        }
        connections.get(w.numCols()).weight = (float) bias.get(0);
    }

    void addConnection(Connection c) {
        connections.add(c);
    }

    float getOutput() {
        return output;
    }
}
