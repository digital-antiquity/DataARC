'use strict';

/* globals d3 */

var Timeline = {

  settings: {
    gutter: 5,
    item_gutter: 1,
    width: 1000,
    height: 200,
    item_size: 10,
    label_padding: 40,
    max_block_height: 20,
    transition_duration: 500,
    tooltip_width: 250,
    tooltip_padding: 15,
  },


  /**
   * Initialize
   */
  refresh: function(container, handler) {
    // Set container
    Timeline.container = container;
    $(container).empty();

    // Set calendar data
    Timeline.data = timeline_data; // Search.facets.temporal;

    // Set calendar color
    Timeline.color = '#ff4500';

    // Initialize current overview type and history
    Timeline.overview = 'global';
    Timeline.history = ['global'];
    Timeline.selected = {};

    // Set handler function
    Timeline.handler = handler;

    // No transition to start with
    Timeline.in_transition = false;

    // Create html elementsfor the calendar
    Timeline.createElements();

    // Parse data for summary details
    Timeline.parseData();

    // Draw the chart
    Timeline.drawChart();
  },


  /**
   * Create html elements for the calendar
   */
  createElements: function() {
    // Create svg element
    var svg = d3.select(Timeline.container).append('svg')
      .attr('class', 'svg');

    // Create other svg elements
    Timeline.items = svg.append('g');
    Timeline.labels = svg.append('g');
    Timeline.buttons = svg.append('g');

    // Add tooltip to the same element as main svg
    Timeline.tooltip = d3.select(Timeline.container).append('div')
      .attr('class', 'heatmap-tooltip')
      .style('opacity', 0);

    // Calculate dimensions based on available width
    var calcDimensions = function() {

      var dayIndex = Math.round((moment() - moment().subtract(1, 'year').startOf('week')) / 86400000);
      var colIndex = Math.trunc(dayIndex / 7);
      var numWeeks = colIndex + 1;

      Timeline.settings.width = $(Timeline.container).outerWidth() < 1000 ? 1000 : $(Timeline.container).outerWidth();
      Timeline.settings.item_size = ((Timeline.settings.width - Timeline.settings.label_padding) / numWeeks - Timeline.settings.gutter);
      Timeline.settings.height = Timeline.settings.label_padding + 7 * (Timeline.settings.item_size + Timeline.settings.gutter);
      svg.attr('width', Timeline.settings.width)
        .attr('height', Timeline.settings.height);

      if (!!Timeline.data && !!Timeline.data[0].summary) {
        Timeline.drawChart();
      }
    };
    calcDimensions();

    window.onresize = function(event) {
      calcDimensions();
    };
  },


  /**
   * Parse data for summary in case it was not provided
   */
  parseData: function() {
    if (!Timeline.data) { return; }

    // Get daily summary if that was not provided
    if (!Timeline.data[0].summary) {
      Timeline.data.map(function(d) {
        var summary = d.details.reduce(function(uniques, project) {
          if (!uniques[project.name]) {
            uniques[project.name] = {
              'value': project.value
            };
          } else {
            uniques[project.name].value += project.value;
          }
          return uniques;
        }, {});
        var unsorted_summary = Object.keys(summary).map(function(key) {
          return {
            'name': key,
            'value': summary[key].value
          };
        });
        d.summary = unsorted_summary.sort(function(a, b) {
          return b.value - a.value;
        });
        return d;
      });
    }
  },


  /**
   * Draw the chart based on the current overview type
   */
  drawChart: function() {
    if (Timeline.overview === 'global') {
      Timeline.drawGlobalOverview();
    } else if (Timeline.overview === 'year') {
      Timeline.drawYearOverview();
    } else if (Timeline.overview === 'month') {
      Timeline.drawMonthOverview();
    } else if (Timeline.overview === 'week') {
      Timeline.drawWeekOverview();
    } else if (Timeline.overview === 'day') {
      Timeline.drawDayOverview();
    } else if (Timeline.overview === 'millenium') {
      Timeline.drawMilleniumOverview();
    } else if (Timeline.overview === 'century') {
      Timeline.drawCenturyOverview();
    } else if (Timeline.overview === 'decade') {
      Timeline.drawDecadeOverview();
    }
  },


  /**
   * Draw global overview (multiple years)
   */
  drawGlobalOverview: function() {
    // Add current overview to the history
    if (Timeline.history[Timeline.history.length - 1] !== Timeline.overview) {
      Timeline.history.push(Timeline.overview);
    }

    // Define start and end of the dataset
    var start = moment(Timeline.data[0].date).startOf('year');
    var end = moment(Timeline.data[Timeline.data.length - 1].date).endOf('year');

    // Define array of years and total values
    var year_data = d3.timeYears(start, end).map(function(d) {
      var date = moment(d);
      return {
        'date': date,
        'total': Timeline.data.reduce(function(prev, current) {
          if (moment(current.date).year() === date.year()) {
            prev += current.total;
          }
          return prev;
        }, 0),
        'summary': function() {
          var summary = Timeline.data.reduce(function(summary, d) {
            if (moment(d.date).year() === date.year()) {
              for (var i = 0; i < d.summary.length; i++) {
                if (!summary[d.summary[i].name]) {
                  summary[d.summary[i].name] = {
                    'value': d.summary[i].value,
                  };
                } else {
                  summary[d.summary[i].name].value += d.summary[i].value;
                }
              }
            }
            return summary;
          }, {});
          var unsorted_summary = Object.keys(summary).map(function(key) {
            return {
              'name': key,
              'value': summary[key].value
            };
          });
          return unsorted_summary.sort(function(a, b) {
            return b.value - a.value;
          });
        }(),
      };
    });

    // Calculate max value of all the years in the dataset
    var max_value = d3.max(year_data, function(d) {
      return d.total;
    });

    // Define year labels and axis
    var year_labels = d3.timeYears(start, end).map(function(d) {
      return moment(d);
    });
    var yearScale = d3.scaleBand()
      .rangeRound([0, Timeline.settings.width])
      .padding([0.05])
      .domain(year_labels.map(function(d) {
        return d.year();
      }));

    // Add month data items to the overview
    Timeline.items.selectAll('.item-block-year').remove();
    var item_block = Timeline.items.selectAll('.item-block-year')
      .data(year_data)
      .enter()
      .append('rect')
      .attr('class', 'item item-block-year')
      .attr('width', function() {
        return (Timeline.settings.width - Timeline.settings.label_padding) / year_labels.length - Timeline.settings.gutter * 5;
      })
      .attr('height', function() {
        return Timeline.settings.height - Timeline.settings.label_padding;
      })
      .attr('transform', function(d) {
        return 'translate(' + yearScale(d.date.year()) + ',' + Timeline.settings.tooltip_padding * 2 + ')';
      })
      .attr('fill', function(d) {
        var color = d3.scaleLinear()
          .range(['#ffffff', Timeline.color || '#ff4500'])
          .domain([-0.15 * max_value, max_value]);
        return color(d.total) || '#ff4500';
      })
      .on('click', function(d) {
        if (Timeline.in_transition) { return; }

        // Set in_transition flag
        Timeline.in_transition = true;

        // Set selected date to the one clicked on
        Timeline.selected = d;

        // Hide tooltip
        Timeline.hideTooltip();

        // Remove all month overview related items and labels
        Timeline.removeGlobalOverview();

        // Redraw the chart
        Timeline.overview = 'year';
        Timeline.drawChart();
      })
      .style('opacity', 0)
      .on('mouseover', function(d) {
        if (Timeline.in_transition) { return; }
        // Tooltip
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }
      })
      .transition()
      .delay(function(d, i) {
        return Timeline.settings.transition_duration * (i + 1) / 10;
      })
      .duration(function() {
        return Timeline.settings.transition_duration;
      })
      .ease(d3.easeLinear)
      .style('opacity', 1)
      .call(function(transition, callback) {
        if (transition.empty()) {
          callback();
        }
        var n = 0;
        transition
          .each(function() {++n; })
          .on('end', function() {
            if (!--n) {
              callback.apply(this, arguments);
            }
          });
      }, function() {
        Timeline.in_transition = false;
      });

    // Add year labels
    Timeline.labels.selectAll('.label-year').remove();
    Timeline.labels.selectAll('.label-year')
      .data(year_labels)
      .enter()
      .append('text')
      .attr('class', 'label label-year')
      .attr('font-size', function() {
        return Math.floor(Timeline.settings.label_padding / 3) + 'px';
      })
      .text(function(d) {
        return d.year();
      })
      .attr('x', function(d) {
        return yearScale(d.year());
      })
      .attr('y', Timeline.settings.label_padding / 2)
      .on('mouseenter', function(year_label) {
        if (Timeline.in_transition) { return; }

        Timeline.items.selectAll('.item-block-year')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', function(d) {
            return (moment(d.date).year() === year_label.year()) ? 1 : 0.1;
          });
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }

        Timeline.items.selectAll('.item-block-year')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', 1);
      })
      .on('click', function(d) {
        if (Timeline.in_transition) { return; }

        // Set in_transition flag
        Timeline.in_transition = true;

        // Set selected month to the one clicked on
        Timeline.selected = d;

        // Hide tooltip
        Timeline.hideTooltip();

        // Remove all year overview related items and labels
        Timeline.removeGlobalOverview();

        // Redraw the chart
        Timeline.overview = 'year';
        Timeline.drawChart();
      });
  },


/**
   * Draw millenium overview
   */
  drawMilleniumOverview: function() {
    // Add current overview to the history
    if (Timeline.history[Timeline.history.length - 1] !== Timeline.overview) {
      Timeline.history.push(Timeline.overview);
    }

    // Define start and end date of the selected year
    var start_of_year = moment(Timeline.selected.date).startOf('year');
    var end_of_year = moment(Timeline.selected.date).endOf('year');

    // Filter data down to the selected year
    var year_data = Timeline.data.filter(function(d) {
      return start_of_year <= moment(d.date) && moment(d.date) < end_of_year;
    });

    // Calculate max value of the year data
    var max_value = d3.max(year_data, function(d) {
      return d.total;
    });

    var color = d3.scaleLinear()
      .range(['#ffffff', Timeline.color || '#ff4500'])
      .domain([-0.15 * max_value, max_value]);

    var calcItemX = function(d) {
      var date = moment(d.date);
      var dayIndex = Math.round((date - moment(start_of_year).startOf('week')) / 86400000);
      var colIndex = Math.trunc(dayIndex / 7);
      return colIndex * (Timeline.settings.item_size + Timeline.settings.gutter) + Timeline.settings.label_padding;
    };
    var calcItemY = function(d) {
      return Timeline.settings.label_padding + moment(d.date).weekday() * (Timeline.settings.item_size + Timeline.settings.gutter);
    };
    var calcItemSize = function(d) {
      if (max_value <= 0) { return Timeline.settings.item_size; }
      return Timeline.settings.item_size * 0.75 + (Timeline.settings.item_size * d.total / max_value) * 0.25;
    };

    Timeline.items.selectAll('.item-circle').remove();
    Timeline.items.selectAll('.item-circle')
      .data(year_data)
      .enter()
      .append('rect')
      .attr('class', 'item item-circle')
      .style('opacity', 0)
      .attr('x', function(d) {
        return calcItemX(d) + (Timeline.settings.item_size - calcItemSize(d)) / 2;
      })
      .attr('y', function(d) {
        return calcItemY(d) + (Timeline.settings.item_size - calcItemSize(d)) / 2;
      })
      .attr('rx', function(d) {
        return calcItemSize(d);
      })
      .attr('ry', function(d) {
        return calcItemSize(d);
      })
      .attr('width', function(d) {
        return calcItemSize(d);
      })
      .attr('height', function(d) {
        return calcItemSize(d);
      })
      .attr('fill', function(d) {
        return (d.total > 0) ? color(d.total) : 'transparent';
      })
      .on('click', function(d) {
        if (Timeline.in_transition) { return; }

        // Don't transition if there is no data to show
        if (d.total === 0) { return; }

        Timeline.in_transition = true;

        // Set selected date to the one clicked on
        Timeline.selected = d;

        // Hide tooltip
        Timeline.hideTooltip();

        // Remove all year overview related items and labels
        Timeline.removeYearOverview();

        // Redraw the chart
        Timeline.overview = 'day';
        Timeline.drawChart();
      })
      .on('mouseover', function(d) {
        if (Timeline.in_transition) { return; }

        // Pulsating animation
        var circle = d3.select(this);
        (function repeat() {
          circle = circle.transition()
            .duration(Timeline.settings.transition_duration)
            .ease(d3.easeLinear)
            .attr('x', function(d) {
              return calcItemX(d) - (Timeline.settings.item_size * 1.1 - Timeline.settings.item_size) / 2;
            })
            .attr('y', function(d) {
              return calcItemY(d) - (Timeline.settings.item_size * 1.1 - Timeline.settings.item_size) / 2;
            })
            .attr('width', Timeline.settings.item_size * 1.1)
            .attr('height', Timeline.settings.item_size * 1.1)
            .transition()
            .duration(Timeline.settings.transition_duration)
            .ease(d3.easeLinear)
            .attr('x', function(d) {
              return calcItemX(d) + (Timeline.settings.item_size - calcItemSize(d)) / 2;
            })
            .attr('y', function(d) {
              return calcItemY(d) + (Timeline.settings.item_size - calcItemSize(d)) / 2;
            })
            .attr('width', function(d) {
              return calcItemSize(d);
            })
            .attr('height', function(d) {
              return calcItemSize(d);
            })
            .on('end', repeat);
        })();

        // // Construct tooltip
        // var tooltip_html = '';
        // tooltip_html += '<div class="header"><strong>' + (d.total ? Timeline.formatTime(d.total) : 'No time') + ' tracked</strong></div>';
        // tooltip_html += '<div>on ' + moment(d.date).format('dddd, MMM Do YYYY') + '</div><br>';

        // // Add summary to the tooltip
        // for (var i = 0; i < d.summary.length; i++) {
        //   tooltip_html += '<div><span><strong>' + d.summary[i].name + '</strong></span>';
        //   tooltip_html += '<span>' + Timeline.formatTime(d.summary[i].value) + '</span></div>';
        // };

        // // Calculate tooltip position
        // var x = calcItemX(d) + Timeline.settings.item_size;
        // if (Timeline.settings.width - x < (Timeline.settings.tooltip_width + Timeline.settings.tooltip_padding * 3)) {
        //   x -= Timeline.settings.tooltip_width + Timeline.settings.tooltip_padding * 2;
        // }
        // var y = calcItemY(d) + Timeline.settings.item_size;

        // // Show tooltip
        // Timeline.tooltip.html(tooltip_html)
        //   .style('left', x + 'px')
        //   .style('top', y + 'px')
        //   .transition()
        //   .duration(Timeline.settings.transition_duration / 2)
        //   .ease(d3.easeLinear)
        //   .style('opacity', 1);
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }

        // Set circle radius back to what it's supposed to be
        d3.select(this).transition()
          .duration(Timeline.settings.transition_duration / 2)
          .ease(d3.easeLinear)
          .attr('x', function(d) {
            return calcItemX(d) + (Timeline.settings.item_size - calcItemSize(d)) / 2;
          })
          .attr('y', function(d) {
            return calcItemY(d) + (Timeline.settings.item_size - calcItemSize(d)) / 2;
          })
          .attr('width', function(d) {
            return calcItemSize(d);
          })
          .attr('height', function(d) {
            return calcItemSize(d);
          });

        // Hide tooltip
        Timeline.hideTooltip();
      })
      .transition()
      .delay(function() {
        return (Math.cos(Math.PI * Math.random()) + 1) * Timeline.settings.transition_duration;
      })
      .duration(function() {
        return Timeline.settings.transition_duration;
      })
      .ease(d3.easeLinear)
      .style('opacity', 1)
      .call(function(transition, callback) {
        if (transition.empty()) {
          callback();
        }
        var n = 0;
        transition
          .each(function() {++n; })
          .on('end', function() {
            if (!--n) {
              callback.apply(this, arguments);
            }
          });
      }, function() {
        Timeline.in_transition = false;
      });

    // Add month labels
    var month_labels = d3.timeMonths(start_of_year, end_of_year);
    var monthScale = d3.scaleLinear()
      .range([0, Timeline.settings.width])
      .domain([0, month_labels.length]);
    Timeline.labels.selectAll('.label-month').remove();
    Timeline.labels.selectAll('.label-month')
      .data(month_labels)
      .enter()
      .append('text')
      .attr('class', 'label label-month')
      .attr('font-size', function() {
        return Math.floor(Timeline.settings.label_padding / 3) + 'px';
      })
      .text(function(d) {
        return d.toLocaleDateString('en-us', { month: 'short' });
      })
      .attr('x', function(d, i) {
        return monthScale(i) + (monthScale(i) - monthScale(i - 1)) / 2;
      })
      .attr('y', Timeline.settings.label_padding / 2)
      .on('mouseenter', function(d) {
        if (Timeline.in_transition) { return; }

        var selected_month = moment(d);
        Timeline.items.selectAll('.item-circle')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', function(d) {
            return moment(d.date).isSame(selected_month, 'month') ? 1 : 0.1;
          });
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }

        Timeline.items.selectAll('.item-circle')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', 1);
      })
      .on('click', function(d) {
        if (Timeline.in_transition) { return; }

        // Check month data
        var month_data = Timeline.data.filter(function(e) {
          return moment(d).startOf('month') <= moment(e.date) && moment(e.date) < moment(d).endOf('month');
        });

        // Don't transition if there is no data to show
        if (!month_data.length) { return; }

        // Set selected month to the one clicked on
        Timeline.selected = { date: d };

        Timeline.in_transition = true;

        // Hide tooltip
        Timeline.hideTooltip();

        // Remove all year overview related items and labels
        Timeline.removeYearOverview();

        // Redraw the chart
        Timeline.overview = 'month';
        Timeline.drawChart();
      });

    // Add day labels
    var day_labels = d3.timeDays(moment().startOf('week'), moment().endOf('week'));
    var dayScale = d3.scaleBand()
      .rangeRound([Timeline.settings.label_padding, Timeline.settings.height])
      .domain(day_labels.map(function(d) {
        return moment(d).weekday();
      }));
    Timeline.labels.selectAll('.label-day').remove();
    Timeline.labels.selectAll('.label-day')
      .data(day_labels)
      .enter()
      .append('text')
      .attr('class', 'label label-day')
      .attr('x', Timeline.settings.label_padding / 3)
      .attr('y', function(d, i) {
        return dayScale(i) + dayScale.bandwidth() / 1.75;
      })
      .style('text-anchor', 'left')
      .attr('font-size', function() {
        return Math.floor(Timeline.settings.label_padding / 3) + 'px';
      })
      .text(function(d) {
        return moment(d).format('dddd')[0];
      })
      .on('mouseenter', function(d) {
        if (Timeline.in_transition) { return; }

        var selected_day = moment(d);
        Timeline.items.selectAll('.item-circle')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', function(d) {
            return (moment(d.date).day() === selected_day.day()) ? 1 : 0.1;
          });
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }

        Timeline.items.selectAll('.item-circle')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', 1);
      });

    // Add button to switch back to previous overview
    Timeline.drawButton();
  },


  /**
   * Draw year overview
   */
  drawYearOverview: function() {
    // Add current overview to the history
    if (Timeline.history[Timeline.history.length - 1] !== Timeline.overview) {
      Timeline.history.push(Timeline.overview);
    }

    // Define start and end date of the selected year
    var start_of_year = moment(Timeline.selected.date).startOf('year');
    var end_of_year = moment(Timeline.selected.date).endOf('year');

    // Filter data down to the selected year
    var year_data = Timeline.data.filter(function(d) {
      return start_of_year <= moment(d.date) && moment(d.date) < end_of_year;
    });

    // Calculate max value of the year data
    var max_value = d3.max(year_data, function(d) {
      return d.total;
    });

    var color = d3.scaleLinear()
      .range(['#ffffff', Timeline.color || '#ff4500'])
      .domain([-0.15 * max_value, max_value]);

    var calcItemX = function(d) {
      var date = moment(d.date);
      var dayIndex = Math.round((date - moment(start_of_year).startOf('week')) / 86400000);
      var colIndex = Math.trunc(dayIndex / 7);
      return colIndex * (Timeline.settings.item_size + Timeline.settings.gutter) + Timeline.settings.label_padding;
    };
    var calcItemY = function(d) {
      return Timeline.settings.label_padding + moment(d.date).weekday() * (Timeline.settings.item_size + Timeline.settings.gutter);
    };
    var calcItemSize = function(d) {
      if (max_value <= 0) { return Timeline.settings.item_size; }
      return Timeline.settings.item_size * 0.75 + (Timeline.settings.item_size * d.total / max_value) * 0.25;
    };

    Timeline.items.selectAll('.item-circle').remove();
    Timeline.items.selectAll('.item-circle')
      .data(year_data)
      .enter()
      .append('rect')
      .attr('class', 'item item-circle')
      .style('opacity', 0)
      .attr('x', function(d) {
        return calcItemX(d) + (Timeline.settings.item_size - calcItemSize(d)) / 2;
      })
      .attr('y', function(d) {
        return calcItemY(d) + (Timeline.settings.item_size - calcItemSize(d)) / 2;
      })
      .attr('rx', function(d) {
        return calcItemSize(d);
      })
      .attr('ry', function(d) {
        return calcItemSize(d);
      })
      .attr('width', function(d) {
        return calcItemSize(d);
      })
      .attr('height', function(d) {
        return calcItemSize(d);
      })
      .attr('fill', function(d) {
        return (d.total > 0) ? color(d.total) : 'transparent';
      })
      .on('click', function(d) {
        if (Timeline.in_transition) { return; }

        // Don't transition if there is no data to show
        if (d.total === 0) { return; }

        Timeline.in_transition = true;

        // Set selected date to the one clicked on
        Timeline.selected = d;

        // Hide tooltip
        Timeline.hideTooltip();

        // Remove all year overview related items and labels
        Timeline.removeYearOverview();

        // Redraw the chart
        Timeline.overview = 'day';
        Timeline.drawChart();
      })
      .on('mouseover', function(d) {
        if (Timeline.in_transition) { return; }

        // Pulsating animation
        var circle = d3.select(this);
        (function repeat() {
          circle = circle.transition()
            .duration(Timeline.settings.transition_duration)
            .ease(d3.easeLinear)
            .attr('x', function(d) {
              return calcItemX(d) - (Timeline.settings.item_size * 1.1 - Timeline.settings.item_size) / 2;
            })
            .attr('y', function(d) {
              return calcItemY(d) - (Timeline.settings.item_size * 1.1 - Timeline.settings.item_size) / 2;
            })
            .attr('width', Timeline.settings.item_size * 1.1)
            .attr('height', Timeline.settings.item_size * 1.1)
            .transition()
            .duration(Timeline.settings.transition_duration)
            .ease(d3.easeLinear)
            .attr('x', function(d) {
              return calcItemX(d) + (Timeline.settings.item_size - calcItemSize(d)) / 2;
            })
            .attr('y', function(d) {
              return calcItemY(d) + (Timeline.settings.item_size - calcItemSize(d)) / 2;
            })
            .attr('width', function(d) {
              return calcItemSize(d);
            })
            .attr('height', function(d) {
              return calcItemSize(d);
            })
            .on('end', repeat);
        })();

        // // Construct tooltip
        // var tooltip_html = '';
        // tooltip_html += '<div class="header"><strong>' + (d.total ? Timeline.formatTime(d.total) : 'No time') + ' tracked</strong></div>';
        // tooltip_html += '<div>on ' + moment(d.date).format('dddd, MMM Do YYYY') + '</div><br>';

        // // Add summary to the tooltip
        // for (var i = 0; i < d.summary.length; i++) {
        //   tooltip_html += '<div><span><strong>' + d.summary[i].name + '</strong></span>';
        //   tooltip_html += '<span>' + Timeline.formatTime(d.summary[i].value) + '</span></div>';
        // };

        // // Calculate tooltip position
        // var x = calcItemX(d) + Timeline.settings.item_size;
        // if (Timeline.settings.width - x < (Timeline.settings.tooltip_width + Timeline.settings.tooltip_padding * 3)) {
        //   x -= Timeline.settings.tooltip_width + Timeline.settings.tooltip_padding * 2;
        // }
        // var y = calcItemY(d) + Timeline.settings.item_size;

        // // Show tooltip
        // Timeline.tooltip.html(tooltip_html)
        //   .style('left', x + 'px')
        //   .style('top', y + 'px')
        //   .transition()
        //   .duration(Timeline.settings.transition_duration / 2)
        //   .ease(d3.easeLinear)
        //   .style('opacity', 1);
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }

        // Set circle radius back to what it's supposed to be
        d3.select(this).transition()
          .duration(Timeline.settings.transition_duration / 2)
          .ease(d3.easeLinear)
          .attr('x', function(d) {
            return calcItemX(d) + (Timeline.settings.item_size - calcItemSize(d)) / 2;
          })
          .attr('y', function(d) {
            return calcItemY(d) + (Timeline.settings.item_size - calcItemSize(d)) / 2;
          })
          .attr('width', function(d) {
            return calcItemSize(d);
          })
          .attr('height', function(d) {
            return calcItemSize(d);
          });

        // Hide tooltip
        Timeline.hideTooltip();
      })
      .transition()
      .delay(function() {
        return (Math.cos(Math.PI * Math.random()) + 1) * Timeline.settings.transition_duration;
      })
      .duration(function() {
        return Timeline.settings.transition_duration;
      })
      .ease(d3.easeLinear)
      .style('opacity', 1)
      .call(function(transition, callback) {
        if (transition.empty()) {
          callback();
        }
        var n = 0;
        transition
          .each(function() {++n; })
          .on('end', function() {
            if (!--n) {
              callback.apply(this, arguments);
            }
          });
      }, function() {
        Timeline.in_transition = false;
      });

    // Add month labels
    var month_labels = d3.timeMonths(start_of_year, end_of_year);
    var monthScale = d3.scaleLinear()
      .range([0, Timeline.settings.width])
      .domain([0, month_labels.length]);
    Timeline.labels.selectAll('.label-month').remove();
    Timeline.labels.selectAll('.label-month')
      .data(month_labels)
      .enter()
      .append('text')
      .attr('class', 'label label-month')
      .attr('font-size', function() {
        return Math.floor(Timeline.settings.label_padding / 3) + 'px';
      })
      .text(function(d) {
        return d.toLocaleDateString('en-us', { month: 'short' });
      })
      .attr('x', function(d, i) {
        return monthScale(i) + (monthScale(i) - monthScale(i - 1)) / 2;
      })
      .attr('y', Timeline.settings.label_padding / 2)
      .on('mouseenter', function(d) {
        if (Timeline.in_transition) { return; }

        var selected_month = moment(d);
        Timeline.items.selectAll('.item-circle')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', function(d) {
            return moment(d.date).isSame(selected_month, 'month') ? 1 : 0.1;
          });
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }

        Timeline.items.selectAll('.item-circle')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', 1);
      })
      .on('click', function(d) {
        if (Timeline.in_transition) { return; }

        // Check month data
        var month_data = Timeline.data.filter(function(e) {
          return moment(d).startOf('month') <= moment(e.date) && moment(e.date) < moment(d).endOf('month');
        });

        // Don't transition if there is no data to show
        if (!month_data.length) { return; }

        // Set selected month to the one clicked on
        Timeline.selected = { date: d };

        Timeline.in_transition = true;

        // Hide tooltip
        Timeline.hideTooltip();

        // Remove all year overview related items and labels
        Timeline.removeYearOverview();

        // Redraw the chart
        Timeline.overview = 'month';
        Timeline.drawChart();
      });

    // Add day labels
    var day_labels = d3.timeDays(moment().startOf('week'), moment().endOf('week'));
    var dayScale = d3.scaleBand()
      .rangeRound([Timeline.settings.label_padding, Timeline.settings.height])
      .domain(day_labels.map(function(d) {
        return moment(d).weekday();
      }));
    Timeline.labels.selectAll('.label-day').remove();
    Timeline.labels.selectAll('.label-day')
      .data(day_labels)
      .enter()
      .append('text')
      .attr('class', 'label label-day')
      .attr('x', Timeline.settings.label_padding / 3)
      .attr('y', function(d, i) {
        return dayScale(i) + dayScale.bandwidth() / 1.75;
      })
      .style('text-anchor', 'left')
      .attr('font-size', function() {
        return Math.floor(Timeline.settings.label_padding / 3) + 'px';
      })
      .text(function(d) {
        return moment(d).format('dddd')[0];
      })
      .on('mouseenter', function(d) {
        if (Timeline.in_transition) { return; }

        var selected_day = moment(d);
        Timeline.items.selectAll('.item-circle')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', function(d) {
            return (moment(d.date).day() === selected_day.day()) ? 1 : 0.1;
          });
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }

        Timeline.items.selectAll('.item-circle')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', 1);
      });

    // Add button to switch back to previous overview
    Timeline.drawButton();
  },


  /**
   * Draw month overview
   */
  drawMonthOverview: function() {
    // Add current overview to the history
    if (Timeline.history[Timeline.history.length - 1] !== Timeline.overview) {
      Timeline.history.push(Timeline.overview);
    }

    // Define beginning and end of the month
    var start_of_month = moment(Timeline.selected.date).startOf('month');
    var end_of_month = moment(Timeline.selected.date).endOf('month');

    // Filter data down to the selected month
    var month_data = Timeline.data.filter(function(d) {
      return start_of_month <= moment(d.date) && moment(d.date) < end_of_month;
    });
    var max_value = d3.max(month_data, function(d) {
      return d3.max(d.summary, function(d) {
        return d.value;
      });
    });

    // Define day labels and axis
    var day_labels = d3.timeDays(moment().startOf('week'), moment().endOf('week'));
    var dayScale = d3.scaleBand()
      .rangeRound([Timeline.settings.label_padding, Timeline.settings.height])
      .domain(day_labels.map(function(d) {
        return moment(d).weekday();
      }));

    // Define week labels and axis
    var week_labels = [start_of_month.clone()];
    while (start_of_month.week() !== end_of_month.week()) {
      week_labels.push(start_of_month.add(1, 'week').clone());
    }
    var weekScale = d3.scaleBand()
      .rangeRound([Timeline.settings.label_padding, Timeline.settings.width])
      .padding([0.05])
      .domain(week_labels.map(function(weekday) {
        return weekday.week();
      }));

    // Add month data items to the overview
    Timeline.items.selectAll('.item-block-month').remove();
    var item_block = Timeline.items.selectAll('.item-block-month')
      .data(month_data)
      .enter()
      .append('g')
      .attr('class', 'item item-block-month')
      .attr('width', function() {
        return (Timeline.settings.width - Timeline.settings.label_padding) / week_labels.length - Timeline.settings.gutter * 5;
      })
      .attr('height', function() {
        return Math.min(dayScale.bandwidth(), Timeline.settings.max_block_height);
      })
      .attr('transform', function(d) {
        return 'translate(' + weekScale(moment(d.date).week()) + ',' + ((dayScale(moment(d.date).weekday()) + dayScale.bandwidth() / 1.75) - 15) + ')';
      })
      .attr('total', function(d) {
        return d.total;
      })
      .attr('date', function(d) {
        return d.date;
      })
      .attr('offset', 0)
      .on('click', function(d) {
        if (Timeline.in_transition) { return; }

        // Don't transition if there is no data to show
        if (d.total === 0) { return; }

        Timeline.in_transition = true;

        // Set selected date to the one clicked on
        Timeline.selected = d;

        // Hide tooltip
        Timeline.hideTooltip();

        // Remove all month overview related items and labels
        Timeline.removeMonthOverview();

        // Redraw the chart
        Timeline.overview = 'day';
        Timeline.drawChart();
      });

    var item_width = (Timeline.settings.width - Timeline.settings.label_padding) / week_labels.length - Timeline.settings.gutter * 5;
    var itemScale = d3.scaleLinear()
      .rangeRound([0, item_width]);

    item_block.selectAll('.item-block-rect')
      .data(function(d) {
        return d.summary;
      })
      .enter()
      .append('rect')
      .attr('class', 'item item-block-rect')
      .attr('x', function(d) {
        var total = parseInt(d3.select(this.parentNode).attr('total'));
        var offset = parseInt(d3.select(this.parentNode).attr('offset'));
        itemScale.domain([0, total]);
        d3.select(this.parentNode).attr('offset', offset + itemScale(d.value));
        return offset;
      })
      .attr('width', function(d) {
        var total = parseInt(d3.select(this.parentNode).attr('total'));
        itemScale.domain([0, total]);
        return Math.max((itemScale(d.value) - Timeline.settings.item_gutter), 1)
      })
      .attr('height', function() {
        return Math.min(dayScale.bandwidth(), Timeline.settings.max_block_height);
      })
      .attr('fill', function(d) {
        var color = d3.scaleLinear()
          .range(['#ffffff', Timeline.color || '#ff4500'])
          .domain([-0.15 * max_value, max_value]);
        return color(d.value) || '#ff4500';
      })
      .style('opacity', 0)
      .on('mouseover', function(d) {
        if (Timeline.in_transition) { return; }

        // Get date from the parent node
        var date = new Date(d3.select(this.parentNode).attr('date'));

        // // Construct tooltip
        // var tooltip_html = '';
        // tooltip_html += '<div class="header"><strong>' + d.name + '</strong></div><br>';
        // tooltip_html += '<div><strong>' + (d.value ? Timeline.formatTime(d.value) : 'No time') + ' tracked</strong></div>';
        // tooltip_html += '<div>on ' + moment(date).format('dddd, MMM Do YYYY') + '</div>';

        // // Calculate tooltip position
        // var x = weekScale(moment(date).week()) + Timeline.settings.tooltip_padding;
        // while (Timeline.settings.width - x < (Timeline.settings.tooltip_width + Timeline.settings.tooltip_padding * 3)) {
        //   x -= 10;
        // }
        // var y = dayScale(moment(date).weekday()) + Timeline.settings.tooltip_padding * 2;

        // // Show tooltip
        // Timeline.tooltip.html(tooltip_html)
        //   .style('left', x + 'px')
        //   .style('top', y + 'px')
        //   .transition()
        //   .duration(Timeline.settings.transition_duration / 2)
        //   .ease(d3.easeLinear)
        //   .style('opacity', 1);
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }
        Timeline.hideTooltip();
      })
      .transition()
      .delay(function() {
        return (Math.cos(Math.PI * Math.random()) + 1) * Timeline.settings.transition_duration;
      })
      .duration(function() {
        return Timeline.settings.transition_duration;
      })
      .ease(d3.easeLinear)
      .style('opacity', 1)
      .call(function(transition, callback) {
        if (transition.empty()) {
          callback();
        }
        var n = 0;
        transition
          .each(function() {++n; })
          .on('end', function() {
            if (!--n) {
              callback.apply(this, arguments);
            }
          });
      }, function() {
        Timeline.in_transition = false;
      });

    // Add week labels
    Timeline.labels.selectAll('.label-week').remove();
    Timeline.labels.selectAll('.label-week')
      .data(week_labels)
      .enter()
      .append('text')
      .attr('class', 'label label-week')
      .attr('font-size', function() {
        return Math.floor(Timeline.settings.label_padding / 3) + 'px';
      })
      .text(function(d) {
        return 'Week ' + d.week();
      })
      .attr('x', function(d) {
        return weekScale(d.week());
      })
      .attr('y', Timeline.settings.label_padding / 2)
      .on('mouseenter', function(weekday) {
        if (Timeline.in_transition) { return; }

        Timeline.items.selectAll('.item-block-month')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', function(d) {
            return (moment(d.date).week() === weekday.week()) ? 1 : 0.1;
          });
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }

        Timeline.items.selectAll('.item-block-month')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', 1);
      })
      .on('click', function(d) {
        if (Timeline.in_transition) { return; }

        // Check week data
        var week_data = Timeline.data.filter(function(e) {
          return d.startOf('week') <= moment(e.date) && moment(e.date) < d.endOf('week');
        });

        // Don't transition if there is no data to show
        if (!week_data.length) { return; }

        Timeline.in_transition = true;

        // Set selected month to the one clicked on
        Timeline.selected = { date: d };

        // Hide tooltip
        Timeline.hideTooltip();

        // Remove all year overview related items and labels
        Timeline.removeMonthOverview();

        // Redraw the chart
        Timeline.overview = 'week';
        Timeline.drawChart();
      });

    // Add day labels
    Timeline.labels.selectAll('.label-day').remove();
    Timeline.labels.selectAll('.label-day')
      .data(day_labels)
      .enter()
      .append('text')
      .attr('class', 'label label-day')
      .attr('x', Timeline.settings.label_padding / 3)
      .attr('y', function(d, i) {
        return dayScale(i) + dayScale.bandwidth() / 1.75;
      })
      .style('text-anchor', 'left')
      .attr('font-size', function() {
        return Math.floor(Timeline.settings.label_padding / 3) + 'px';
      })
      .text(function(d) {
        return moment(d).format('dddd')[0];
      })
      .on('mouseenter', function(d) {
        if (Timeline.in_transition) { return; }

        var selected_day = moment(d);
        Timeline.items.selectAll('.item-block-month')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', function(d) {
            return (moment(d.date).day() === selected_day.day()) ? 1 : 0.1;
          });
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }

        // Timeline.items.selectAll('.item-block-month')
        //   .transition()
        //   .duration(Timeline.settings.transition_duration)
        //   .ease(d3.easeLinear)
        //   .style('opacity', 1);
      });

    // Add button to switch back to previous overview
    Timeline.drawButton();
  },


  /**
   * Draw week overview
   */
  drawWeekOverview: function() {
    // Add current overview to the history
    if (Timeline.history[Timeline.history.length - 1] !== Timeline.overview) {
      Timeline.history.push(Timeline.overview);
    }

    // Define beginning and end of the week
    var start_of_week = moment(Timeline.selected.date).startOf('week');
    var end_of_week = moment(Timeline.selected.date).endOf('week');

    // Filter data down to the selected week
    var week_data = Timeline.data.filter(function(d) {
      return start_of_week <= moment(d.date) && moment(d.date) < end_of_week;
    });
    var max_value = d3.max(week_data, function(d) {
      return d3.max(d.summary, function(d) {
        return d.value;
      });
    });

    // Define day labels and axis
    var day_labels = d3.timeDays(moment().startOf('week'), moment().endOf('week'));
    var dayScale = d3.scaleBand()
      .rangeRound([Timeline.settings.label_padding, Timeline.settings.height])
      .domain(day_labels.map(function(d) {
        return moment(d).weekday();
      }));

    // Define week labels and axis
    var week_labels = [start_of_week];
    var weekScale = d3.scaleBand()
      .rangeRound([Timeline.settings.label_padding, Timeline.settings.width])
      .padding([0.01])
      .domain(week_labels.map(function(weekday) {
        return weekday.week();
      }));

    // Add week data items to the overview
    Timeline.items.selectAll('.item-block-week').remove();
    var item_block = Timeline.items.selectAll('.item-block-week')
      .data(week_data)
      .enter()
      .append('g')
      .attr('class', 'item item-block-week')
      .attr('width', function() {
        return (Timeline.settings.width - Timeline.settings.label_padding) / week_labels.length - Timeline.settings.gutter * 5;
      })
      .attr('height', function() {
        return Math.min(dayScale.bandwidth(), Timeline.settings.max_block_height);
      })
      .attr('transform', function(d) {
        return 'translate(' + weekScale(moment(d.date).week()) + ',' + ((dayScale(moment(d.date).weekday()) + dayScale.bandwidth() / 1.75) - 15) + ')';
      })
      .attr('total', function(d) {
        return d.total;
      })
      .attr('date', function(d) {
        return d.date;
      })
      .attr('offset', 0)
      .on('click', function(d) {
        if (Timeline.in_transition) { return; }

        // Don't transition if there is no data to show
        if (d.total === 0) { return; }

        Timeline.in_transition = true;

        // Set selected date to the one clicked on
        Timeline.selected = d;

        // Hide tooltip
        Timeline.hideTooltip();

        // Remove all week overview related items and labels
        Timeline.removeWeekOverview();

        // Redraw the chart
        Timeline.overview = 'day';
        Timeline.drawChart();
      });

    var item_width = (Timeline.settings.width - Timeline.settings.label_padding) / week_labels.length - Timeline.settings.gutter * 5;
    var itemScale = d3.scaleLinear()
      .rangeRound([0, item_width]);

    item_block.selectAll('.item-block-rect')
      .data(function(d) {
        return d.summary;
      })
      .enter()
      .append('rect')
      .attr('class', 'item item-block-rect')
      .attr('x', function(d) {
        var total = parseInt(d3.select(this.parentNode).attr('total'));
        var offset = parseInt(d3.select(this.parentNode).attr('offset'));
        itemScale.domain([0, total]);
        d3.select(this.parentNode).attr('offset', offset + itemScale(d.value));
        return offset;
      })
      .attr('width', function(d) {
        var total = parseInt(d3.select(this.parentNode).attr('total'));
        itemScale.domain([0, total]);
        return Math.max((itemScale(d.value) - Timeline.settings.item_gutter), 1)
      })
      .attr('height', function() {
        return Math.min(dayScale.bandwidth(), Timeline.settings.max_block_height);
      })
      .attr('fill', function(d) {
        var color = d3.scaleLinear()
          .range(['#ffffff', Timeline.color || '#ff4500'])
          .domain([-0.15 * max_value, max_value]);
        return color(d.value) || '#ff4500';
      })
      .style('opacity', 0)
      .on('mouseover', function(d) {
        if (Timeline.in_transition) { return; }

        // Get date from the parent node
        var date = new Date(d3.select(this.parentNode).attr('date'));

        // // Construct tooltip
        // var tooltip_html = '';
        // tooltip_html += '<div class="header"><strong>' + d.name + '</strong></div><br>';
        // tooltip_html += '<div><strong>' + (d.value ? Timeline.formatTime(d.value) : 'No time') + ' tracked</strong></div>';
        // tooltip_html += '<div>on ' + moment(date).format('dddd, MMM Do YYYY') + '</div>';

        // // Calculate tooltip position
        // var total = parseInt(d3.select(this.parentNode).attr('total'));
        // itemScale.domain([0, total]);
        // var x = parseInt(d3.select(this).attr('x')) + itemScale(d.value) / 4 + Timeline.settings.tooltip_width / 4;
        // while (Timeline.settings.width - x < (Timeline.settings.tooltip_width + Timeline.settings.tooltip_padding * 3)) {
        //   x -= 10;
        // }
        // var y = dayScale(moment(date).weekday()) + Timeline.settings.tooltip_padding * 1.5;

        // // Show tooltip
        // Timeline.tooltip.html(tooltip_html)
        //   .style('left', x + 'px')
        //   .style('top', y + 'px')
        //   .transition()
        //   .duration(Timeline.settings.transition_duration / 2)
        //   .ease(d3.easeLinear)
        //   .style('opacity', 1);
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }
        Timeline.hideTooltip();
      })
      .transition()
      .delay(function() {
        return (Math.cos(Math.PI * Math.random()) + 1) * Timeline.settings.transition_duration;
      })
      .duration(function() {
        return Timeline.settings.transition_duration;
      })
      .ease(d3.easeLinear)
      .style('opacity', 1)
      .call(function(transition, callback) {
        if (transition.empty()) {
          callback();
        }
        var n = 0;
        transition
          .each(function() {++n; })
          .on('end', function() {
            if (!--n) {
              callback.apply(this, arguments);
            }
          });
      }, function() {
        Timeline.in_transition = false;
      });

    // Add week labels
    Timeline.labels.selectAll('.label-week').remove();
    Timeline.labels.selectAll('.label-week')
      .data(week_labels)
      .enter()
      .append('text')
      .attr('class', 'label label-week')
      .attr('font-size', function() {
        return Math.floor(Timeline.settings.label_padding / 3) + 'px';
      })
      .text(function(d) {
        return 'Week ' + d.week();
      })
      .attr('x', function(d) {
        return weekScale(d.week());
      })
      .attr('y', Timeline.settings.label_padding / 2)
      .on('mouseenter', function(weekday) {
        if (Timeline.in_transition) { return; }

        Timeline.items.selectAll('.item-block-week')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', function(d) {
            return (moment(d.date).week() === weekday.week()) ? 1 : 0.1;
          });
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }

        Timeline.items.selectAll('.item-block-week')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', 1);
      });

    // Add day labels
    Timeline.labels.selectAll('.label-day').remove();
    Timeline.labels.selectAll('.label-day')
      .data(day_labels)
      .enter()
      .append('text')
      .attr('class', 'label label-day')
      .attr('x', Timeline.settings.label_padding / 3)
      .attr('y', function(d, i) {
        return dayScale(i) + dayScale.bandwidth() / 1.75;
      })
      .style('text-anchor', 'left')
      .attr('font-size', function() {
        return Math.floor(Timeline.settings.label_padding / 3) + 'px';
      })
      .text(function(d) {
        return moment(d).format('dddd')[0];
      })
      .on('mouseenter', function(d) {
        if (Timeline.in_transition) { return; }

        var selected_day = moment(d);
        Timeline.items.selectAll('.item-block-week')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', function(d) {
            return (moment(d.date).day() === selected_day.day()) ? 1 : 0.1;
          });
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }

        Timeline.items.selectAll('.item-block-week')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', 1);
      });

    // Add button to switch back to previous overview
    Timeline.drawButton();
  },


  /**
   * Draw day overview
   */
  drawDayOverview: function() {
    // Add current overview to the history
    if (Timeline.history[Timeline.history.length - 1] !== Timeline.overview) {
      Timeline.history.push(Timeline.overview);
    }

    // Initialize selected date to today if it was not set
    if (!Object.keys(Timeline.selected).length) {
      Timeline.selected = Timeline.data[Timeline.data.length - 1];
    }

    var project_labels = Timeline.selected.summary.map(function(project) {
      return project.name;
    });
    var projectScale = d3.scaleBand()
      .rangeRound([Timeline.settings.label_padding, Timeline.settings.height])
      .domain(project_labels);

    var itemScale = d3.scaleTime()
      .range([Timeline.settings.label_padding * 2, Timeline.settings.width])
      .domain([moment(Timeline.selected.date).startOf('day'), moment(Timeline.selected.date).endOf('day')]);
    Timeline.items.selectAll('.item-block').remove();
    Timeline.items.selectAll('.item-block')
      .data(Timeline.selected.details)
      .enter()
      .append('rect')
      .attr('class', 'item item-block')
      .attr('x', function(d) {
        return itemScale(moment(d.date));
      })
      .attr('y', function(d) {
        return (projectScale(d.name) + projectScale.bandwidth() / 2) - 15;
      })
      .attr('width', function(d) {
        var end = itemScale(d3.timeSecond.offset(moment(d.date), d.value));
        return Math.max((end - itemScale(moment(d.date))), 1);
      })
      .attr('height', function() {
        return Math.min(projectScale.bandwidth(), Timeline.settings.max_block_height);
      })
      .attr('fill', function() {
        return Timeline.color || '#ff4500';
      })
      .style('opacity', 0)
      .on('mouseover', function(d) {
        if (Timeline.in_transition) { return; }

        // // Construct tooltip
        // var tooltip_html = '';
        // tooltip_html += '<div class="header"><strong>' + d.name + '</strong><div><br>';
        // tooltip_html += '<div><strong>' + (d.value ? Timeline.formatTime(d.value) : 'No time') + ' tracked</strong></div>';
        // tooltip_html += '<div>on ' + moment(d.date).format('dddd, MMM Do YYYY HH:mm') + '</div>';

        // // Calculate tooltip position
        // var x = d.value * 100 / (60 * 60 * 24) + itemScale(moment(d.date));
        // while (Timeline.settings.width - x < (Timeline.settings.tooltip_width + Timeline.settings.tooltip_padding * 3)) {
        //   x -= 10;
        // }
        // var y = projectScale(d.name) + projectScale.bandwidth() / 2 + Timeline.settings.tooltip_padding / 2;

        // // Show tooltip
        // Timeline.tooltip.html(tooltip_html)
        //   .style('left', x + 'px')
        //   .style('top', y + 'px')
        //   .transition()
        //   .duration(Timeline.settings.transition_duration / 2)
        //   .ease(d3.easeLinear)
        //   .style('opacity', 1);
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }
        Timeline.hideTooltip();
      })
      .on('click', function(d) {
        if (!!Timeline.handler && typeof Timeline.handler == 'function') {
          Timeline.handler(d);
        }
      })
      .transition()
      .delay(function() {
        return (Math.cos(Math.PI * Math.random()) + 1) * Timeline.settings.transition_duration;
      })
      .duration(function() {
        return Timeline.settings.transition_duration;
      })
      .ease(d3.easeLinear)
      .style('opacity', 0.5)
      .call(function(transition, callback) {
        if (transition.empty()) {
          callback();
        }
        var n = 0;
        transition
          .each(function() {++n; })
          .on('end', function() {
            if (!--n) {
              callback.apply(this, arguments);
            }
          });
      }, function() {
        Timeline.in_transition = false;
      });

    // Add time labels
    var timeLabels = d3.timeHours(
      moment(Timeline.selected.date).startOf('day'),
      moment(Timeline.selected.date).endOf('day')
    );
    var timeScale = d3.scaleTime()
      .range([Timeline.settings.label_padding * 2, Timeline.settings.width])
      .domain([0, timeLabels.length]);
    Timeline.labels.selectAll('.label-time').remove();
    Timeline.labels.selectAll('.label-time')
      .data(timeLabels)
      .enter()
      .append('text')
      .attr('class', 'label label-time')
      .attr('font-size', function() {
        return Math.floor(Timeline.settings.label_padding / 3) + 'px';
      })
      .text(function(d) {
        return moment(d).format('HH:mm');
      })
      .attr('x', function(d, i) {
        return timeScale(i);
      })
      .attr('y', Timeline.settings.label_padding / 2)
      .on('mouseenter', function(d) {
        if (Timeline.in_transition) { return; }

        var selected = itemScale(moment(d));
        Timeline.items.selectAll('.item-block')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', function(d) {
            var start = itemScale(moment(d.date));
            var end = itemScale(moment(d.date).add(d.value, 'seconds'));
            return (selected >= start && selected <= end) ? 1 : 0.1;
          });
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }

        Timeline.items.selectAll('.item-block')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', 0.5);
      });

    // Add project labels
    Timeline.labels.selectAll('.label-project').remove();
    Timeline.labels.selectAll('.label-project')
      .data(project_labels)
      .enter()
      .append('text')
      .attr('class', 'label label-project')
      .attr('x', Timeline.settings.gutter)
      .attr('y', function(d) {
        return projectScale(d) + projectScale.bandwidth() / 2;
      })
      .attr('min-height', function() {
        return projectScale.bandwidth();
      })
      .style('text-anchor', 'left')
      .attr('font-size', function() {
        return Math.floor(Timeline.settings.label_padding / 3) + 'px';
      })
      .text(function(d) {
        return d;
      })
      .each(function() {
        var obj = d3.select(this),
          text_length = obj.node().getComputedTextLength(),
          text = obj.text();
        while (text_length > (Timeline.settings.label_padding * 1.5) && text.length > 0) {
          text = text.slice(0, -1);
          obj.text(text + '...');
          text_length = obj.node().getComputedTextLength();
        }
      })
      .on('mouseenter', function(project) {
        if (Timeline.in_transition) { return; }

        Timeline.items.selectAll('.item-block')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', function(d) {
            return (d.name === project) ? 1 : 0.1;
          });
      })
      .on('mouseout', function() {
        if (Timeline.in_transition) { return; }

        Timeline.items.selectAll('.item-block')
          .transition()
          .duration(Timeline.settings.transition_duration)
          .ease(d3.easeLinear)
          .style('opacity', 0.5);
      });

    // Add button to switch back to previous overview
    Timeline.drawButton();
  },


  /**
   * Draw the button for navigation purposes
   */
  drawButton: function() {
    Timeline.buttons.selectAll('.button').remove();
    var button = Timeline.buttons.append('g')
      .attr('class', 'button button-back')
      .style('opacity', 0)
      .on('click', function() {
        if (Timeline.in_transition) { return; }

        // Set transition boolean
        Timeline.in_transition = true;

        // Clean the canvas from whichever overview type was on
        if (Timeline.overview === 'year') {
          Timeline.removeYearOverview();
        } else if (Timeline.overview === 'month') {
          Timeline.removeMonthOverview();
        } else if (Timeline.overview === 'week') {
          Timeline.removeWeekOverview();
        } else if (Timeline.overview === 'day') {
          Timeline.removeDayOverview();
        }

        // Redraw the chart
        Timeline.history.pop();
        Timeline.overview = Timeline.history.pop();
        Timeline.drawChart();
      });
    button.append('circle')
      .attr('cx', Timeline.settings.label_padding / 2.25)
      .attr('cy', Timeline.settings.label_padding / 2.5)
      .attr('r', Timeline.settings.item_size / 2);
    button.append('text')
      .attr('x', Timeline.settings.label_padding / 2.25)
      .attr('y', Timeline.settings.label_padding / 2.5)
      .attr('dy', function() {
        return Math.floor(Timeline.settings.width / 100) / 3;
      })
      .attr('font-size', function() {
        return Math.floor(Timeline.settings.label_padding / 3) + 'px';
      })
      .html('&#x2190;');
    button.transition()
      .duration(Timeline.settings.transition_duration)
      .ease(d3.easeLinear)
      .style('opacity', 1);
  },


  /**
   * Transition and remove items and labels related to global overview
   */
  removeGlobalOverview: function() {
    Timeline.items.selectAll('.item-block-year')
      .transition()
      .duration(Timeline.settings.transition_duration)
      .ease(d3.easeLinear)
      .style('opacity', 0)
      .remove();
    Timeline.labels.selectAll('.label-year').remove();
  },


  /**
   * Transition and remove items and labels related to year overview
   */
  removeYearOverview: function() {
    Timeline.items.selectAll('.item-circle')
      .transition()
      .duration(Timeline.settings.transition_duration)
      .ease(d3.easeLinear)
      .style('opacity', 0)
      .remove();
    Timeline.labels.selectAll('.label-day').remove();
    Timeline.labels.selectAll('.label-month').remove();
    Timeline.hideBackButton();
  },


  /**
   * Transition and remove items and labels related to month overview
   */
  removeMonthOverview: function() {
    Timeline.items.selectAll('.item-block-month').selectAll('.item-block-rect')
      .transition()
      .duration(Timeline.settings.transition_duration)
      .ease(d3.easeLinear)
      .style('opacity', 0)
      .attr('x', function(d, i) {
        return (i % 2 === 0) ? -Timeline.settings.width / 3 : Timeline.settings.width / 3;
      })
      .remove();
    Timeline.labels.selectAll('.label-day').remove();
    Timeline.labels.selectAll('.label-week').remove();
    Timeline.hideBackButton();
  },


  /**
   * Transition and remove items and labels related to week overview
   */
  removeWeekOverview: function() {
    Timeline.items.selectAll('.item-block-week').selectAll('.item-block-rect')
      .transition()
      .duration(Timeline.settings.transition_duration)
      .ease(d3.easeLinear)
      .style('opacity', 0)
      .attr('x', function(d, i) {
        return (i % 2 === 0) ? -Timeline.settings.width / 3 : Timeline.settings.width / 3;
      })
      .remove();
    Timeline.labels.selectAll('.label-day').remove();
    Timeline.labels.selectAll('.label-week').remove();
    Timeline.hideBackButton();
  },


  /**
   * Transition and remove items and labels related to daily overview
   */
  removeDayOverview: function() {
    Timeline.items.selectAll('.item-block')
      .transition()
      .duration(Timeline.settings.transition_duration)
      .ease(d3.easeLinear)
      .style('opacity', 0)
      .attr('x', function(d, i) {
        return (i % 2 === 0) ? -Timeline.settings.width / 3 : Timeline.settings.width / 3;
      })
      .remove();
    Timeline.labels.selectAll('.label-time').remove();
    Timeline.labels.selectAll('.label-project').remove();
    Timeline.hideBackButton();
  },


  /**
   * Helper function to hide the tooltip
   */
  hideTooltip: function() {
    Timeline.tooltip.transition()
      .duration(Timeline.settings.transition_duration / 2)
      .ease(d3.easeLinear)
      .style('opacity', 0);
  },


  /**
   * Helper function to hide the back button
   */
  hideBackButton: function() {
    Timeline.buttons.selectAll('.button')
      .transition()
      .duration(Timeline.settings.transition_duration)
      .ease(d3.easeLinear)
      .style('opacity', 0)
      .remove();
  },


  /**
   * Helper function to convert seconds to a human readable format
   * @param seconds Integer
   */
  formatTime: function(seconds) {
    var hours = Math.floor(seconds / 3600);
    var minutes = Math.floor((seconds - (hours * 3600)) / 60);
    var time = '';
    if (hours > 0) {
      time += hours === 1 ? '1 hour ' : hours + ' hours ';
    }
    if (minutes > 0) {
      time += minutes === 1 ? '1 minute' : minutes + ' minutes';
    }
    if (hours === 0 && minutes === 0) {
      time = Math.round(seconds) + ' seconds';
    }
    return time;
  },

};