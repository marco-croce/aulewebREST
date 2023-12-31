
const aule_result = $('#aule-result');
const aule_empty = $('#aule-empty');
const aule_container = $('#aule-container');

function getAula(idAula) {
    clear();
    toggleVisibility(aule_container);

    if (idAula) {
        $.ajax({
            url: "rest/aule/" + idAula,
            method: "GET",
            success: function (data) {
                //data -> aula con ID richiesto
                aule_result.children().remove();
                inserisciAula(data);
            },
            error: function (request) {
                handleError(request, "#aule", "Errore nel caricamento dell'aula.");
            },
            cache: false
        });
    } else {
        handleError("", "#aule", "Parametri insufficienti!");
    }
}

function inserisciAula(data) {
    if (data) {
        aule_result.show();
        aule_empty.hide();

        aule_result.append('<tr>');
        aule_result.append('<td>' + data['nome'] + '</td>');
        aule_result.append('<td>' + data['piano'] + '</td>');
        aule_result.append('<td>' + data['luogo'] + '</td>');
        aule_result.append('<td>' + data['edificio'] + '</td>');
        aule_result.append('<td>' + data['email_responsabile'] + '</td>');
        aule_result.append('</tr>');
    } else {
        aule_result.children().remove();
        aule_empty.show();
    }
    aule_empty.text("Aula non trovata!");
}
