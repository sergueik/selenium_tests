add_element = function() {
  const id = 'messages';
  if (document.getElementById(id) == null) { 
    element = document.createElement('span');
    element.id = id;
    element.style.display = 'none';
    if (document.body != null) {
      document.body.appendChild(element);
    }
  }
}

window.addEventListener('message', function(event) {
    add_element();
    if (event.origin === window.location.protocol + '//' + window.location.hostname + ':' + window.location.port) {
        confirmation_message = 'Parent received message: ' + event.data.message;
    } else {
        confirmation_message = 'Origin ' + event.origin + ' is not allowed. Need: ' + window.location.protocol + '//' + window.location.hostname + ':' + window.location.port;
    }
    confirm(confirmation_message);
    console.log(confirmation_message);
    document.getElementById('messages').innerHTML += confirmation_message + '\n';
}, false);
