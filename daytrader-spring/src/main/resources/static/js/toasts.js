// DayTrader Toast Notifications
(function() {
    'use strict';

    // Create toast container
    var container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);

    window.DayTraderToast = {
        show: function(message, type) {
            type = type || 'info';
            var toast = document.createElement('div');
            toast.className = 'toast toast-' + type;
            toast.textContent = message;
            container.appendChild(toast);

            // Trigger slide-in animation
            requestAnimationFrame(function() {
                toast.classList.add('toast-show');
            });

            // Auto-dismiss after 4 seconds
            setTimeout(function() {
                toast.classList.remove('toast-show');
                toast.classList.add('toast-hide');
                setTimeout(function() { toast.remove(); }, 300);
            }, 4000);
        }
    };

    // Listen for HTMX-triggered toast events
    document.body.addEventListener('showToast', function(e) {
        var detail = e.detail || {};
        DayTraderToast.show(detail.message || 'Done', detail.type || 'success');
    });
})();
