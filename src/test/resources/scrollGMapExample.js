/*
 google map scroll example
 origin: https://stackoverflow.com/questions/47274852/mouse-scroll-wheel-with-selenium-webdriver-on-element-without-scrollbar
*/
scrollbydeltaY = function(element, deltaY) {

  // var box = element.getBoundingClientRect();
  // var clientX = box.left + (arguments[2] || box.width / 2);
  // var clientY = box.top + (arguments[3] || box.height / 2);
  
  // var target = element.ownerDocument.elementFromPoint(clientX, clientY);
  var target = element.ownerDocument.elementFromPoint(100,100);
  // element.getBoundingClientRect is not a function
  for (var e = target; e; e = e.parentElement) {
    if (e === element) {
      target.dispatchEvent(new MouseEvent('mouseover', {
        view: window,
        bubbles: true,
        cancelable: true,
        clientX: clientX,
        clientY: clientY
      }));
      target.dispatchEvent(new MouseEvent('mousemove', {
        view: window,
        bubbles: true,
        cancelable: true,
        clientX: clientX,
        clientY: clientY
      }));
      target.dispatchEvent(new WheelEvent('wheel', {
        view: window,
        bubbles: true,
        cancelable: true,
        clientX: clientX,
        clientY: clientY,
        deltaY: deltaY
      }));
      return;
    }
  }
  return "Element is not interactable";

}
var element = arguments[0];
var deltaY = arguments[1];

scrollbydeltaY(element, deltaY);