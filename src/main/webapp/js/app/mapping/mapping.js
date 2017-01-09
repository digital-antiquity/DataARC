//Vue.http.options.emulateJSON = true; // send as 


// var Vue = require('vue');
  Vue.config.errorHandler = function (err, vm) {
      console.log(err);
      console.log(vm);
  }

 Vue.component('spart', {
      template: "#spart-template",
      props: ['fields', "part","rowindex","parts"],
      methods: {
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
    indicatorName: "",
    indicators: [] ,
    indicatorId: -1,
    results: undefined,
    conditions: [{}],
    selectedSchema: undefined,
    selectedField: undefined,
    selectedIndicator: undefined
  },
  computed: {
  },
  watch: {
      'selectedSchema' : function(val, oldVal) {
          this.selectSchema();
      },
      'selectedField' : function(val, oldVal) {
          this.selectField();
      },
      'selectedIndicator' : function(val, oldVal) {
          this.selectIndicator();
      }
  },
  mounted: function () {
    this.fetchSchema();
  },

  methods: {
      fetchSchema: function () {
          var events = [];
          console.log("fetch schema");
          this.$http.get('/api/schema')
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
            this.$http.get('/api/fields',{params: {'schema': s.name}})
              .then(function (request) {
//                  console.trace(JSON.stringify(request.body));
                Vue.set(this, 'fields', request.body);
              })
              .catch(function (err) {
                console.err(err);
              });

            this.$http.get('/api/indicator',{params: {'schema': s.name}})
            .then(function (request) {
                console.log(JSON.stringify(request.body));
              Vue.set(this, 'indicators', request.body);
            })
            .catch(function (err) {
              console.err(err);
            });
        },
        selectIndicator: function() {
            var s = this.schema[this.selectedSchema];
            var i = this.indicators[this.selectedIndicator];
            Vue.set(this,'conditions',i.query.conditions);
            Vue.set(this,"indicatorId", i.id);
            Vue.set(this,"indicatorName", i.name);
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
          selectFieldByName(name) {
                var s = this.schema[this.selectedSchema];
                this.$http.get('/api/listDistinctValues',{params: {'schema': s.name, "field":name}})
                .then(function (request) {
                    console.log(JSON.stringify(request.body));
                  Vue.set(this, 'uniqueValues', request.body);
                })
                .catch(function (err) {
                  console.err(err);
                });
          },
          createQuery() {
              var query = {"conditions":this.conditions, "operator": "AND"};
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
                console.err(err);
              });
          },
          saveIndicator() {
              var indicator = {name: this.indicatorName, id: this.indicatorId,
                      query: this.createQuery()};
              if (this.indicatorId == -1) {
                  
              this.$http.post('/api/indicator/save' , JSON.stringify(indicator), {emulateJSON:true,
                  headers: {
                      'Content-Type': 'application/json'
                  }})
              .then(function (request) {
                  console.log(JSON.stringify(request.body));
                  Vue.set(this, "indicatorId", request.body);
              })
              .catch(function (err) {
                  console.log(err);
                console.err(err);
              });
              } else {
                  this.$http.put('/api/indicator/' + this.indicatorId , JSON.stringify(indicator), {emulateJSON:true,
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
