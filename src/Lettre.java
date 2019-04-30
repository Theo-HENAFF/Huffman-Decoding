
public class Lettre {
    int frequence;
    char label;
    String codeBinaire;

    public Lettre(int frequence, Character label){
        this.frequence = frequence;
        this.label = label;
    }

    public int getFrequence() {
        return frequence;
    }

    public char getLabel() {
        return label;
    }

    public String getCodeBinaire() {
        return codeBinaire;
    }

    public void setCodeBinaire(String codeBinaire) {
        this.codeBinaire = codeBinaire;
    }
}
