/**
 * Computes text value of the all text contained in the
 * element by ignoring nested DOM child nodes.
 */
// used in https://automated-testing.info/t/kak-poluchit-tekst-tega-bez-vlozhennyh-tegov-v-selenide/23607/13
// see also: https://automated-testing.info/t/poisk-teksta-v-elemente-bez-uchyota-dochernih-elementov/24285/7
getTextOnlyNonDestructive = function(element, debug) {
  var child, result = [];
  element.childNodes.forEach(
    function(childNode) {
      if (childNode.nodeName == '#text') {
        result.push(childNode.data);
      }
    });
  if (debug) {
    console.log(result);
  }
  return(result.join('')); // TODO: argument
}
var element = arguments[0];
var debug = arguments[1];
return getTextOnlyNonDestructive(element, debug);
