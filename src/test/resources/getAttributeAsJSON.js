function getAttributeAsJSON(path) {
    try {
        var element = document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
        if (element) {
            var result = {};
            for (p in element) {

                console.log(p);
                result[p] = element[p];
            }
            return JSON.stringify(result);

        }
    } catch (e) {
        return JSON.stringify({
            "exception": e.toString()
        });
    }
}
var path = arguments[0];
return getAttributeAsJSON(path);
