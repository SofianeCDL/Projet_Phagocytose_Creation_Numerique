import processing.core.PApplet;

public class Main extends PApplet {

    public static PApplet processing;

    public static void main(String[] args) {
        PApplet.main("Main", args);
    }

    private int nombreAgent;

    private Environnement environnement;

    public void settings() {
        fullScreen();
    }

    public void setup() {
        processing = this;

        strokeWeight(1);
        background(0);

        this.nombreAgent = (int) (((width * height) * 0.05) / 100);

        this.environnement = new Environnement(this.nombreAgent);
        this.environnement.initAgents();

    }

    public void draw(){
        background(0);

        this.environnement.updateAllAgents();
    }

    public void mousePressed() {
        this.environnement.setClicked(true);
        this.environnement.ajouterAgentClicked();
    }

}
