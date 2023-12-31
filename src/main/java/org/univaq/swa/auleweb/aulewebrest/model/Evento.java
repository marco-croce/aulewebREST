package org.univaq.swa.auleweb.aulewebrest.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Evento {

    private LocalDateTime data_inizio;
    private LocalDateTime data_fine;
    private String nome;
    private String descrizione;
    private String email_responsabile;
    private Aula aula;
    private TipologiaEvento tipologia;
    private String nomeCorso;
    private TipologiaRicorrenza tipologiaRicorrenza;
    private LocalDate dataFineRicorrenza;

    public Evento() {
        data_inizio = null;
        data_fine = null;
        nome = "";
        descrizione = "";
        email_responsabile = "";
        aula = new Aula();
        tipologia = null;
        nomeCorso = "";
        tipologiaRicorrenza = null;
        dataFineRicorrenza = null;
    }

    public LocalDateTime getDataInizio() {
        return data_inizio;
    }

    public void setDataInizio(LocalDateTime data_inizio) {
        this.data_inizio = data_inizio;
    }

    public LocalDateTime getDataFine() {
        return data_fine;
    }

    public void setDataFine(LocalDateTime data_fine) {
        this.data_fine = data_fine;
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

    public String getEmailResponsabile() {
        return email_responsabile;
    }

    public void setEmailResponsabile(String email_responsabile) {
        this.email_responsabile = email_responsabile;
    }

    public Aula getAula() {
        return aula;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
    }

    public TipologiaEvento getTipologia() {
        return tipologia;
    }

    public void setTipologia(TipologiaEvento tipologia) {
        this.tipologia = tipologia;
    }

    public String getNomeCorso() {
        return nomeCorso;
    }

    public void setNomeCorso(String nomeCorso) {
        this.nomeCorso = nomeCorso;
    }

    public TipologiaRicorrenza getTipoRicorrenza() {
        return tipologiaRicorrenza;
    }

    public void setRicorrenza(TipologiaRicorrenza tipologiaRicorrenza) {
        this.tipologiaRicorrenza = tipologiaRicorrenza;
    }

    public LocalDate getDataFineRicorrenza() {
        return dataFineRicorrenza;
    }

    public void setDataFineRicorrenza(LocalDate dataFineRicorrenza) {
        this.dataFineRicorrenza = dataFineRicorrenza;
    }

}
