/**
 * Result class for data-arc
 * requires jQuery 2.0+
 */

/**
 * Result handler
 * @param {String} container
 * @param {Object} settings
 */
var Results = function(container, settings) {

  /**
   * @namespace								  - Settings
   * @property {object}  settings               - The default values for settings.
   */
  this.settings = {

  }

  /**
   * jQuery Object that contains all generated elements
   * @type {Object}
   * @const
   */
  this.container = $(container);
  // check if html data exists
  var existing = this.container.find('.result-area');
  existing.fadeOut('slow', function() { $(this).empty(); });

  /**
   * jQuery Object that contains the loader element
   * @type {Object}
   * @const
   */
  this.loader = $('<div>', { 'class': 'result-loader col-sm-12 text-center' });
  this.loader.append('<h1><i class="fa fa-cog fa-spin fa-2x"></i></h1>');
  this.container.empty();
  this.container.append(this.loader);

  /**
   * Build the UI
   **/
  this.init();
}
Results.prototype = {
  /**
   * Initialize result UI
   * @return {void}
   */
  init: function() {
    this.matched = new ResultGroup('matched', this.container);
  }
}


/**
 * ResultGroup - Defines each result row behaviors and elements
 * @param {String} type of result row ["matched", "relevant", "context"]
 * @param {Object} jQuery container object
 */
var ResultGroup = function(type, container) {

  // modify title string to upperCase
  this.type = type.charAt(0).toUpperCase() + type.slice(1).toLowerCase();

  this.container = container;

  /**
   * @property {Object}	- possibly pre-existing jQuery element
   **/
  this.loader = container.find('.result-loader');

  /**
   * @property {Object}	- Summary stats by category / set in getSummaryStatsByCategories Method
   **/
  this.data = {}

  /**
   * @property {Array}	- List of categories present
   **/
  this.categories = [];

  /**
   * @property {Object|Boolean} - ResultDetail() object
   **/
  this.detail = false;

  this.init();
}

ResultGroup.prototype = {
  init: function() {

    // get the data
    this.getSummaryStatsByCategories();

    // build the row
    var _this = this;
    setTimeout(function() {
      _this.constructRow();
    }, 2000);


  },

  /**
   * Constructs a row of elements and category containers
   * @return {void}
   */
  constructRow: function() {

    var _this = this;

    this.resultArea = $('<div>', { 'class': 'result-area', 'style': 'display:none;' });

    this.container.append(this.resultArea);

    // append header
    this.header = $('<div>', { 'class': 'row result-row-header' });
    this.header.append('<div class="col-sm-12">' + this.type + ' Results</div>');

    this.resultArea.append(this.header);

    this.row = $('<div>', { 'class': 'result-row' });

    this.resultArea.append(this.row);

    // iterate through each category
    for (var i = 0; i < this.categories.length; i++) {

      this.row.append(_this.constructElements(this.categories[i]));

    }

    // remove the spinner
    this.loader.remove();

    // fade in the result area
    this.resultArea.fadeIn();

  },
  /**
   * Constructs all internal elements with styles and behaviors
   * @return {void}
   */
  constructElements: function(category) {

    var _this = this;

    var el = $('<div>', { 'class': 'result-category text-center' });

    var data = this.data[category];
    // determine widths based on category count
    var categoryWidth = parseInt(100 / this.categories.length);

    var sourcesWidth = parseInt(100 / this.data[category].sources.length);

    el.css('width', categoryWidth + '%');

    el.append('<div class="result-category-heading">' + category.charAt(0).toUpperCase() + category.slice(1).toLowerCase() + '</div>');

    el.append('<div class="result-category-heading-fillet"></div>');

    el.append('<div class="result-category-count">' + this.data[category].count + '</div>');

    if (this.data.count > 0) {

      el.append('<div class="result-category-sources-heading">Sources</div>');

      el.append('<div class="result-category-sources-heading-fillet"></div>');

      var sourcesUl = $('<ul class="result-category-sources-list">');

      for (var i = 0; i < this.data[category].sources.length; i++) {

        sourcesUl.append('<li class="result-category-sources-item" style="width:' +
          sourcesWidth + '%"><div class="sources-item-title"><strong>' +
          this.data[category].sources[i] + '</strong></div><div class="sources-item-count">' +
          this.data[category].sources_count[i] + '<div></li>');

      }

      el.append(sourcesUl);

      // Actions area
      var actionsEl = $('<div>', { 'class': 'result-category-actions' });




      // Calls the Detail() class constructor
      var viewDetail = $('<button>', { 'class': 'btn btn-block btn-default result-category-detail-btn' }).text('View');
      // set click event
      viewDetail.click(function() {

        _this.detail = new ResultDetail(category, _this);

      });

      actionsEl.append(viewDetail);

      el.append(actionsEl);

    }

    return el;

  },

  /**
   * Requests and builds the summary data object
   * @return {void}
   */
  getSummaryStatsByCategories: function() {

    // Fetch the data

    if (this.type == 'Context' || this.type == 'Relevant') {

      // different end point expected
      // Topic ids will be required

      /** Matched is the default **/
    } else {

      this.data = typeof Search.facets["category"] == 'undefined' ? {} : Search.facets["category"];

    }

    // Format the statistics

    count = 0;

    for (var category in this.data) {

      this.categories.push(category);

      for (var source in this.data[category].source) {

        if (typeof this.data[category].sources_count == 'undefined') {

          this.data[category].sources = [];

          this.data[category].sources_count = [];

        }

        this.data[category].sources.push(source);

        this.data[category].sources_count.push(this.data[category].source[source]);

      }

      this.data[category].count = this.data[category].sources_count.reduce((a, b) => a + b, 0);

      count += this.data[category].count;

    }

    this.data.count = count;

    if (count == 0) {
      this.categories.push("No Results");
      this.data["No Results"] = {};
      this.data["No Results"].sources_count = [];
      this.data["No Results"].sources = [];
      this.data["No Results"].count = 0;
    }

  }
}




var ResultDetail = function(category, parent) {

  this.parent = parent;

  this.category = category;

  this.sources = [];

  this.init();

}

ResultDetail.prototype = {

  init: function() {

    this.fetchData();

    this.prepareElements();

    this.drawContent();

  },

  fetchData: function() {

    this.features = Search.getResultsByCategory(this.category);

    this.data = {};

    for (var i = 0; i < this.features.length; i++) {

      // compile expected sources
      if (typeof this.data[this.features[i].properties.source] == 'undefined') {

        this.data[this.features[i].properties.source] = {};
        this.data[this.features[i].properties.source].tabledata = [];
        this.data[this.features[i].properties.source].features = [];

      }

      // push the new feature object for datatables
      this.data[this.features[i].properties.source].tabledata.push({
        id: (typeof this.features[i].properties.id == 'undefined' ? "" : this.features[i].properties.id),
        date: (typeof this.features[i].properties.date == 'undefined' ? "" : this.features[i].properties.date),
        title: (typeof this.features[i].properties.title == 'undefined' ? "" : this.features[i].properties.title)
      });

      // push the full feature object for display
      this.data[this.features[i].properties.source].features.push(this.features[i]);

    }
    console.log("Still need source inside each feature.properties");
    console.log(this.data);
  },

  prepareElements: function() {

    this.overlay = $('<div>', { 'class': 'result-detail-overlay' });

    $('.result-detail-overlay').remove();

    $('body').append(this.overlay);

    this.container = $('<div>', { 'class': 'result-detail-container' });

    this.overlay.append(this.container);

    this.overlay.fadeIn();

  },

  drawContent: function() {

    var _this = this;

    var header = $('<h5>', { 'style': 'padding:10px;' });
    header.text(this.parent.type + " Results: " + this.category);
    this.container.append(header);

    var closeBtn = $('<button>', { 'class': 'btn btn-default pull-right' });
    closeBtn.append('<i class="fa fa-times"></i>');
    closeBtn.click(function() {

      _this.destroy();

    });
    header.append(closeBtn);

    var tabContainer = $('<ul>', { 'class': 'nav nav-tabs' });
    this.container.append(tabContainer);

    var sourceContainer = $('<div>', { 'class': 'tab-content' });
    this.container.append(sourceContainer);

    var i = 0;

    for (var source in this.data) {

      var tab = $('<li>', { 'class': 'nav-item' });
      tab.append('<a href="#result-detail-source-' + source.replace(/ /g, "_") + '" class="nav-link' + (i == 0 ? ' active' : '') + '">' + source + '</a>');
      tabContainer.append(tab);

      var contentContainer = $('<div>', { 'class': 'tab-pane' + (i == 0 ? ' active' : ''), 'id': 'result-detail-source-' + source.replace(/ /g, "_"), 'aria-expanded': (i == 0 ? 'true' : 'false') });
      sourceContainer.append(contentContainer);

      tab.find('a').click(function(e) {
        e.preventDefault();
        if (!$(this).hasClass('active')) {
          $(this).parent().parent().next('.tab-content').children('.tab-pane').each(function() {
            if ($(this).hasClass('active')) {
              $(this).removeClass('active');
            }
          });
          $(this).parent().parent().children('.nav-item').each(function() {
            if ($(this).find('a').hasClass('active')) {
              $(this).find('a').removeClass('active');
            }
          });
          $(this).addClass('active');
          var target = $(this).attr('href');
          $(target).addClass('active');
        }

      });

      this.sources.push({ source: new ResultSource(source, this.data[source], contentContainer) });

      i++;

    }

  },

  destroy: function() {

    var _this = this;

    this.overlay.fadeOut('slow', function() {

      $(this).empty();

      $(this).remove();

      _this.parent.detail = false;

    });

  }

}



/**
 * Source class for data-arc data source types
 */

var ResultSource = function(source, data, container) {

  this.source = source;

  this.data = data;

  this.root = container;

  this.container = $('<div>', { 'class': 'row result-detail-content' });
  this.root.append(this.container);

  this.init();

};
ResultSource.prototype = {

  init: function() {

    this.drawContainers();

  },

  drawContainers: function() {

    var _this = this;

    this.tableContainer = $('<div>', { 'class': 'col-sm-5 result-detail-table' });
    this.container.append(this.tableContainer);

    this.featureContainer = $('<div>', {'class':'col-sm-7'});
    this.container.append(this.featureContainer);

    this.table = $('<table>', { 'id': this.source.replace(/ /g, "_") + "_table_detail", 'class': 'table table-striped table-bordered table-sm', 'style': 'width:100%;', 'cellspacing': '0' });
    this.table.append('<thead><tr><th>View</th><th>Date</th><th>Title</th></tr></thead>');
    this.tableContainer.append(this.table);

    // init the datatable
    this.table.DataTable({
      "data": _this.data.tabledata,
      "lengthChange": false,
      "dom": '<<"search"f>i<t>p>',
      "language": {
        "paginate": {
          "previous": "<",
          "next": ">"
        },
        "search": "_INPUT_",
        "searchPlaceholder": "Filter...",
        "infoEmpty": "No features for this source",
        "info": "(_START_-_END_)/_TOTAL_",
        "lengthMenu": "",
      },
      "columns": [
        { "data": "id" },
        { "data": "date" },
        { "data": "title" }
      ],
      "columnDefs": [{
        "targets": 0,
        "searchable": false,
        "render": function(id, type, row, meta) {

          return _this.createBtn(id);

        }
      }],
    });

    this.activateBtns();
  },

  createBtn: function(id) {

    return '<button class="btn btn-sm btn-default result-detail-view-feature" id="' + id + '">View</button>';

  },

  activateBtns: function() {

    var _this = this;

    // btn event MUST be added to the table, not the btn
    // the class of the btn is simply passed as second param
    // otherwise the paging does not work
    $('#' + this.source.replace(/ /g, "_") + "_table_detail").on('click', '.result-detail-view-feature', function() {

      _this.drawFeature($(this).attr('id'));

    });

  },

  drawFeature: function(id) {
  	var _this = this;
    _this.content = null;
    Search.getDetailsById(id, function(data) {
      console.log(data);
      var feature = data.results.features[0];
      var handlebarHandler = $("#results-template-" + feature.properties.schema_id).length ? $("#results-template-" + feature.properties.schema_id) : $("#results-template-generic");
      // console.log(feature.properties);
      //console.log(handlebarHandler.html());
      var template = Handlebars.compile(handlebarHandler.html());
      _this.content = template(feature.properties);
      _this.featureContainer.empty().append(_this.content);

      _this.processTableType();
    });
  },

  processTableType: function() {

    // check table type
    var $chart = this.featureContainer.find('table.type-chart');
    if( $chart.length ){
      console.log("This has a chart type. Capturing data...");

      function getRandomRgb(a) {
          var num = Math.round(0xffffff * Math.random());
          var r = num >> 16;
          var g = num >> 8 & 255;
          var b = num & 255;
          return r + ', ' + g + ', ' + b;
      }
      var heading = this.featureContainer.find('h3').text();
      var data = {
        labels: [],
        datasets: [
          {
            data: [],
            backgroundColor: [],
            borderColor: [],
            borderWidth: 1
          }
        ],
      }
      var noData = [];
      var i = 0;
      $chart.find('tr').map(function() {
          var label = $("td:eq(0)", this).map(function() {
            return this.innerHTML.split('_').join(' ');
          }).get()[0];
          var val = $("td:eq(1)", this).map(function() {
            // parse the numerical values
            return parseFloat(this.innerHTML);
          }).get()[0];

          if(val && val > 0) {
            data.labels.push(label);
            data.datasets[0].data.push(val);
            var color = getRandomRgb();
            data.datasets[0].backgroundColor.push('rgba('+color+', 0.2)');
            data.datasets[0].borderColor.push('rgba('+color+', 1)');
          } else {
            noData.push(label);
          }
      });
      console.log(data);
      console.log(noData);
      this.createChart(heading, data, noData);
    }

    this.featureContainer.show();

  },

  createChart: function(title, data, noData) {

    this.canvas = $('<canvas>', {id: "results-chart", width: Math.floor(this.featureContainer.width() * 0.5), height: Math.floor(this.featureContainer.height() * 0.5)});
    this.featureContainer.empty();
    this.featureContainer.append(this.canvas);

    this.chart = new Chart(this.canvas, {
      type: 'bar',
      data: data,
      options: {
          title: {
            display: true,
            text: title,
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
              }],
          },
          legend: {
            labels: {
              generateLabels: {

              }
            }
          }
      }
    });
  },

  mortuary: function() {

  },

  PMS: function() {

  },

  SEAD: function() {

  },

  Sagas: function() {

  },

  ISLEIF: function() {

  },

  tdar: function() {

  },

  nabone: function() {

  },
}