package org.univaq.swa.auleweb.aulewebrest.model;

import java.util.ArrayList;
import java.util.List;

public class Gruppo {

    private String nome;
    private String descrizione;

    private List<Aula> aule;

    public Gruppo() {
        nome = "";
        descrizione = "";
        aule = new ArrayList();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public List<Aula> getAule() {
        return aule;
    }

    public void setAule(List<Aula> aule) {
        this.aule = aule;
    }

}
