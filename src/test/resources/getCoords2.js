var getCoords2 = function(id, debug) {
    var elem;
    try {
        elem = document.getElementById(id);
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

var id = arguments[0] || 'text';
var debug = arguments[1];
return getCoords2(id, debug);