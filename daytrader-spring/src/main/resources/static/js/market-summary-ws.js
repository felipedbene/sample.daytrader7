// DayTrader Market Summary WebSocket Client (STOMP over SockJS)
(function() {
    'use strict';

    // Only connect if SockJS and StompJs are available
    if (typeof SockJS === 'undefined') {
        return;
    }

    var socket = new SockJS('/ws');
    var stompClient = Stomp.over(socket);

    // Disable debug logging
    stompClient.debug = null;

    stompClient.connect({}, function() {
        stompClient.subscribe('/topic/market-updates', function(message) {
            var data = JSON.parse(message.body);

            // Find all DOM elements with matching data-symbol attribute
            var elements = document.querySelectorAll('[data-symbol="' + data.symbol + '"]');
            elements.forEach(function(el) {
                var oldVal = parseFloat(el.textContent.replace('$', ''));
                var newVal = parseFloat(data.newPrice);

                // Update the displayed price
                el.textContent = '$' + newVal.toFixed(2);

                // Flash green (up) or red (down)
                el.classList.remove('tick-up', 'tick-down');
                void el.offsetWidth; // force reflow to restart animation
                el.classList.add(newVal >= oldVal ? 'tick-up' : 'tick-down');
            });
        });
    }, function(error) {
        console.log('WebSocket error: ' + error);
    });
})();
