//Vue.http.options.emulateJSON = true; // send as 


// var Vue = require('vue');
  Vue.config.errorHandler = function (err, vm) {
      console.log(err);
      console.log(vm);
  }

// el: "#searchPart",
 Vue.component('spart', {
      template: "#spart-template",
      props: ['fields', "part"],
      methods: {}
  });
  
var Hack = new Vue({
  el: '#schema',

  data: {
    schemum: { name: ''},
    schema: [],
    fields: [],
    uniqueValues: [],
    indicatorName: "",
    results: undefined,
    queryParts: [{}],
    selectedSchema: undefined,
    selectedField: undefined
  },
  computed: {
  },
  watch: {
      'selectedSchema' : function(val, oldVal) {
          this.selectSchema();
      },
      'selectedField' : function(val, oldVal) {
          this.selectField();
      }
  },
  mounted: function () {
    this.fetchSchema();
  },

  methods: {
      addPart: function() {
          this.queryParts.push({});
      },
      removePart: function(idx) {
        this.queryParts.splice(idx,1);  
      },
      fetchSchema: function () {
          var events = [];
          console.log("fetch schema");
          this.$http.get('/api/schema/list')
            .then(function (schema) {
                var list = new Array();
                schema.body.forEach(function(s) {
                    list.push({name: s});
                });
                console.log(list);
              Vue.set(this, 'schema', list);
            })
            .catch(function (err) {
                console.log(err);
              console.err(err);
            });
        },
        selectSchema: function () {
            var s = this.schema[this.selectedSchema];
            console.log("fetch fields for "+ s.name);
            this.$http.get('/api/schema/listFields',{params: {'schema': s.name}})
              .then(function (request) {
                  console.log(JSON.stringify(request.body));
                Vue.set(this, 'fields', request.body);
              })
              .catch(function (err) {
                  console.log(err);
                console.err(err);
              });
          },
          selectField: function () {
              var s = this.schema[this.selectedSchema];
              var f = this.fields[this.selectedField];
              console.log("fetch unique values for "+ f.name);
              return selectFieldByName(name);
            },
            getFieldIndex: function(name) {
                for (var i=0; i< this.fields.length; i++){
                    if (this.fields[i].name===name) {
                        return i;
                    }
                }
                return -1;
            },
            getHtmlFieldType(name){
                if (name == undefined || name == '') {
                    return "text";
                }
                var f = this.fields[this.getFieldIndex(name)];
                if (f.type == undefined) {
                    return 'number';
                }
                if (f.type === 'NUMBER'){
                    return "number";
                } else if (f.type == 'DATE') {
                    return 'date';
                }
                return "text";
            },
          selectFieldByName(name) {
                var s = this.schema[this.selectedSchema];
                this.$http.get('/api/schema/listDistinctValues',{params: {'schema': s.name, "field":name}})
                .then(function (request) {
                    console.log(JSON.stringify(request.body));
                  Vue.set(this, 'uniqueValues', request.body);
                })
                .catch(function (err) {
                    console.log(err);
                  console.err(err);
                });
          },
          updateTest() {
              //FIXME: hack, replace with component and proper binding?
            this.$forceUpdate();  
          },
          createQuery() {
              var query = {"conditions":this.queryParts, "operator": "AND"};
              var s = this.schema[this.selectedSchema];
              query.schema = s.name;
              return query;
          },
          runQuery() {
              var query = this.createQuery();
              console.log(JSON.stringify(query));
              this.$http.post('/api/query/datastore' , JSON.stringify(query), {emulateJSON:true,
                  headers: {
                      'Content-Type': 'application/json'
                  }})
              .then(function (request) {
                  console.log(JSON.stringify(request.body));
                  Vue.set(this,"results",request.body);
              })
              .catch(function (err) {
                  console.log(err);
                console.err(err);
              });
          },
          saveIndicator() {
              var indicator = {name: this.indicatorName,
                      query: this.createQuery()};
              this.$http.post('/api/indicator/save' , JSON.stringify(indicator), {emulateJSON:true,
                  headers: {
                      'Content-Type': 'application/json'
                  }})
              .then(function (request) {
                  console.log(JSON.stringify(request.body));
              })
              .catch(function (err) {
                  console.log(err);
                console.err(err);
              });

          },
    getQuery: function() {
        console.log(this.queryParts);
        return this.queryParts;
    }
  }
});
