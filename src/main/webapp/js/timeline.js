/* globals d3 */

var Timeline = {
  settings: {
    width: 1000,
    height: 200,
    transition_duration: 1000,
    headingHeight: 10,
    rectWidth: 10,
    rectHeight: 30,
  },

  refresh: function(container, handler) {
    // Set container
    Timeline.container = container;
    $(container).empty();

    // Set the catagories
    Timeline.categories = [
      { id: 1, key: "ARCHAEOLOGICAL", label: "Archaeological", color: "#08519c", },
      { id: 2, key: "HISTORIC", label: "Historic", color: "#a50f15" },
      { id: 3, key: "MODEL", label: "Model", color: "#006d2c" }
    ];

    // Set the time periods
    Timeline.ranges = { "millennium": 1000, "century" : 100, "decade" : 10};

    // Set calendar data
    Timeline.data = Search.facets.temporal;

    // Set opacities
    Timeline.opacities = [0.25, 0.5, 0.75, 1.0];
    Timeline.buckets = 5;

    // Initialize current overview type and history
    Timeline.type = 'millennium';
    Timeline.base = -7000;

    // Set handler function
    Timeline.handler = handler;

    // Parse data
    Timeline.parseData();

    // Create html elements
    Timeline.createElements();

    // Draw
    Timeline.createChart();
  },

  createElements: function() {
    Timeline.svg = d3.select(Timeline.container)
      .classed("timeline-container", true)
      .append("svg")
      .attr("preserveAspectRatio", "xMinYMin meet")
      .attr("viewBox", "0 0 "+Timeline.settings.width+" "+Timeline.settings.height)
      .classed("timeline-content-"+Timeline.type, true);

    Timeline.settings.rectHeight = (90 / Timeline.categories.length);

    Timeline.periodLabels = Timeline.svg.append("g").selectAll(".periodLabel")
      .data(Timeline.labels)
      .enter().append("text")
      .text(function(d) { return d.label; })
      .attr("x", function(d) { return ((d.id - 1) * Timeline.settings.rectWidth) + 5 + '%'; })
      .attr("y", '7%')
      .style("text-anchor", "middle")
      .classed("periodText", true);
  },

  createChart: function(category) {
    Timeline.categories.forEach(category => {
      var colorScale = d3.scaleQuantile()
        .domain([0, Timeline.buckets - 1, d3.max(Timeline.data, function(d) { return d.value; })])
        .range(Timeline.opacities);

      var period = Timeline.svg.append("g").selectAll(".period")
        .data(Timeline.data, function(d) { return d.category + ':' + d.period; });
      period.append("title");
      var rect = period.enter().append("rect");
      rect
        .attr("class", function(d, i) { return "bordered"; })
        .attr("data-category", function(d, i) { return d.category; })
        .attr("data-period", function(d, i) { return d.period; })
        .attr("x", function(d) { return ((d.period - 1) * Timeline.settings.rectWidth) + '%'; })
        .attr("y", function(d) { return (d.category - 1) * Timeline.settings.rectHeight + Timeline.settings.headingHeight + '%'; })
        .attr("width", function(d) { return Timeline.settings.rectWidth + '%'; })
        .attr("height", function(d) { return Timeline.settings.rectHeight + '%'; })
        .on('mouseover', function(d, i) {
          Timeline.hover(d);
          // tooltip = d3.select(chart.selector())
          //   .append('div')
          //   .attr('class', 'day-cell-tooltip')
          //   .html(tooltipHTMLForDate(d))
          //   .style('left', function () { return Math.floor(i / 7) * SQUARE_LENGTH + 'px'; })
          //   .style('top', function () {
          //     return formatWeekday(d.getDay()) * (SQUARE_LENGTH + SQUARE_PADDING) + MONTH_LABEL_PADDING * 2 + 'px';
          //   });
        })
        .on('mouseout', function(d, i) {
          Timeline.hoverRect.remove();
        })
        .on('click', function(d, i) {
          Timeline.shrink(d);
        })
        .transition().duration(Timeline.settings.transition_duration)
        .style("fill", function(d, i) { return Timeline.categories[d.category - 1].color; })
        .style("fill-opacity", function(d, i) { return (d.value ? colorScale(d.value) : 0.05); });
      rect
        .append("title").text(function(d) { return d.value; });

      // period.select("title").text(function(d) { return d.value; });
      period.exit().remove();
    });
  },


  hover: function(d) {
    var _d = d;
    Timeline.hoverRect = Timeline.svg.append('rect');
    Timeline.hoverRect
      .attr("x", function(d) { return ((_d.period - 1) * Timeline.settings.rectWidth) + 0.25 + '%'; })
      .attr("y", function(d) { return Timeline.settings.headingHeight + 1 + '%'; })
      .attr("width", function(d) { return Timeline.settings.rectWidth - 0.5 + '%'; })
      .attr("height", function(d) { return (Timeline.settings.rectHeight * 3) - 2 + '%'; })
      .classed("highlighted", true);
  },


  shrink: function(d) {
    var _d = d;
    console.log("period " + d.period + " clicked");
    Timeline.clickedRect = Timeline.svg.append('rect');
    Timeline.clickedRect
      .attr("x", function(d) { return ((_d.period - 1) * Timeline.settings.rectWidth) + 0.25 + '%'; })
      .attr("y", function(d) { return Timeline.settings.headingHeight + 1 + '%'; })
      .attr("width", function(d) { return Timeline.settings.rectWidth - 0.5 + '%'; })
      .attr("height", function(d) { return (Timeline.settings.rectHeight * 3) - 2 + '%'; })
      .classed("highlighted", true);
    Timeline.periodLabels.classed('hidden', true);
    Timeline.svg.transition().duration(Timeline.settings.transition_duration)
      .attr("viewBox", "0 0 "+Timeline.settings.width+" "+(Timeline.settings.height / 5));
    Timeline.svg.selectAll('rect')
      .on('mouseover', null)
      .on('mouseout', null)
      .on('click', function(d) {
        Timeline.expand();
      });
  },


  expand: function() {
    Timeline.clickedRect.remove();
    Timeline.svg.transition().duration(Timeline.settings.transition_duration)
      .attr("viewBox", "0 0 "+Timeline.settings.width+" "+Timeline.settings.height);
    Timeline.periodLabels.classed('hidden', false);
    Timeline.svg.selectAll('rect')
      .on('mouseover', function(d) {
        Timeline.hover(d);
      })
      .on('mouseout', function(d) {
        Timeline.hoverRect.remove();
      })
      .on('click', function(d) {
        Timeline.shrink(d);
      });
  },

  parseData: function() {
    if (!Timeline.data) { return; }

    var raw_data = Timeline.data;
    Timeline.data = [];
    Timeline.labels = [];

    Timeline.categories.forEach(category => {
      for (var i = 0; i < 10; i++) {
        var id = i+1;
        var period = i * Timeline.ranges[Timeline.type] + Timeline.base + "";
        Timeline.labels[i] = { id: id, key: period, label: period};

        var value = 0;
        if (raw_data[category.key])
          if (raw_data[category.key][Timeline.type])
            if (raw_data[category.key][Timeline.type][period])
              value = raw_data[category.key][Timeline.type][period];
        Timeline.data.push({category: category.id, period: id, value: value});
      }
    });

    console.log(Timeline.labels);
    console.log(Timeline.data);
    console.log(raw_data);
  },

};