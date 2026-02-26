// DayTrader Market Summary WebSocket Client (STOMP over SockJS)
(function() {
    'use strict';

    // Only connect if SockJS and StompJs are available
    if (typeof SockJS === 'undefined') {
        console.log('SockJS not loaded, skipping WebSocket connection');
        return;
    }

    var socket = new SockJS('/ws');
    var stompClient = Stomp.over(socket);

    // Disable debug logging
    stompClient.debug = null;

    stompClient.connect({}, function(frame) {
        console.log('Connected to WebSocket: ' + frame);

        stompClient.subscribe('/topic/market-updates', function(message) {
            var data = JSON.parse(message.body);
            console.log('Quote update: ' + data.symbol + ' ' + data.oldPrice + ' -> ' + data.newPrice);
            // Market summary is refreshed via HTMX polling, but real-time
            // quote updates could be used for visual indicators here.
        });
    }, function(error) {
        console.log('WebSocket connection error: ' + error);
    });
})();
