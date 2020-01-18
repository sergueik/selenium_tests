var getCoords = function(selector, debug) {
    var elem;
    try {
        elem = document.querySelector(selector);
    } catch (e) {
    }
    var t = null;
    if (elem != null) {
        t = elem.getBoundingClientRect();
        if (debug) {
            alert(JSON.stringify(t));
        }
    }
    return JSON.stringify(t);
}

var selector = arguments[0] || '#text';
var debug = arguments[1];
return getCoords(selector, debug);
