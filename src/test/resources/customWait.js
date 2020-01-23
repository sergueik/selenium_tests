// NOTE: not working
var customWait = function(selector, kind, endtime, delay, debug) {
  var element = null;
  if (kind.match(/id/)) {
    element = document.getElementById(selector);
  } else if (kind.match(/css/)) {
    element = document.querySelector(selector);
  } else {
    throw 'unsupported kind of locator';
  }
  if (typeof element == 'undefined' || element == null) {
    if (debug){  
      alert ("Resetting self"  + "\n" +"time: " + (new Date()).getTime().toString() + "\n" + "endtime: " + endtime );
    }
    var timerID = setTimeout(function() { customWait(selector, kind, endtime, delay, debug); }, delay);
    /*
       TODO: dislaying alert that would exit by causin the caller to receive
       org.openqa.selenium.UnhandledAlertException:
       unexpected alert open: {Alert text :  ...}
    */
    if ((new Date()).getTime() > endtime) {
     clearTimeout(timerID);g
     throw 'not found';
    }
  } else {
    if (debug){  
      alert ("found element:" + element);
    }
  }
}

var selector = arguments[0];
var kind = arguments[1] || 'id';
var timeout = arguments[2] || 10; // second
var delay = arguments[3] || 300; // millisecond
var debug = arguments[4];

var endtime =  (new Date()).getTime() + timeout * 1000;

customWait(selector, kind, endtime, delay, debug);
return (true);
