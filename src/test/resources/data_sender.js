// based on: https://github.com/sunnylost/navigation-timing
add_element = function() {
    element = document.createElement('div');
    element.id = 'data';
    element.style.display = 'none';
    if (document.body != null) {
        document.body.appendChild(element);
    }
}
add_element();
get_load = function(options = {}) {
    var phases = [{
        name: 'Redirect',
        start: 'redirectStart',
        end: 'redirectEnd',
        index: 0
    }, {
        name: 'App cache',
        start: 'fetchStart',
        end: 'domainLookupStart',
        index: 1
    }, {
        name: 'DNS',
        start: 'domainLookupStart',
        end: 'domainLookupEnd',
        index: 2
    }, {
        name: 'TCP',
        start: 'connectStart',
        end: 'connectEnd',
        index: 3
    }, {
        name: 'Request',
        start: 'requestStart',
        end: 'responseStart',
        index: 4
    }, {
        name: 'Response',
        start: 'responseStart',
        end: 'responseEnd',
        index: 5
    }, {
        name: 'Processing',
        start: 'domLoading',
        end: 'domComplete',
        index: 6
    }, {
        name: 'onLoad',
        start: 'loadEventStart',
        end: 'loadEventEnd',
        index: 7
    }];

    var totalCost = 0;
    var t = performance.timing;
    var content = [];
    // visualization code removed 

    phases.forEach(function(v) {
        var start = t[v.start],
            end = t[v.end];
        totalCost += (v.value = (start == 0 ? 0 : (end - start)));
    });
    if (options['ladder']) {
        phases.sort(function(a, b) {
            return b.value - a.value;
        })

        phases.forEach(function(v, i) {
            v.width = (100 * v.value / totalCost).toFixed(3);
        })

        phases.sort(function(a, b) {
            return a.index - b.index;
        })

        var content = [];
        var left = 0;
        phases.forEach(function(v) {
            v.left = left;
            left += +v.width;
        })
    }
    data = JSON.stringify(phases);
    document.getElementById('data').innerHTML = data;
    if (options['debug'] === true) {
        confirm(data);
    }
};
get_load();