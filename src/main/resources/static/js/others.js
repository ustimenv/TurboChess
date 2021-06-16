'use strict';

var script = document.createElement('script');              // Surely there's a better way to import jquery...
script.src = 'https://code.jquery.com/jquery-3.4.1.min.js';
script.type = 'text/javascript';
var stats = document.getElementById('stats');
var editbutton = document.getElementById('editbutton');
var editform = document.getElementById('form-editInfo');

function ocultar(){
    var stats = document.getElementById('stats');
    var editbutton = document.getElementById('editbutton');
    var editform = document.getElementById('form-editInfo');
    var alertSuccess = document.getElementById('msgSuccess');
    var alertError = document.getElementById('msgError');
    stats.style.display = "none";
    editbutton.style.display = "none";
    editform.style.display = "inline";
    if(alertError!= null && alertError.textContent!= null)alertError.style.display = "none";
    if(alertSuccess!= null && alertSuccess.textContent!= null)alertSuccess.style.display = "none";
}
function mostrar(){
    var stats = document.getElementById('stats');
    var editbutton = document.getElementById('editbutton');
    var editform = document.getElementById('form-editInfo');

    stats.style.display = "flex";
    editbutton.style.display = "flex";
    editform.style.display = "none";
}
function save(){
    console.log("entro a save");
    var editbutton = document.getElementById('editbutton');
    var msg = document.getElementById('msg');
    if( msg != null && msg.textContent != null){
        // Simular click

        ocultar();
        console.log("entro a text");
    }
}