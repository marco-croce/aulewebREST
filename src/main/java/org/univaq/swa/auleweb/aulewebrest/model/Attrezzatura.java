package org.univaq.swa.auleweb.aulewebrest.model;

public class Attrezzatura {

    private String numeroSeriale;
    private String descrizione;

    public Attrezzatura() {
        numeroSeriale = "";
        descrizione = "";
    }

    public String getNumeroSeriale() {
        return numeroSeriale;
    }

    public void setNumeroSeriale(String numeroSeriale) {
        this.numeroSeriale = numeroSeriale;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

}
