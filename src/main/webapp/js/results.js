'use strict';

/**
 * Results handler class for data-arc Requires ECMA6, Lodash, jQuery
 */

/**
 * Result handler
 *
 * @param {Object}
 *            settings
 */
class ResultsHandler {
  constructor(settings) {
    this.settings = {
      container: '#results-section',
      types: ['matched', 'related', 'contextual'],
      typemap: {
        'matched': '0',
        'related': '1',
        'contextual': '2'
      }
    }
    // set the container
    this.container = $(this.settings.container);
  }

  /**
     * Refresh UI
     *
     * @return {void}
     */
  refresh() {
    this.loader = Loader.large;
    this.filters = Search.values;
    this.results = Search.results;
    this.schema = SCHEMA;

    // purge existing dom elements
    $(this.settings.container).empty();

    // loop through the types
    for (let type of this.settings.types) {
      // make sure we have results before creating the dom
      if (this.results[type].count > 0) {
        // prepare the dom
        this.prepare(type);

        // query for results and update the dom
        this.update(type, this.results[type].facets);
      }
    }
  }

  wait() {
    this.loader = Loader.large;
    $(this.settings.container).append(this.loader);
  }

  prepare(type) {
    // create new placeholders
    var dom = $('<div>', {'class': 'call-to-action bg-dark', 'id': 'results-' + type});
    $(this.settings.container).append(dom);
    var container = $('<div>', {'class': 'container text-center'});
    container.append('<h2>' + _.startCase(type) + ' Results</h2><hr>');
    container.append('<div class="card-deck results-container"></div>');
    dom.append(container);
  }

  update(type, facets) {
    // loop through the categories and build the dom
    for (let category in facets.category) {
      if (!this.results[type].facets.category.hasOwnProperty(category)) continue;
      this.appendCategory(type, category.toLowerCase(), this.results[type].facets.category[category].count.toLocaleString());

      // loop through the sources and build the dom
      for (let source in this.results[type].facets.category[category].source) {
        if (!this.results[type].facets.category[category].source.hasOwnProperty(source)) continue;
        var source_name = (this.schema.hasOwnProperty(source) ? this.schema[source] : source);
        var source_count = this.results[type].facets.category[category].source[source].toLocaleString();
        this.appendSource(type, category.toLowerCase(), source_name, source_count);
      }
    }

    // make our view buttons clickable
    $(this.settings.container + ' .btn').unbind('click').click((e) => {
      this.details($(e.currentTarget).data('type'), $(e.currentTarget).data('category'));
    });
  }

  appendCategory(type, name, value) {
    var id = 'results-' + type + '-' + name;
    var card = $('<div>', { 'class': 'card text-center results-category results-' + name, 'id': id });
    $('#results-' + type + ' .results-container').append(card);
    var body = $('<div>', { 'class': 'card-body results-' + name });
    card.append(body);
    body.append('<h4 class="card-title typetip">' + name + '</h4><hr>');
    body.append('<p class="card-text">' + value + '</p>');
    body.append('<h5>Sources</h5><hr>');
    body.append('<ul class="list-group"></ul>');
    var foot = $('<div>', { 'class': 'card-footer'});
    card.append(foot);
    foot.append('<button class="btn btn-block btn-primary" data-type="' + type + '" data-category="' + name + '">View</button></div>');
  }

  appendSource(type, parent, name, value) {
    var id = 'results-'+type+'-'+parent+'-'+name;
    var dom = $('<li>', { 'class': 'list-group-item d-flex justify-content-between align-items-center text-left results-source', 'id': id });
    dom.append(name + '<span class="badge badge-dark">' + value + '</span>');
    $('#results-'+type+'-'+parent+' ul').append(dom);
  }

  details(type, category) {
    // prepare modal
    var details = $('#results-details');
    details.find('.modal-header').html('<h5 class="modal-title">' + type + ' Results: ' + category + '</h5><button type="button" class="close text-light" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>');
    details.find('.modal-body').html(this.loader);
    details.modal('show');

    // build containers
    var tabs = $('<div>', {'class': 'nav nav-tabs', 'id': 'results-source-tab', 'role': 'tablist'});
    var content = $('<div>', {'class': 'tab-content', 'id': 'results-source-tabContent'});

    // loop through the sources and build the dom
    var catref = category.toUpperCase();
    var first = true;
    for (let srcref in this.results[type].facets.category[catref].source) {
      if (!this.results[type].facets.category[catref].source.hasOwnProperty(srcref)) continue;
      var source = {};
      source.type = type;
      source.category = category;
      source.id = srcref;
      source.name = (this.schema.hasOwnProperty(srcref) ? this.schema[srcref] : srcref);
      source.count = this.results[type].facets.category[catref].source[srcref];
      source.results = Search.getResultsByTypeSource(type, srcref);

      // create the source tab link
      this.appendTabLink(tabs, source, first);

      // create the content of the tab
      this.appendTabData(content, source, first);

      // done with first
      first = false;
    }

    // update the modal contents
    var nav = $('<nav>').append(tabs);
    details.find('.modal-body').empty().append(nav, content);

    // adjust the modal height
    details.modal('handleUpdate');
  }

  appendTabLink(parent, data, active) {
    var link = $('<a>', {
      'id': data.id + '-tab',
      'class': 'nav-item nav-link' + (active ? ' active' : ''),
      'data-toggle': 'tab',
      'href': '#' + data.id + '-content',
      'role': 'tab',
      'aria-controls': data.id + '-content',
      'aria-selected': active.toString()
    });
    link.append(data.name + ' <span class="badge badge-dark">' + data.count + '</span>');
    link.on('click', (e) => {
      var description = $('#' + data.id + '_bio').text();
      $(e.target.hash + ' .source-details').text(description);
    });
    parent.append(link);
  }

  appendTabData(parent, data, active) {
    var description = $('#' + data.id + '_bio').text();
    var content = $('<div>', {
      'id': data.id + '-content',
      'class': 'tab-pane fade' + (active ? ' show active' : ''),
      'role': 'tabpanel',
      'aria-labelledby': data.id + '-tab'
    });
    content.append('<div class="row"><div class="col-4 source-table"></div><div class="col-8 source-details"><p>' + description + '</p></div></div>');
    this.appendDataTable(content.find('.source-table'), data);
    parent.append(content);
  }

  appendDataTable(parent, data) {
    var table = $('<table>', {
      'id': 'results-details-table',
      'class': 'table table-sm table-striped table-bordered table-hover',
      'style': 'width: 100%',
      'cellspacing': '0'
    });
    table.append('<thead class="thead-light"><tr><th>View</th><th>Date</th><th>Title</th></tr></thead>');
    parent.append(table);

    // init the datatable
    table.DataTable({
      'data': data.results,
      'lengthChange': false,
      'dom': '<<"badge badge-light pull-left"i><"pull-right"f><t>p>',
      'language': {
        'paginate': {
          'previous': '',
          'next': ''
        },
        'search': '_INPUT_',
        'searchPlaceholder': 'Filter...',
        'infoEmpty': 'No features for this source',
        'info': '_START_ - _END_ of _TOTAL_',
        'lengthMenu': ''
      },
      'columns': [
        { 'data': 'properties.id' },
        { 'defaultContent': '' },
        { 'defaultContent': 'not yet implemented' }
      ],
      'columnDefs': [{
        'targets': 0,
        'searchable': false,
        'render': (id, type, row, meta) => {
          var btn = $('<button>', { 'class': 'btn btn-sm btn-default results-view', 'id': id }).text('View');
          return btn.prop('outerHTML');
        }
      }]
    });

    // init the view buttons
    table.find('tbody').on('click', '.results-view', (e) => {
      this.showDetails(e.currentTarget.id);
    });
  }

  showDetails(id) {
    var details = $('.source-details');
    details.html(this.loader);
    Search.getDetailsById(id, (data) => {
      var feature = data.results.features[0];
      var handlebarHandler = $("#results-template-" + feature.properties.schema_id).length ? $("#results-template-" + feature.properties.schema_id) : $("#results-template-generic");
      var template = Handlebars.compile(handlebarHandler.html());
      details.html(template(feature.properties));
      this.analyzeDetails(details);
    });
  }

  analyzeDetails(container) {
    var chart = container.find('table.type-chart');
    if (chart.length) {
      container.html(this.loader);
      function getRandomRgb(a) {
        var num = Math.round(0xffffff * Math.random());
        var r = num >> 16;
        var g = num >> 8 & 255;
        var b = num & 255;
        return r + ', ' + g + ', ' + b;
      }
      var title = container.find('h3').text();
      var data = {
        labels: [],
        datasets: [{
          data: [],
          backgroundColor: [],
          borderColor: [],
          borderWidth: 1
        }],
      }
      var noData = [];
      var i = 0;
      chart.find('tbody tr').map((i, e) => {
        var label = $("td:eq(0)", e).text().split('_').join(' ');
        var val = parseFloat($("td:eq(1)", e).text());
        if (val && val > 0) {
          data.labels.push(label);
          data.datasets[0].data.push(val);
          var color = getRandomRgb();
          data.datasets[0].backgroundColor.push('rgba(' + color + ', 0.2)');
          data.datasets[0].borderColor.push('rgba(' + color + ', 1)');
        }
        else {
          noData.push(label);
        }
      });
      this.createChart(container, title, data, noData);
    }
  }

  createChart(container, title, data, noData) {
    var canvas = $('<canvas>', {
      id: 'source-chart',
      width: Math.floor(container.width()),
      height: Math.floor(container.width() * 0.75)
    });
    container.empty().append(canvas);
    var chart = new Chart('source-chart', {
      type: 'bar',
      responsive: true,
      animation: true,
      data: data,
      options: {
        title: {
          display: true,
          text: title
        },
        legend: {
          display: false
        },
        scales: {
          yAxes: [{
            ticks: {
              beginAtZero:true
            }
          }],
          xAxes: [{
            ticks: {
              autoSkip: false
            }
          }]
        }
      }
    });
  }

}