package org.univaq.swa.auleweb.aulewebrest.model;

import java.time.LocalDateTime;

public class EventoRicorrente {

    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private Evento evento;

    public EventoRicorrente() {
        dataInizio = null;
        dataFine = null;
        evento = new Evento();
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDateTime dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDateTime getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDateTime dataFine) {
        this.dataFine = dataFine;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

}
