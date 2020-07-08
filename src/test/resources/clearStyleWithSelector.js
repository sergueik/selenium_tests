var selector = arguments[0];
var nodes = window.document.querySelectorAll(selector);
if (nodes) {
  var element = nodes[0]; 
  element.getAttribute('style', '');
}
return;