/**
 * Changes the anchor target attribute value
 * arguments[0] {Element} The event target.
 */
// based on https://stackoverflow.com/questions/17547473/how-to-open-a-new-tab-using-selenium-webdriver
setTargetAttribute = function(element) {
  var attribute = document.createAttribute('target');
  attribute.value = '_blank';
  element.setAttributeNode(attribute);
}
return setTargetAttribute(arguments[0]);