//Vue.use(VeeValidate); // good to go.
//Vue.http.options.emulateJSON = true; // send as 


// var Vue = require('vue');
  Vue.config.errorHandler = function (err, vm) {
      console.log(err);
      console.log(vm);
  }


 Vue.component('spart', {
      template: "#spart-template",
      props: ['fields', "part","rowindex","parts"],
      computed: {
      },
      methods: {
          getLimits :  function() {
              var r = [{'text':'Equals', 'value': 'EQUALS'},{'text':'Does not Equal', 'value': 'DOES_NOT_EQUAL'}];
              if (this.getHtmlFieldType(this.part.fieldName) != 'text') {
                  r.push({'text':'Greater Than','value':'GREATER_THAN'});
                  r.push({'text':'Less Than','value':'LESS_THAN'});
              } else {
                  r.push({'text':'Contains','value':'CONTAINS'});
              }
              return r;
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
          getFieldIndex: function(name) {
              for (var i=0; i< this.fields.length; i++){
                  if (this.fields[i].name===name) {
                      return i;
                  }
              }
              return -1;
          },
          addPart: function() {
              this.parts.push({});
          },
          removePart: function(idx) {
            this.parts.splice(idx,1);  
          },
          updateTest() {
              // FIXME: hack, replace with component and proper binding?
            this.$forceUpdate();  
          },
      }
  });
  
var Hack = new Vue({
  el: '#schema',

  data: {
    schemum: { name: ''},
    schema: [],
    fields: [],
    uniqueValues: [],
    indicators: [] ,
    topics: [],
    results: undefined,
    conditions: [{}],
    currentSchema: undefined,
    currentField: undefined,
    currentIndicator: undefined
  },
  computed: {
      cannotSubmit: function() {
          if (this.currentIndicator != undefined) {
              var ind = this.indicators[this.currentIndicator];
              console.log(ind);
              if (ind.name != '') {
                  return false;
              }
          }
          return true;
      },
      cannotSearch: function() {
          return false;
          if (this.currentIndicator != undefined) {
              var ind = this.indicators[this.currentIndicator];
              console.log(ind.query.conditions.length);
              if (ind.query.conditions.length == 0) {
                  return true;
              }
              var disabled = false;
              ind.query.conditions.forEach(function(cond){
                  console.log(cond);
                  if (cond.fieldName == undefined || cond.fieldName == '') {
                      console.log('invalid field name:' + cond);
                      disabled = true;
                      return;
                  }
                  if (cond.type == undefined || cond.type == '') {
                      console.log('invalid field type:' + cond);
                      disabled = true;
                      return;
                  }
              });
              if (disabled) {
                  return true;
              }
          }
          return false;
      }

  },
  watch: {
      'currentSchema' : function(val, oldVal) {
          this.selectSchema();
      },
      'currentField' : function(val, oldVal) {
          this.selectField();
      },
      'currentIndicator' : function(val, oldVal) {
          if (val === "new") {
              console.log("setup new indicator");
              var indicator = {name:'',query: {conditions:[{}], operator:'AND', schema: this.schema[this.currentSchema].name}};
              this.indicators.push(indicator);
              console.log(indicator);
              Vue.set(this,"currentIndicator", this.indicators.length -1);
          }
          this.selectIndicator();
      }
  },
  mounted: function () {
    this.fetchSchema();
    this.fetchTopics();
    this.$nextTick(function () {
          console.log('hi');
          $('[data-toggle="tooltip"]').tooltip();
          $('[data-toggle="popover"]').popover({'trigger':'focus','placement':'left'});
      })},
  methods: {
      fetchSchema: function () {
          var events = [];
          console.log("fetch schema");
          this.$http.get(getContextPath() + '/api/schema')
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
            var s = this.schema[this.currentSchema];
            console.log("fetch fields for "+ s.name);
            this.$http.get(getContextPath() + '/api/fields',{params: {'schema': s.name}})
              .then(function (request) {
                Vue.set(this, 'fields', request.body);
              })
              .catch(function (err) {
                console.err(err);
              });

            this.$http.get(getContextPath() + '/api/indicator',{params: {'schema': s.name}})
            .then(function (request) {
                console.log(JSON.stringify(request.body));
              Vue.set(this, 'indicators', request.body);
            })
            .catch(function (err) {
              console.err(err);
            });
        },
        fetchTopics: function() {
            this.$http.get(getContextPath() +"/api/topicmap/indicators")
            .then(function(request){
                Vue.set(this,"topics",request.body);
            })
            .catch(function(err) {
                console.err(err);
            });
        }
        ,selectIndicator: function() {
            var s = this.schema[this.currentSchema];
            var i = this.indicators[this.currentIndicator];
        },
          selectField: function () {
              var s = this.schema[this.currentSchema];
              var f = this.fields[this.currentField];
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
          selectFieldByName(name) {
                var s = this.schema[this.currentSchema];
                this.$http.get(getContextPath() + '/api/listDistinctValues',{params: {'schema': s.name, "field":name}})
                .then(function (request) {
                    console.log(JSON.stringify(request.body));
                  Vue.set(this, 'uniqueValues', request.body);
                })
                .catch(function (err) {
                  console.err(err);
                });
          },
          runQuery() {
              var query = this.indicators[this.currentIndicator].query;
              console.log(JSON.stringify(query));
              this.$http.post(getContextPath() + '/api/query/datastore' , JSON.stringify(query), {emulateJSON:true,
                  headers: {
                      'Content-Type': 'application/json'
                  }})
              .then(function (request) {
                  console.log(JSON.stringify(request.body));
                  Vue.set(this,"results",request.body);
              })
              .catch(function (err) {
                console.err(err);
              });
          },
          saveIndicator() {
              var indicator = this.indicators[this.currentIndicator];
              console.log(indicator);
              if (indicator.id == -1 || indicator.id == undefined) {
                  this.$http.post(getContextPath() + '/api/indicator/save' , JSON.stringify(indicator), {emulateJSON:true,
                      headers: {
                          'Content-Type': 'application/json'
                      }})
                  .then(function (request) {
                      console.log(JSON.stringify(request.body));
                      indicatorId = request.body;
                  })
                  .catch(function (err) {
                      console.log(err);
                    console.err(err);
                  });
              } else {
                  this.$http.put(getContextPath() + '/api/indicator/' + indicator.id , JSON.stringify(indicator), {emulateJSON:true,
                      headers: {
                          'Content-Type': 'application/json'
                      }})
                  .then(function (request) {
                      console.log(JSON.stringify(request.body));
                      Vue.set(this, "indicatorId", request.body);
                  })
                  .catch(function (err) {
                    console.err(err);
                  });
                  
              }

          },
    getQuery: function() {
        console.log(this.conditions);
        return this.conditions;
    }
  }
});
