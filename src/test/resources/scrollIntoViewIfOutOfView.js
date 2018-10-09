function scrollIntoViewIfOutOfView(element, debug) {
  var topOfPage = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
  var heightOfPage = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
  let result = undefined;
  var element_Y = 0;
  var element_height = 0;
  if (document.layers) { // NS4
    element_Y = element.y;
    element_height = element.height;
  } else {
    for (var element_parent = element; element_parent && element_parent.tagName != 'BODY'; element_parent = element_parent.offsetParent) {
      element_Y += element_parent.offsetTop;
    }
    element_height = element.offsetHeight;
  }
  if ((topOfPage + heightOfPage) < (element_Y + element_height)) {
    element.scrollIntoView(false);
    result = false;
  } else if (element_Y < topOfPage) {
    result = true;
    element.scrollIntoView(true);
  }
  // else result will remain undefined 
  if (debug) {
    return 'scrollIntoViewIfOutOfView scrolled: ' +
      ((result == undefined) ? 'undefined' : result.toString()) + ' element_Y: ' + element_Y.toString();
  } else {
    return;
  }
}

var element = arguments[0];
var debug = arguments[1];
if (debug == undefined) {
  debug = false;
}
return scrollIntoViewIfOutOfView(element, debug);