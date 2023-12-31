
const eventi_result = $('#eventi-result');
const eventi_empty = $('#eventi-empty');
const eventi_container = $('#eventi-container');

function getEventiAttuali() {
    clear();
    toggleVisibility(eventi_container);

    $.ajax({
        url: "rest/eventi/attuali",
        method: "GET",
        success: function (data) {
            //data --> lista di eventi
            eventi_result.children().remove();
            if (data.length > 0) {
                getEventiURL(data);
            } else {
                handleError("", "#eventi", "Non è presente nessun evento!");
            }
        },
        error: function (request) {
            handleError(request, "#eventi", "Errore nel caricamento degli eventi.");
        },
        cache: false
    });
}

function getEventi(idAula, inizio, fine) {
    clear();
    toggleVisibility(eventi_container);

    if (idAula === "" || inizio === "" || fine === "") {
        handleError("", "#eventi", "Parametri insufficienti!");
        return;
    }

    $.ajax({
        url: "rest/eventi?aula=" + idAula + "&from=" + inizio + "&to=" + fine,
        method: "GET",
        success: function (data) {
            //data --> lista di eventi
            eventi_result.children().remove();
            if (data.length > 0) {
                getEventiURL(data);
            } else {
                handleError("", "#eventi", "Non è presente nessun evento!");
            }
        },
        error: function (request) {
            handleError(request, "#eventi", "Errore nel caricamento degli eventi.");
        },
        cache: false
    });
}


function downloadICSFile(startDate, endDate) {

    if (startDate && endDate) {

        let url = "rest/eventi/export?from=" + startDate + "&to=" + endDate;

        // Creazione di un elemento <a> temporaneo per il download del file .ics
        let downloadLink = document.createElement("a");
        downloadLink.href = url;

        let fileName = "calendar.ics";
        downloadLink.setAttribute("download", fileName);

        // Aggiunta dell'elemento <a> alla pagina e "simulazione" del click per avviare il download
        document.body.appendChild(downloadLink);
        downloadLink.click();

        // Rimozione dell'elemento <a> dopo il download
        document.body.removeChild(downloadLink);
    } else {
        if (!startDate)
            document.getElementById("startDate").focus();
        else
            document.getElementById("endDate").focus();
    }

}

function getEventiURL(data) {
    $.each(data, function (key) {
        $.ajax({
            url: "rest/" + data[key].split("/")[5] + "/" + data[key].split("/")[6],
            method: "GET",
            success: function (data) {
                inserisciEvento(data);
            },
            error: function (request) {
                handleError(request, "#eventi", "Errore generico");
            },
            cache: false
        });
    });
}

function inserisciEvento(data) {
    if (data) {
        eventi_result.show();
        eventi_empty.hide();

        eventi_result.append('<tr>');
        eventi_result.append('<td>' + data['nome'] + '</td>');
        eventi_result.append('<td>' + data['data_inizio'] + '</td>');
        eventi_result.append('<td>' + data['descrizione'] + '</td>');
        eventi_result.append('<td>' + data['email_responsabile'] + '</td>');
        eventi_result.append('</tr>');
    } else {
        eventi_result.children().remove();
        eventi_empty.show();
    }
    eventi_empty.text("Evento non trovato!");
}
