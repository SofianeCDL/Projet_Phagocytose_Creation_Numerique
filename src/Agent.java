import processing.core.PApplet;
import processing.core.PVector;

public class Agent extends PApplet {


    //Dimatre d'origine.
    private float diametreOrigine;
    //Diametre final
    private float diametreFinal;

    //Rayon de l'agent.
    private float rayon;

    //Position de l'agent.
    private PVector position;

    //Couleur et opacité de l'agent.
    private int alpha;

    //Couleur de type "color".
    private int couleur;

    //Mouvement bloquer oun non. "true" bloquer, "false" en mouvement.
    private boolean bloquerMouvement;
    /**
     * Constructeur des agent zero.
     */
    public Agent(float r, float g, float b) {

        //diamètre = 2% de la hauteur de l'ecran.
        this.diametreOrigine = ((Main.processing.height * 3) / 100);
        this.diametreFinal = this.diametreOrigine;

        //Diametre div 2.
        this.rayon = this.diametreOrigine / 2;

        //Position aléatoire
        this.position = new PVector(random(this.rayon, Main.processing.width - this.rayon), random(this.rayon, Main.processing.height - this.rayon));

        this.alpha = 100;

        this.couleur = color(r, g, b);

        this.bloquerMouvement = false;
    }

    public Agent(float r, float g, float b, float positionX, float positionY, float taille) {

        //diamètre = 2% de la hauteur de l'ecran.
        this.diametreOrigine = taille;
        this.diametreFinal = this.diametreOrigine;

        //Diametre div 2.
        this.rayon = this.diametreOrigine / 2;

        //Position aléatoire
        this.position = new PVector(positionX, positionY);

        this.alpha = 100;

        //Prends les couleurs choisis au moment de la création.
        this.couleur = color(r, g, b);

        this.bloquerMouvement = false;
    }

    /**
     * Constructeur des agents phagocytés.
     */
    public Agent(float diametreOrigine, float diametreFinal, PVector position, int couleur) {

        //Prends le diametre d'origine de la plus grande cellule qui phagocyte.
        this.diametreOrigine = diametreOrigine;

        //Diametre final de la phagocytose.
        this.diametreFinal = diametreFinal;

        this.rayon = this.diametreOrigine / 2;

        //Position situé au millieur des deux agents en phagocytose.
        this.position = position;

        this.couleur = couleur;

        //Sommes des couleur et des alphas des deux agent modulo 256.
        this.alpha = 100;

        this.bloquerMouvement = false;
    }

    /**
     * Dessine l'agent.
     */
    public void drawAgent() {

        Main.processing.stroke(this.couleur);
        Main.processing.fill(this.couleur, this.alpha);
        Main.processing.circle(this.position.x, this.position.y, this.diametreOrigine);
    }

    /**
     * Veirifcation si la distance entre les deux agents est superieur ou inferieur au rayon de this.
     *
     * @param agentIncertain Agent dont les positions sont incertaines.
     * @return "true" si le rayon est superieur à la distance des agents, sinon "false".
     */
    public boolean verificationPosition(Agent agentIncertain) {
        return this.calculeDistance(agentIncertain) < this.rayon + 3; //distance entre deux particules + 3 pixel (pour ne pas avoir des agents collés.
    }

    /**
     *Calcule de distance entre deux agents.
     *
     * @param agentIncertain agent dont la distance est inconnue.
     * @return la distance en float qui sépare "this" et l'agent incertain.
     */
    public float calculeDistance( Agent agentIncertain) {
        return sqrt( (this.position.x - agentIncertain.getPosition().x) * (this.position.x - agentIncertain.getPosition().x) +
                        (this.position.y - agentIncertain.getPosition().y) * (this.position.y - agentIncertain.getPosition().y));
    }

    /**
     * Donne l'attribue possition de agent.
     *
     * @return PVector position.
     */
    public PVector getPosition() {
        return this.position;
    }

    /**
     * Rapproche "this" de son agent le plus proche "agentProche".
     */
    public void updateAgent(Agent agentProche) {

        if (!this.bloquerMouvement) {
            //Calcule la distance en "x" puis "y".
            float distanceAgentsX = this.position.x - agentProche.getPosition().x;
            float distanceAgentsY = this.position.y - agentProche.getPosition().y;

            //2% de la distance en "x" puis en "y".
            float distanceAvanceX = (distanceAgentsX * 2) / 100;
            float distanceAvanceY = (distanceAgentsY * 2) / 100;

            //diminue la distace entre "this" et l'agent le plus proche.
            this.position.sub(distanceAvanceX, distanceAvanceY);
        }
    }

    /**
     * Vérification si un agent proche est à moins de 10% du rayon de "this".
     *
     * @param agentProche agent le plus proche.
     * @return "true" si la distance est < à 10% du rayon de "this" sinon "false".
     */
    public boolean verificationLimite(Agent agentProche) {
        return this.calculeDistance(agentProche) < (this.rayon * 10) / 100;
    }

    /**
     * Augmentation progressif de la taille.
     */
    public void acroissement() {
        if (this.diametreOrigine < this.diametreFinal) {
            this.diametreOrigine += (this.diametreFinal * 0.5) / 100;
        }
    }

    /** ROLE : Interpolation simple
     *
     * @param x float : Variable entre 0 et 1, sert à fluidifier l'interpolation.
     * @param a float : Valeur "a" à inteporler.
     * @param b float : Valeur "b" à inteporler.
     *
     * @return float
     */
    private float interpol(float x, float a, float b) {
        return (b - a) * x + a;
    }

    //--------------------------------------------
    /** ROLE : Interpolation 2 couleurs.
     * Appelle interpolSimple pour red, green et blue.
     *
     * @param x  float : Variable entre 0 et 1, sert à fluidifier l'interpolation.
     * @param c1 float : Couleur "c1" à inteporler pour chaque valeurs rgb.
     * @param c2 float : Couleur "c2" à inteporler pour chaque valeurs rgb.
     *
     * @return float
     */
    private int interpol2Couleurs(float x, int c1, int c2) {
        int r1 = (int) Main.processing.red(c1);
        int r2 = (int) Main.processing.red(c2);
        int r3 = (int) interpol(x, r1, r2);

        int g1 = (int) Main.processing.green(c1);
        int g2 = (int) Main.processing.green(c2);
        int g3 = (int) interpol(x, g1, g2);

        int b1 = (int) Main.processing.blue(c1);
        int b2 = (int) Main.processing.blue(c2);
        int b3 = (int) interpol(x, b1, b2);

        return color(r3, g3, b3);
    }

    /**
     * Getter du rayon.
     * @return la taille du rayon en float.
     */
    public float getRayon() {
        return this.rayon;
    }

    public int getCouleur() {
        return this.couleur;
    }

    /**
     * L'opacité de l'agent.
     * @return l'alpha en entier entre 0 et 255.
     */
    public int getAlpha() {
        return alpha;
    }

    /**
     * Diametre d'origine de l'agent.
     * @return diametre en float.
     */
    public float getDiametreOrigine() {
        return diametreOrigine;
    }

    /**
     * augmente le diamètre d'origine. Sert pour le clique souri.
     *
     * @param diametreOrigine nouveau diamètre.
     */
    public void setDiametreOrigine(float diametreOrigine) {
        this.diametreOrigine = diametreOrigine;
        this.rayon = this.diametreOrigine / 2;
    }

    /**
     * Bloque le mouvement d'attirance vers un autre agent.
     *
     * @param bloquerMouvement
     */
    public void setBloquerMouvement(boolean bloquerMouvement) {
        this.bloquerMouvement = bloquerMouvement;
    }

    /**
     * Mouvement bloqué ou non.
     * @return "true" bloque, "false" non bloqué.
     */
    public boolean isBloquerMouvement() {
        return this.bloquerMouvement;
    }
}
