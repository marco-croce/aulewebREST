$(document).ready(function () {
    clear();
});

function toggleVisibility(elem) {
    if (elem.css('display') === 'none') {
        elem.css('display', 'block');
    } else {
        elem.css('display', 'none');
    }
}

function clear() {
    eventi_container.css('display', 'none');
    eventi_result.hide();
    aule_container.css('display', 'none');
    aule_result.hide();
}

function handleError(request, table, msg) {
    const table_empty = $(table + "-empty");
    const table_result = $(table + "-result");

    table_empty.show();
    table_result.children().remove();
    switch (request.status) {
        case 401:   //Non autorizzato
            table_empty.text("Non sei autorizzato.");
            break;
        case 404:   //Elemento non trovato
            table_empty.text("Elemento non presente.");
            break;
        case 500:   //Server error
            table_empty.text("Errore del server.");
            break;
        default:    //altro
            table_empty.text(msg);
    }

}
