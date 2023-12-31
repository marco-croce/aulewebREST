package org.univaq.swa.auleweb.aulewebrest.result;

import java.util.List;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import org.univaq.swa.auleweb.aulewebrest.model.Evento;

public class ICalendarWriter {

    public Calendar export(List<Evento> eventi) {

        Calendar calendar = new Calendar().withProdId("-//Ben Fortuna//iCal4j 1.0//EN")
                .withDefaults().getFluentTarget();

        for (Evento evento : eventi) {
            String summary = evento.getNome() + " (" + evento.getDescrizione() + ") in aula " + evento.getAula().getNome() + " - "
                    + evento.getAula().getEdificio();
            VEvent event = new VEvent(evento.getDataInizio(), evento.getDataFine(), summary);
            calendar.withComponent(event);
        }

        return calendar;

    }

}
