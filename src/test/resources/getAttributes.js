/**
 * Returns hash of all attributes of an DOM element
 *
 * arguments[0] {Element} The event target.
 */
// origin: https://stackoverflow.com/questions/27307131/selenium-webdriver-how-do-i-find-all-of-an-elements-attributes
// see also: getStyle
// http://www.htmlgoodies.com/html5/css/referencing-css3-properties-using-javascript.html#fbid=88eQV8NzD6Q 
getAttributes = function(element) {
  var items = {};
  for (index = 0; index < element.attributes.length; ++index) {
	// the "value" attribute is special, needs to be accessed through direct getter
      if (element.attributes[index].name === 'value'){
        items[element.attributes[index].name] = element.value;
      } else {
        items[element.attributes[index].name] = element.attributes[index].value;
      }
  };
  return items;
}

var element = arguments[0];
return getAttributes(element);