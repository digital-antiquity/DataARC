//require.config({
//  baseUrl: "",
//  paths: {
//    'vue': '/components/vue/dist/vue',
//    'jquery' : '/components/jquery/dist/jquery',
//    'vue-resource': '/components/vue-resource/dist/vue-resource',
//    'bootstrap': '/components/bootstrap/dist/js/bootstrap.min',
//    "typeahead" : '/components/typeahead.js/dist/typeahead.bundle',
//    "handlebar" : 'http://twitter.github.io/typeahead.js/js/handlebars',
//  },
//  shim: {
//    vue: {
//      exports: 'Vue'
//    },
//    bootstrap : { "deps" :['jquery'] },
//    typeaead: { "deps" :['jquery'] }
//  }
//
//});
//require([
//    'vue','jquery','vue-resource','bootstrap','typeahead',"handlebar"
//    ], function(Vue,JQuery,VueResource,Bootstrap,Typeahead,Handlebars){
  
//    var Resource = require('vue-resource');
    Vue.use(VueResource);

    Vue.config.errorHandler = function (err, vm) {
      console.log(err);
      console.log(vm);
   }

    var substringMatcher = function(strs) {
        return function findMatches(q, cb) {
          var matches, substringRegex;

          // an array that will be populated with substring matches
          matches = [];

          // regex used to determine if a string contains the substring `q`
          substrRegex = new RegExp(q, 'i');

          // iterate through the pool of strings and for any string that
          // contains the substring `q`, add it to the `matches` array
          $.each(strs, function(i, str) {
              console.log(str.value);
            if (substrRegex.test(str.value)) {
              matches.push(str);
            }
          });

          cb(matches);
        };
      };
    Vue.directive('popover', function(el, binding){
        $(el).popover({
                 content: $(binding.value).html(),
                 html:true,
                 placement: binding.arg,
                 trigger: 'hover'             
             })
    })
    
    var setupTypeahead = function(el_, binding) {
        $(el_).typeahead('destroy');
        $(el_).typeahead({
            hint: true,
            highlight: true,
            minLength: 1
          },
          {
            name: 'states',
            display: 'value',
            source: substringMatcher(binding.value),
            templates: {
                suggestion: Handlebars.compile('<div><strong>{{value}}</strong> – {{occurrence}}</div>')
              }
          });    }
    Vue.directive('typeahead', {
        update : function(el_,binding){
            setupTypeahead(el_,binding);

        },
        inserted:  function(el_, binding) {
            setupTypeahead(el_,binding);
        }
    })
/**

 */    
/** https://jsfiddle.net/krn9v4vr/59/ **/

    
 Vue.component('spart', {
      template: "#spart-template",
      props: ['fields', "part","rowindex","parts","schema","operator"],
      data() {
          return {
          }
      },
      computed: {
      },
      methods: {
          onValidChange : function() {
            window.console.log("ON VALID CHANGE");
            this.$parent.runQuery();
          },
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
//              console.log('Selected option:', option);
//              this.$emit('select', option);
            },
          getHtmlFieldType(name){
              if (name == undefined || name == '') {
                  return "text";
              }
//              console.log(name, this.getFieldIndex(name));
              var f = this.fields[this.getFieldIndex(name)];
              if (f == undefined) {
                  return undefined;
              }
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
          getFieldValues: function(name) {
              var index = this.getFieldIndex(name);
              if (index == -1)  {
                  return [];
              }
              var fld = this.fields[index];
              return fld.values;
          },
          updateValue: function(value,index) {
              this.parts[index].value = value;
              this.$parent.runQuery();
          },
          addPart: function() {
              this.parts.push({type:'EQUALS'});
          },
          removePart: function(idx) {
            this.parts.splice(idx ,1);  
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
    currentIndicator: undefined,
    saveStatus: ''
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
          this.runQuery();
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
              var indicator = {name:'New Indicator',citation:'',description:'',query: {conditions:[{type:'EQUALS'}], operator:'AND', schema: this.schema[this.currentSchema].name}, topicIdentifers:[{}]};
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
      })},
  methods: {
      onValidChange() {
          this.runQuery();
      },
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
              window.console.log("RunQuery-->", JSON.stringify(query));
//              window.console.log();
              this.$http.post(getContextPath() + '/api/query/datastore' , JSON.stringify(query), {emulateJSON:true,
                  headers: {
                      'Content-Type': 'application/json'
                  }})
              .then(function (request) {
//                  console.log(JSON.stringify(request.body));
                  Vue.set(this,"results",request.body);
              })
              .catch(function (err) {
                console.err(err);
              });
          },
          resetSave() {
              Vue.set(this,"saveStatus","");
          },
          saveIndicator() {
              var indicator = this.indicators[this.currentIndicator];
              console.log(indicator);
              indicator.topicIdentifiers = this.selectedTopics;
              Vue.set(this,"saveStatus","saving...");
              if (indicator.id == -1 || indicator.id == undefined) {
                  this.$http.post(getContextPath() + '/api/indicator/save' , JSON.stringify(indicator), {emulateJSON:true,
                      headers: {
                          'Content-Type': 'application/json'
                      }})
                  .then(function (request) {
                      console.log(JSON.stringify(request.body));
                      indicatorId = request.body;
                      Vue.set(this,"saveStatus","successful");
                      setTimeout(this.resetSave, 2000);

                  })
                  .catch(function (err) {
                      console.log(err);
                      Vue.set(this,"saveStatus",err);

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
        return this.conditions;
    }
  }
//});
});