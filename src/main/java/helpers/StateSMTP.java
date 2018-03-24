package helpers;

public enum StateSMTP {

    Debut("Début"),
    Attente("Attente"),
    Mail_cree("Mail créé"),
    Destinataire_attribue("Destinataire attribué"),
    Ecriture_mail("Ecriture mail");

    private String value;

    StateSMTP(final String nom){
        this.value = nom;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
