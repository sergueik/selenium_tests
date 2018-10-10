function scrollIntoViewIfOutOfView(element, debug, force) {
  var topOfPage = window.pageYOffset || document.documentElement.scrollTop ||
    document.body.scrollTop;
  var heightOfPage = window.innerHeight ||
    document.documentElement.clientHeight ||
    document.body.clientHeight;
  var result = undefined;
  /*
   * 'let result = undefined;' would fail with FF with Exception:
   * org.openqa.selenium.JavascriptException: missing ; before statement
   */
  var element_Y = 0;
  var element_height = 0;
  if (document.layers) {
    element_Y = element.y;
    element_height = element.height;
  } else {
    for (var element_parent = element; element_parent &&
      element_parent.tagName != 'BODY'; element_parent = element_parent.offsetParent) {
      element_Y += element_parent.offsetTop;
    }
    element_height = element.offsetHeight;
  }
  if (force) {
    element.scrollIntoView({
      behavior: 'smooth'
    });
  } else {
    if ((topOfPage + heightOfPage) < (element_Y + element_height)) {
      element.scrollIntoView(false);
      result = false;
    } else if (element_Y < topOfPage) {
      result = true;
      element.scrollIntoView(true);
    }
  }
  if (debug) {
    return 'scrollIntoViewIfOutOfView scrolled: ' +
      +((result == undefined) ? 'undefined' : result.toString()) +
      ((force) ? ' (forced)' : '') + ' element_Y: ' +
      element_Y.toString();
  } else {
    return;
  }
}

var element = arguments[0];
var debug = arguments[1];
if (debug == undefined) {
  debug = false;
}
var force = arguments[2];
if (force == undefined) {
	force = true;
}
return scrollIntoViewIfOutOfView(element, debug, force);