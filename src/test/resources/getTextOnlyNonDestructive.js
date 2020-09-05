/**
 * Computes text value of the all text contained in the
 * element by ignoring all other child nodes.
 */
// used in https://automated-testing.info/t/kak-poluchit-tekst-tega-bez-vlozhennyh-tegov-v-selenide/23607/13
getTextOnlyNonDestructive = function(element, debug) {
  var child, result = [];
  element.childNodes.forEach(
    function(childNode) {
      if (childNode.nodeName == '#text') {
        result.push(childNode.data);
      }
    });
  if (debug) {
    console.log(res);
  }
  return(result.join('')); // TODO: argument
}
var element = arguments[0];
var debug = arguments[1];
return getTextOnlyNonDestructive(element, debug);
