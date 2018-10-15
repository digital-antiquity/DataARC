
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
          if (q == undefined) {
            q = "";  
          } 
          
          q = q.replace(/[^a-z0-9\s]+/gi, '');

          // regex used to determine if a string contains the substring `q`
          substrRegex = new RegExp(q, 'i');

          // iterate through the pool of strings and for any string that
          // contains the substring `q`, add it to the `matches` array
          $.each(strs, function(i, str) {
// console.log(str.value);
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

          Vue.directive('jsonpretty', function(el, binding,v){
              var $el = $(el);
              $el.click(function() {
                  var $next = $el.next();
                  $next.toggleClass('hidden');
                  if (!$next.hasClass('hidden')) {
                      var name = "#results-template-"+ v.context._self.schema[v.context._self.currentSchema].id;
                      var data = JSON.parse(JSON.stringify(binding.value));
                      $next.html(getContent(name, data));
                  }
              })
      });
          
      var getContent = function(name,data) {
          var tmpl = $(name).html();
          var template = Handlebars.compile(tmpl);
// var val = template(data);
          return JSON.stringify(data);
      }
      

    Vue.component('selectize', {
        props: ['options', 'value'],
        template: '<select><slot></slot></select>',
        mounted: function () {
          var vm = this;
          var opt = $.extend({},$(this.$el).data());
          if (this.options != null) {
              opt.options = this.options;
          }
          
          this.sel = $(this.$el).selectize(opt)
              .on("change",function(){
                vm.$emit('input', vm.sel.getValue());
              })[0].selectize;
          this.sel.setValue(this.value,true);
          this.sel.setTextboxValue(this.value);
          console.trace("setValue mounted:", this.value);
        },
        watch: {
          value: function (value) {
              Vue.set(this,"value",value);
              this.sel.setValue(value,true);
              this.sel.setTextboxValue(value,true);
              console.trace("setValue value:", value);
          },
          options: function (options) {
// var val = this.sel.getValue();
              this.sel.clearOptions();
              this.sel.addOption(options);
              this.sel.refreshOptions(false);
// this.sel.setValue(val, true);
// console.log("setValue options:", val);
          }
        },
        destroyed: function () {
          this.sel.destroy();
        }
      })


    /** https://jsfiddle.net/krn9v4vr/59/ * */

    
 Vue.component('spart', {
      template: "#spart-template",
      props: ['fields', "part","rowindex","parts","schema","operator"],
      data: function() {
          return {
          }
      },
      methods: {
          onValidChange : function() {
            window.console.log("ON VALID CHANGE");
            this.$emit("run-query");
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
          getHtmlFieldType: function(name){
              if (name == undefined || name == '') {
                  return "text";
              }
// console.log(name, this.getFieldIndex(name));
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
          setValue: function(val) {
// console.log('setValue:' + val);
              this.parts[this.rowindex].value = val;
              Vue.set(this,'part.value',val);
              this.$emit("run-query");
//              this.$parent.runQuery();
          },
          addPart: function() {
              this.parts.push({type:'EQUALS',value:'',fieldName:'',secondaryFieldName:undefined,compare:false});
          },
          showCompare : function() {
              this.parts[this.rowindex].compare = true;
              this.parts[this.rowindex].value = '';
          },
          hideCompare : function() {
              this.parts[this.rowindex].compare = false;
              this.parts[this.rowindex].secondaryFieldName = undefined;
          },
          removePart: function(idx) {
            this.parts.splice(idx ,1);
            this.$emit("run-query");
          },
          typechange: function() {
// console.log('catch typechange');
          },
          updateTest: function(v) {
              this.parts[this.rowindex].value = v;
              this.$forceUpdate();
              this.$emit("run-query");
//              this.$parent.runQuery();
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
    query: "",
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
              // console.log(ind.name);
              if (ind.name == '' || ind.name == undefined) {
                  return true;
              }
// console.log(JSON.stringify(this.selectedTopics));
              if (this.selectedTopics.length == 0) {
                  return true;
              }
              var valid = false;
              this.selectedTopics.forEach(function(tid){
                  if (tid != '' && tid != undefined && !jQuery.isEmptyObject(tid)) {
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
              var indicator = {name:'New Indicator',citation:'',description:'',query: {conditions:[{type:'EQUALS',value:'',compare:false}], operator:'AND', schema: this.schema[this.currentSchema].name}, topicIdentifiers:[{}]};
              this.indicators.push(indicator);
              console.log(indicator);
              Vue.set(this,"currentIndicator", this.indicators.length -1);
          }
          this.selectIndicator();
      }
  },
  mounted: function () {
    this.fetchSchema();
    this.$nextTick(function () {
      })},
  methods: {
      onValidChange: function() {
          this.runQuery();
      },
      fetchSchema: function () {
          var events = [];
          console.log("fetch schema");
          var self = this;
          axios.get(getContextPath() + '/api/schema')
            .then(function (schema) {
                var list = new Array();
                schema.data.forEach(function(s) {
                    list.push(s);
                });
                console.log(list);
              Vue.set(self, 'schema', list);
            })
            .catch(function (err) {
                console.log(err);
                //Rollbar.error(err);
            });
        },
        selectSchema: function () {
            var s = this.schema[this.currentSchema];
            Vue.set(this,"schemaName",s.name);
            Vue.set(this,"indicators",[]);
            Vue.set(this,"fields",[]);
            Vue.set(this,"results",undefined);
            console.log("fetch fields for "+ s.name);
            var self = this;
            axios.get(getContextPath() + '/api/fields',{params: {schema: s.name}})
              .then(function (request) {
                  var fields = request.data;
                  function compare(a,b) {
                      if (a.displayName < b.displayName)
                        return -1;
                      if (a.displayName > b.displayName)
                        return 1;
                      return 0;
                    }
//                  console.log(fields);
                    fields.sort(compare);
                    Vue.set(self, 'fields', fields);
                    
                    

              })
              .catch(function (err) {
                Rollbar.error("error getting fields", err);
              });

            axios.get(getContextPath() + '/api/indicator',{params: {schema: s.name}})
            .then(function (request) {
//                console.log(JSON.stringify(request.data));
                console.log("indicators:", request);
                Vue.set(self, 'indicators', request.data);
            })
            .catch(function (err) {
                Rollbar.error("error getting indicators", err);
            });
            Vue.set(this,"selectedTopics",[]);
            this.fetchTopics();

        },
        fetchTopics: function() {
            var s = this.schema[this.currentSchema];
            axios.get(getContextPath() +"/api/topicmap/indicators", {params: {schemaId: s.id}})
            .then(function(request){
                Vue.set(this,"topics",request.body);
            })
            .catch(function(err) {
                Rollbar.error("error getting topics", err);
            });
        },
        selectIndicator: function() {
            var s = this.schema[this.currentSchema];
            var i = this.indicators[this.currentIndicator];
            if (i != undefined) {
                var idents = i.topicIdentifiers;
                if (idents == undefined || idents.length == 0) {
                    idents = [];
                    i.topicIdentifiers = idents;
                    idents.push("");
                } 
                Vue.set(this,"selectedTopics",idents);
                this.runQuery();
            }
        },
        addTopic: function() {
            this.selectedTopics.push({});
        },
        removeTopic: function(idx) {
            this.selectedTopics.splice(idx,1);
            if (this.selectedTopics.length == 0) {
                this.selectedTopics.push({});
            }
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
          selectFieldByName: function(name) {
                var s = this.schema[this.currentSchema];
                axios.get(getContextPath() + '/api/listDistinctValues',{params: {schema: s.name, field:name}})
                .then(function (request) {
                    console.log(JSON.stringify(request.body));
                  Vue.set(this, 'uniqueValues', request.body);
                })
                .catch(function (err) {
                    Rollbar.error("error getting unique field values", err);
                });
          },
          runQuery: function() {
              var query = this.indicators[this.currentIndicator].query;
              var qs = JSON.stringify(query);
              console.log(query, qs);
              if (this.query == qs) {
                  return;
              }
              this.query = qs;
              window.console.log("RunQuery-->", qs);
              
              var self = this;
              if (self.cancelToken != undefined) {
                  self.cancelToken.cancel();
              }
              var token = axios.CancelToken.source();
              Vue.set(self, "cancelToken" ,token);
              
              axios.post(getContextPath() + '/api/query/datastore', qs,{ headers: {'Content-Type':"application/json"},cancelToken: token.token }).then(function(res) {
                  console.log(res);
                  Vue.set(self, 'results',res.data);

              }).catch(function(thrown) {
                  if (!axios.isCancel(thrown)) {
                      console.error(thrown);
                  } else {
                      Rollbar.error("error running mongo search", err);
                  }
              });
          },
          resetSave: function() {
              Vue.set(this,"saveStatus","");
          },
          saveIndicator: function() {
              var indicator = this.indicators[this.currentIndicator];
              console.log(indicator);
              indicator.topicIdentifiers = [];
              this.selectedTopics.forEach(function(topic){
                  if (topic != undefined && topic != '' && !jQuery.isEmptyObject(topic)) {
                      indicator.topicIdentifiers.push(topic);
                  }
              });
              
              Vue.set(this,"saveStatus","saving...");
              var json = JSON.stringify(indicator);
              if (indicator.id == -1 || indicator.id == undefined) {
                  axios.post(getContextPath() + '/api/indicator/save' , json, {headers: {'Content-Type':"application/json"}})
                  .then(function (request) {
                      console.log("indicator result: " + JSON.stringify(request.body));
                      indicatorId = request.body;
                      indicator.id = indicatorId;
                      Vue.set(this, "indicatorId", request.body);
                      Vue.set(this,"saveStatus", "successful ["+new Date()+"]");
                  })
                  .catch(function (err) {
                      Rollbar.error("error saving indicator: " + json, err);
                      Vue.set(this,"saveStatus",err  + " ["+new Date()+"]");

                      Rollbar.errors(err);
                  });
              } else {
                  axios.put(getContextPath() + '/api/indicator/' + indicator.id , json, { headers: {'Content-Type':"application/json"} })
                  .then(function (request) {
                      console.log(JSON.stringify(request.body));
                      Vue.set(this,"saveStatus", "successful ["+new Date()+"]");
                      Vue.set(this, "indicatorId", request.body);
                      indicator.id = request.body;
                  })
                  .catch(function (err) {
                      Vue.set(this,"saveStatus",err);
                      Rollbar.error("error saving indicator: " + json, err);
                  });
                  
              }

          },
          deleteIndicator: function() {
              var indicator = this.indicators[this.currentIndicator];
              console.log(indicator);
              if (indicator.id != -1 && indicator.id != undefined) {
                  axios.delete(getContextPath() + '/api/indicator/' + indicator.id , indicator,{ headers: {'Content-Type':"application/json"} })
                  .then(function (request) {
                      console.log(JSON.stringify(request.body));
                  })
                  .catch(function (err) {
                      Rollbar.error("error deleting indicator: " + indicator.id, err);
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
// });
});