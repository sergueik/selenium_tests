/**
 * arguments[0] {Element} The event target.
 * arguments[1] {Boolean} Whether to concatenate element text fragments with spaces.
 */
// origin: https://qna.habr.com/q/789687
mouseWheel = function(element, delta, unusedFlags) {

  // https://www.w3schools.com/jsref/event_createevent.asp
  // https://www.w3schools.com/jsref/obj_wheelevent.asp
  // https://www.w3schools.com/jsref/obj_mouseevent.asp
  // https://developer.mozilla.org/en-US/docs/Web/API/Element/wheel_event
  try {
    var event = document.createEvent('MouseEvents');
    event.initEvent('WheelEvent', true, true);
    event.deltaX = 0;
    event.deltaY = delta;
    event.deltaMode = 0;
    element.dispatchEvent(event);
    console.log("called");
  } catch (e) {
    console.log(e.toString());
  }
}


return mouseWheel(arguments[0], arguments[1], arguments[2]);