/**
 * Computes cumulative value of the all text contained in the 
 * element by removing all other child nodes. NOTE: destructive.
 */
// based on http://stackoverflow.com/questions/6743912/get-the-pure-text-without-html-element-by-javascript
getTextOnly = function(element) {

var cnt,child, result;
if ( element.childNodes ) {
    for (cnt = 0; cnt < element.childNodes.length; cnt++) {
      child = element.childNodes[cnt];
      if (child.nodeType != 3  ) { 
        // result += ' => ' + child.localName + ' ' + child.nodeType.toString();
        child.remove();
        break;
      }
  }
}

return (element.innerText || element.textContent || '');
} 

return getTextOnly(arguments[0]);
