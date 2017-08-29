require.config({
  baseUrl: "",
  paths: {
    'vue': '/components/vue/dist/vue.min',
    'jquery' : '/components/jquery/dist/jquery',
    'vue-resource': '/components/vue-resource/dist/vue-resource.min',
    'bootstrap': '/components/bootstrap/dist/js/bootstrap.min'
  },
  shim: {
    vue: {
      exports: 'Vue'
    },
    bootstrap : { "deps" :['jquery'] }
  }

});
require([
    'vue','jquery','vue-resource','bootstrap'
    ], function(Vue,JQuery,VueResource,Bootstrap){
  
    var Resource = require('vue-resource');
    Vue.use(Resource);

    Vue.config.errorHandler = function (err, vm) {
      console.log(err);
      console.log(vm);
   }


  Vue.component('autocomplete-input', {
      template: '#autocomplete-input-template',
      props: ["field","schema"],
      data: function() {
        return {
          isOpen: false,
          highlightedPosition: 0,
          keyword: '',
          options:[]
        }
      },
      watch: {
          field: function(fld) {
              window.console.log(fld, this.schema, this.field);
              this.$http.get(getContextPath() + '/api/listDistinctValues',{params: {'schema': this.schema,'field':this.field}})
              .then(function (request) {
                Vue.set(this, 'options', request.body);
                window.console.log(request.body);
              })
              .catch(function (err) {
                console.err(err);
              });

          }
          
      },
      computed: {
        fOptions: function() {
            window.console.log("foptions:" ,this.field);
        }
      },
      methods: {
        onInput(value) {
            window.console.log("onInput:", value);
            var re = new RegExp(value, 'i');
            var filtered = this.options.filter(o => o.value.match(re));
            window.console.log("opt:", this.options);
            window.console.log("filt:", filtered);
            Vue.set(this, 'options', filtered);
            
            this.highlightedPosition = 0
            this.isOpen = !!value
          },
          moveDown() {
            if (!this.isOpen) {
              return
            }
            this.highlightedPosition =
              (this.highlightedPosition + 1) % this.options.length
          },
          moveUp() {
            if (!this.isOpen) {
              return
            }
            this.highlightedPosition = this.highlightedPosition - 1 < 0 ? this.options.length - 1 : this.highlightedPosition - 1
          },
          focus() {
              window.console.log("onFocus:", this.keyword);
              var re = new RegExp(this.keyword, 'i');
              var filtered = this.options.filter(o => o.value.match(re));
              window.console.log("opt:", this.options);
              window.console.log("filt:", filtered);
              Vue.set(this, 'options', filtered);

              this.isOpen = true;
              
          },
          select() {
              window.console.log("onSelect:", this.keyword);
            var selectedOption = this.options[this.highlightedPosition]
            this.$emit('select', selectedOption)
            this.isOpen = false
            this.keyword = selectedOption.title
          }
      }
    });


  
  
 Vue.component('spart', {
      template: "#spart-template",
      props: ['fields', "part","rowindex","parts","onOptionSelect","schema"],
      data() {
          return {
          }
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
          onOptionSelect(option) {
              console.log('Selected option:', option)
            },
          getHtmlFieldType(name){
              if (name == undefined || name == '') {
                  return "text";
              }
              var f = this.fields[this.getFieldIndex(name)];
              if (f.type == undefined) {
                  return 'number';
              }
              if (f.type === 'FLOAT' || f.type === 'LONG'){
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
    schemaName: "",
    fields: [],
    uniqueValues: [],
    indicators: [] ,
    topics: [],
    selectedTopics: [],
    results: undefined,
    currentSchema: undefined,
    currentField: undefined,
    currentIndicator: undefined
  },
  computed: {
      cannotSubmit: function() {
          if (this.currentIndicator != undefined) {
              var ind = this.indicators[this.currentIndicator];
              console.log(ind.name);
              if (ind.name == '' || ind.name == undefined) {
                  return true;
              }
              if (this.selectedTopics.length == 0) {
                  return true;
              }
              var valid = false;
              this.selectedTopics.forEach(function(tid){
                  if (tid != '' && tid != undefined) {
                      valid = true;
                  } 
              });
              if (valid == false) {
                  return true;
              }
          }
          return false;
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
              var indicator = {name:'',query: {conditions:[{}], operator:'AND', schema: this.schema[this.currentSchema].name}, topicIdentifers:[{}]};
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
          JQuery('[data-toggle="tooltip"]').tooltip();
          JQuery('[data-toggle="popover"]').popover({'trigger':'focus','placement':'left'});
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
            Vue.set(this,"schemaName",s.name);
            Vue.set(this,"indicators",[]);
            Vue.set(this,"fields",[]);
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
            Vue.set(this,"selectedTopics",[]);

        },
        fetchTopics: function() {
            this.$http.get(getContextPath() +"/api/topicmap/indicators")
            .then(function(request){
                Vue.set(this,"topics",request.body);
            })
            .catch(function(err) {
                console.err(err);
            });
        },
        selectIndicator: function() {
            var s = this.schema[this.currentSchema];
            var i = this.indicators[this.currentIndicator];
            var idents = i.topicIdentifiers;
            if (idents == undefined || idents.length == 0) {
                idents = [];
                i.topicIdentifiers = idents;
                idents.push("");
            } 
            Vue.set(this,"selectedTopics",idents);
        },
        addTopic() {
            this.selectedTopics.push({});
        },
        removeTopic(idx) {
            this.selectedTopics.splice(idx,1);                
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
              indicator.topicIdentifiers = this.selectedTopics;
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
          deleteIndicator() {
              var indicator = this.indicators[this.currentIndicator];
              console.log(indicator);
              if (indicator.id != -1 && indicator.id != undefined) {
                  this.$http.delete(getContextPath() + '/api/indicator/' + indicator.id , JSON.stringify(indicator), {emulateJSON:true,
                      headers: {
                          'Content-Type': 'application/json'
                      }})
                  .then(function (request) {
                      console.log(JSON.stringify(request.body));
                  })
                  .catch(function (err) {
                    console.err(err);
                  });
              }
              Vue.set(this, "indicatorId", -1);
              this.indicators.splice(this.currentIndicator,1);
              Vue.set(this, "currentIndicator", undefined);
              Vue.set(this,"indicators",this.indicators);
              

          },
    getQuery: function() {
        console.log(this.conditions);
        return this.conditions;
    }
  }
});
});