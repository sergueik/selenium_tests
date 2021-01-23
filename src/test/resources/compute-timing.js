// based on: https://github.com/sunnylost/navigation-timing
get_data = function(options = {}) {
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
    // visualization

    phases.forEach(function(v) {
        var start = t[v.start],
            end = t[v.end];
        totalCost += (v.value = (start == 0 ? 0 : (end - start)));
    });

    return JSON.stringify(phases);
}
return get_data(arguments[0], arguments[1]);
