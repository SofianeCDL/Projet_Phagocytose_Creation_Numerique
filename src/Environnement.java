import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Environnement extends PApplet {

    //Nombre d'agent présent dans l'environnement.
    private int nombreAgents;

    //Liste des agents présents dans environnement.
    private ArrayList agents;

    //Couleur des agents.
    private float r, g, b;

    //Vérifie si l'utilisateur é cliqué sur la souri.
    private boolean clicked;

    /**
     * Constructeur
     */
    public Environnement(int nombreAgent) {

        //Initialisation.
        this.nombreAgents = nombreAgent;

        this.agents = new ArrayList();

        this.clicked = false;
    }

    /**
     * Initialisation des agents.
     */
    public void initAgents() {
        this.r = random(255);
        this.g = random(255);
        this.b = random(255);
        for(int iterator = 0 ; iterator < this.nombreAgents ; ++iterator) {
            Agent nouveauAgent = new Agent(r, g, b);

            //Vérifie si il y a une collision.
            if (this.gestionCollision(nouveauAgent)) {
                this.agents.add(nouveauAgent); //Ajoute à la liste d'agent si il n'y a pas de problème.
            }
        }
        System.out.println(this.agents.size());
    }

    /**
     * Affichage les agents.
     * Update les agents.
     */
    public void updateAllAgents() {

        for (int iterator = 0; iterator < this.agents.size(); ++iterator) {
            Agent agent = (Agent) this.agents.get(iterator);

            int indexAgentProche = this.agentProche(agent);

            //Vérifie si le nombre d'agent restant est suffidant (minimum 2) et si l'index de l'agent proche est différent de -1.
            if (this.agents.size() > 1 && indexAgentProche != -1) {
                //L'agent se déplace vers son agent le plus proche.
                this.updateAgents(iterator, indexAgentProche);
                //Phagocytose si il a.
                phagocytose(iterator, indexAgentProche);
            }

            agent.acroissement();
            //Draw agent.
            agent.drawAgent();
        }

        //Si il y a eu un clique de souris.
        if (this.clicked) {

            //Ajoute un nouvel agent.
            this.nouvelAgentClicked();
        }
    }
    /**
     * Permet d'empecher d'avoir deux agent qui se superpose.
     *
     * @param agentIncertain agent dont la position n'est pas véréifié.
     * @return position vérifié. "true" si la position est ok, "false" sinon.
     */
    public boolean gestionCollision(Agent agentIncertain) {
        for(int iterator = 0 ; iterator < this.agents.size() ; ++iterator) {
            Agent agent = (Agent) this.agents.get(iterator);

            //Verifie avec tout les autres agents si il y a une collision.
            if (agentIncertain.verificationPosition(agent)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Recherche l'index de l'agent le plus proche de agentOrigine (sauf lui même).
     * PRECONDITION : Il doit y avoir au moins deux agents.
     *
     * @param agentOrigine Agent dont on recherche l'agent le plus proche.
     * @return l'index de l'agent le plus proche. -1 si il y a une erreur.
     */
    public int agentProche(Agent agentOrigine) {

        //Distance la plus grande pour initialiser.
        float distanceMemoire = Main.processing.width;

        //index -1 pour initialiser.
        int index = -1;

        for (int iterator = 0 ; iterator < this.agents.size() ; ++iterator) {
            Agent agent = (Agent) this.agents.get(iterator);

            //Calcule de la distance entre "this" et un agent.
            float distanceAgent = agentOrigine.calculeDistance(agent);

            //Verifie si la distance précedente est superieur à la nouvelle distance.
            if (distanceAgent < distanceMemoire && agentOrigine != agent && !agent.isBloquerMouvement()) {

                //Remplacement de distance et index.
                distanceMemoire = distanceAgent;

                index = iterator;
            }
        }
        return index;
    }

    /**
     * Rapproche l'agent d'orgine de sont agent le plus proche.
     *
     * @param indexAgentOrigine
     * @param indexAgentProche
     */
    public void updateAgents(int indexAgentOrigine, int indexAgentProche) {
        if (indexAgentProche != -1) {
            Agent agentOrigine = (Agent) this.agents.get(indexAgentOrigine);
            Agent agentProche = (Agent) this.agents.get(indexAgentProche);

            agentOrigine.updateAgent(agentProche);
        }
    }

    /**
     * Lorsque la limite de d'approche des agents est atteinte, les deux agents phagocytes en effectant la sommes des deux aires.
     *
     * @param indexAgentA index de l'agent A.
     * @param indexAgentB index de l'agent B.
     */
    public void phagocytose(int indexAgentA, int indexAgentB) {

        //Retrouve les agents dans la liste
        Agent agentA = (Agent) this.agents.get(indexAgentA);
        Agent agentB = (Agent) this.agents.get(indexAgentB);

        //Vérifie si la limite entre les deux agents est atteinte.
        if(agentA.verificationLimite(agentB)) {

            //Trouve le diamètre le plus grand entre les deux agents.
            float diametreOrigine = plusGrandDiemtre(agentA, agentB);
            //Calcule le nouveau diamètre.
            float diametreNouvAgent = this.calculeDiametre(agentA.getRayon(), agentB.getRayon());
            //Centre du nouvel agent.
            PVector centreNouveauAgent = this.calculeCentrePVector(agentA.getPosition(), agentB.getPosition());

            //Calcule des couleurs et de l'alpha.
            int couleur = agentA.getCouleur();
            int alpha = (agentA.getAlpha() + agentB.getAlpha()) % 256;

            //Construction de l'agent phagocité, utilisation du deuxième constructeur.
            Agent agentPhagocyte = new Agent(diametreOrigine,diametreNouvAgent, centreNouveauAgent, couleur);

            //Supprime les anciens agents du plus grand index vers le plus petit.
            if (indexAgentA > indexAgentB) {
                this.agents.remove(indexAgentA);
                this.agents.remove(indexAgentB);
            } else {
                this.agents.remove(indexAgentB);
                this.agents.remove(indexAgentA);
            }

            //Ajout de l'agent dans la liste.
            this.agents.add(0,agentPhagocyte);
        }
    }

    /**
     * Quand l'utilisateur reste appuyé sur le bouton de la souri, le dernier élément qui a été ajouté (CaD celui que le l'utilisateur a ajouté en cliquant sur l'écran) grossie,
     * Quand le bouton est relaché cette nouvelle cellule enclanche ses mouvements.
     */
    public void nouvelAgentClicked() {

        //Donne le dernier agent rentré. Qui est l'agent créer lorsque l'utilisateur clique sur l'interface.
        Agent nouvelAgent = (Agent) this.agents.get(this.agents.size() - 1);

        //Tant qu'il y a une pression de souri :
        if (Main.processing.mousePressed == true) {
            //Dessin l'agent.
            nouvelAgent.drawAgent();

            //Augmentation de son diamètre.
            nouvelAgent.setDiametreOrigine((float) (nouvelAgent.getDiametreOrigine() + 1));
        } else {
            //Arret du clique souris.
            this.clicked = false;

            //L'agent peut bouger.
            nouvelAgent.setBloquerMouvement(false);
        }
    }

    /**
     * Recherche le plus grand diametre en deux agents donnés.
     *
     * @param agentA
     * @param agentB
     * @return le plus grand diamètre.
     */
    public float plusGrandDiemtre(Agent agentA, Agent agentB) {
        if (agentA.getDiametreOrigine() < agentB.getDiametreOrigine()) {
            return agentB.getDiametreOrigine();
        } else {
            return agentA.getDiametreOrigine();
        }
    }

    /**
     * Calcule le nouveau diamètre à partir de la sommes des airs des deux agents.
     *
     * @param rayonAgentA
     * @param rayonAgentB
     * @return le nouveau diamètre calculé.
     */
    public float calculeDiametre(float rayonAgentA, float rayonAgentB) {
        return 2*sqrt((rayonAgentA*rayonAgentA * PI + rayonAgentB*rayonAgentB * PI)) / sqrt(PI);
    }

    /**
     * Trouve les coordonnées situé au centre de deux PVector.
     *
     * @param agentA coordonnée de l'agent A.
     * @param agentB coordonnée de l'agent B.
     * @return PV ector, coordonnée du centre d entre les deux agents.
     */
    public PVector calculeCentrePVector(PVector agentA, PVector agentB) {
        float centreX = (agentA.x + agentA.x) / 2;
        float centreY = (agentA.y + agentB.y) / 2;
        return new PVector(centreX, centreY);
    }

    /**
     * Reinitialise "clicked".
     *
     * @param clicked
     */
    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    /**
     * Ajout d'un nouvel agent lorsuqe on clique avec la soursi, Cet agent à ses mouvement bloqué.
     */
    public void ajouterAgentClicked() {

        //Ajout d'un agent de taille 0 et de position souri. Sa taille augment tant qu'on reste clique sur la souri.
        Agent nouvelAgent = new Agent(this.r, this.g, this.b, Main.processing.mouseX, Main.processing.mouseY, 0);

        //Bloquage des mouvements de l'agent.
        nouvelAgent.setBloquerMouvement(true);

        //Ajout de l'agent.
        this.agents.add(nouvelAgent);
    }
}
