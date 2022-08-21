// see also: https://stackoverflow.com/questions/442404/retrieve-the-position-x-y-of-an-html-element
// 
var getCoords = function(selector, kind, debug) {
  var element;
  try {
    // NOTE: the 
    // if (var =~/id/) {
    // is not a valid Javascript, it is interpreted as
    // to become an assigment: if (var = ~ /id/) replacing var with a -1
    if (kind.match(/id/)) {
      if (debug) {
        alert('kind: ' + kind.toString() + '\t' + 'action: ' + 'getElementById');
      }
      element = document.getElementById(selector);
    } else if (kind.match(/css/)) {
      element = document.querySelector(selector);
    } else {
      element = selector;
    }
  } catch (e) {}
  var data = null;
  if (element != null) {
    data = element.getBoundingClientRect();
    //  var bodyRect = document.body.getBoundingClientRect(),
    //  elemRect = element.getBoundingClientRect(),
    //  offset   = elemRect.top - bodyRect.top;
    if (debug) {
      alert("result: " + JSON.stringify(data));
        /*
        NOTE: Every alert call will lead to an exception in Selenium end
        org.openqa.selenium.UnhandledAlertException:
        unexpected alert open: {Alert text : result: {"x":284.5874938964844,"y": ...}
        */
    }
  }
  return JSON.stringify(data);
}

var selector = arguments[0] || '#text';
var kind = arguments[1];
/*
if (!kind) { kind = 'id'}
*/
var debug = arguments[2];
var result = getCoords(selector, kind, debug);
return JSON.stringify({
  "selector": selector,
  "kind": kind,
  "debug": debug,
  "result": result
});
