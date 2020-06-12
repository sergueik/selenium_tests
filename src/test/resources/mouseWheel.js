/**
 * arguments[0] {Element} The event target.
 * arguments[1] {Boolean} Whether to concatenate element text fragments with spaces.
 */
// origin: https://qna.habr.com/q/789687
mouseWheel = function(element, delta, unusedFlags) {
  var evt = document.createEvent('MouseEvents');
  evt.initEvent('wheel', true, true);
  evt.deltaY = delta;
  element.dispatchEvent(evt);
}


return mouseWheel(arguments[0], arguments[1],, arguments[2]);