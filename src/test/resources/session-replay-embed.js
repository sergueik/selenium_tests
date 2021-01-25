// based on: https://github.com/mismith/session-replay/blob/master/src/widget/embed.js
var messagesRef = [];
// load dependencies
var deferreds = [];
element = document.createElement('div');
element.id = 'session-replay_events';
element.style.display = 'none';
if (document.body != null) {
  document.body.appendChild(element);
}
messagesRef.push(
	{
 event: 'init',
                timestamp: +new Date()
	}
)


;
[
    'https://cdn.rawgit.com/rafaelw/mutation-summary/master/src/mutation-summary.js',
    'https://cdn.rawgit.com/rafaelw/mutation-summary/master/util/tree-mirror.js',
].forEach(function(src) {
    deferreds.push(new Promise(function(resolve, reject) {
        var script = document.createElement('script');
        script.src = src;
        script.async = true;
        document.head.appendChild(script);

        script.addEventListener('load', resolve);
    }));
});
Promise.all(deferreds).then(function() {
    // wait for everything to be loaded
  	// ...
    // send event messages
    [
        'mousemove',
        'click',
        'dblclick',
        'contextmenu',
        'change',
        'scroll',
        'keydown',
        'keyup',
    ].map(function(type) {
        document.addEventListener(type, function(e) {
            if (e.target.tagName === 'INPUT') {
                // since certain prop changes don't affect the DOM, they aren't registered, so let's change the DOM to make sure they are reflected
                e.target.checked ? e.target.setAttribute('checked', true) : e.target.removeAttribute('checked');
                e.target.setAttribute('value', e.target.value);
            }

            messagesRef.push({
                event: e.type,
                timestamp: +new Date(),
                targetId: e.target.__mutation_summary_node_map_id__ || null, // @HACK: is this reliable?

                x: e.clientX || null,
                y: e.clientY || null,

                keyCode: e.keyCode || null,

                scrollTop: e.target.scrollTop || null,
            });
		document.getElementById('session-replay_events').innerHTML = JSON.stringify(messagesRef);
        }, true);
    });
    window.addEventListener('resize', function(e) {
        messagesRef.push({
            event: e.type,
            timestamp: +new Date(),

            width: window.innerWidth || null,
            height: window.innerHeight || null,
        });
    }, true);
});

