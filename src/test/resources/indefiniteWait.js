// Wait indefinitely

var indefiniteWait = function(selector, kind, delay) {
    var element;
    if (kind.match(/id/)) {
        element = document.getElementById(selector);
    } else if (kind.match(/css/)) {
        element = document.querySelector(selector);
    } else {
        throw new exception('unsupported kind of locator');
    }
    if (typeof element == 'undefined' || element == null) {
        setTimeout(function() { indefiniteWait(selector, kind, delay); }, delay);
    /*
       TODO: exit by dislaying alert that would cause the caller to receive  
       org.openqa.selenium.UnhandledAlertException:
       unexpected alert open: {Alert text :  ...}
    */
    }
}
var selector = arguments[0];
var kind = arguments[1] || 'id';
var delay = arguments[2] || 1000;
indefiniteWait(selector, kind, delay);
return (true);
