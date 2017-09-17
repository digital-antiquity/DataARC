
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
	this.container.empty();

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

	this.type = type.charAt(0).toUpperCase() + type.slice(1);

	this.container = container;

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

		this.constructRow();

	},
	/**
	 * Constructs a row of elements and category containers
	 * @return {void}
	 */
	constructRow: function() {

		var _this = this;

		// append header
		this.header = $('<div>', {'class': 'row result-row-header'});
			this.header.append('<div class="col-sm-12">'+this.type + ' Results</div>');

		this.container.append(this.header);

		this.row = $('<div>', {'class': 'result-row'});

		this.container.append(this.row);

		for (var i = 0; i < this.categories.length; i++) {

			this.row.append( _this.constructElements(this.categories[i]) );

		}

	},
	/**
	 * Constructs all internal elements with styles and behaviors
	 * @return {void}
	 */
	constructElements: function(category) {

		var _this = this;

		var el = $('<div>', {'class': 'result-category text-center'});

		var data = this.data[category];

		var categoryWidth = parseInt(100 / this.categories.length);

		var sourcesWidth = parseInt(100 / this.data[category].sources.length);

		el.css('width', categoryWidth+'%');

		el.append('<div class="result-category-heading">'+category+'</div>');

		el.append('<div class="result-category-heading-fillet"></div>');

		el.append('<div class="result-category-count">'+this.data[category].count+'</div>');

		el.append('<div class="result-category-sources-heading">Sources Found</div>');

		el.append('<div class="result-category-sources-heading-fillet"></div>');

		var sourcesUl = $('<ul class="result-category-sources-list">');

			for (var i = 0; i < this.data[category].sources.length; i++) {

				sourcesUl.append('<li class="result-category-sources-item" style="width:'
					+sourcesWidth+'%"><div class="sources-item-title"><strong>'
					+this.data[category].sources[i]+'</strong></div><div class="sources-item-count">'
					+this.data[category].sources_count[i]+'<div></li>');

			}

		el.append(sourcesUl);

		// Actions area
		var actionsEl = $('<div>', {'class': 'result-category-actions'});
		// Calls the Detail() class constructor
		var viewDetail = $('<button>', {'class':'btn btn-block btn-default result-category-detail-btn'}).text('View Detail');

		viewDetail.click(function() {
			console.log(_this);
		});

		actionsEl.append(viewDetail);

		el.append(actionsEl);

		return el;

	},

	/**
	 * Requests and builds the summary data object
	 * @return {void}
	 */
	getSummaryStatsByCategories: function() {

		// Fetch the data

		if(this.type == 'Context' || this.type == 'Relevant') {

			// different end point expected
			// Topic ids will be required

		/** Matched is the default **/
		} else {
			this.data = Search.facets["category"];

		}

		// Format the statistics

		count = 0;

		for ( var category in this.data ) {

			this.categories.push(category);

			for ( var source in this.data[category].source ) {

				if( typeof this.data[category].sources_count == 'undefined' ) {

					this.data[category].sources = [];

					this.data[category].sources_count = [];

				}

				this.data[category].sources.push(source);

				this.data[category].sources_count.push(this.data[category].source[source]);

			}

			this.data[category].count = this.data[category].sources_count.reduce((a, b) => a + b, 0);

			count+=this.data[category].count;

		}

		this.data.count = count;

	}
}

var ResultDetail = function(category) {

	this.features = Search.getFeaturesByCategory(category);

}

/**
 * Source class for data-arc data source types
 */
var Source = {

	General: {

	},

	PMS: {

	},

	SEAD: {

	},

	Sagas: {

	},

	ISLEIF: {

	},

	tdar: {

	}
}




