// DayTrader Charts - TradingView Lightweight Charts integration
(function() {
    'use strict';

    var darkTheme = {
        layout: {
            background: { type: 'solid', color: 'transparent' },
            textColor: '#8888a0'
        },
        grid: {
            vertLines: { color: 'rgba(42, 42, 69, 0.5)' },
            horzLines: { color: 'rgba(42, 42, 69, 0.5)' }
        },
        rightPriceScale: {
            borderColor: '#2a2a45'
        },
        timeScale: {
            borderColor: '#2a2a45',
            timeVisible: true,
            secondsVisible: false
        },
        crosshair: {
            vertLine: { color: '#3a3a5c', labelBackgroundColor: '#222240' },
            horzLine: { color: '#3a3a5c', labelBackgroundColor: '#222240' }
        }
    };

    window.DayTraderCharts = {
        initQuoteChart: function(containerId, symbol) {
            var container = document.getElementById(containerId);
            if (!container || typeof LightweightCharts === 'undefined') return null;

            var chart = LightweightCharts.createChart(container, Object.assign({}, darkTheme, {
                width: container.clientWidth,
                height: 300,
                handleScroll: { mouseWheel: true, pressedMouseMove: true },
                handleScale: { mouseWheel: true, pinch: true }
            }));

            var series = chart.addAreaSeries({
                topColor: 'rgba(59, 130, 246, 0.3)',
                bottomColor: 'rgba(59, 130, 246, 0.02)',
                lineColor: '#3b82f6',
                lineWidth: 2
            });

            // Fetch price history
            fetch('/api/charts/quote/' + encodeURIComponent(symbol))
                .then(function(r) { return r.json(); })
                .then(function(data) {
                    if (data && data.length > 0) {
                        var chartData = data.map(function(d) {
                            return { time: d.time, value: parseFloat(d.value) };
                        });
                        series.setData(chartData);
                        chart.timeScale().fitContent();
                    } else {
                        container.innerHTML = '<p style="color:#8888a0;text-align:center;padding:2rem;">No price history yet. Run the trading scenario to generate data.</p>';
                    }
                })
                .catch(function() {
                    container.innerHTML = '<p style="color:#8888a0;text-align:center;padding:2rem;">Failed to load chart data.</p>';
                });

            // Resize handler
            var resizeObserver = new ResizeObserver(function(entries) {
                for (var entry of entries) {
                    chart.applyOptions({ width: entry.contentRect.width });
                }
            });
            resizeObserver.observe(container);

            return { chart: chart, series: series };
        }
    };
})();
